---
status: fixed
files_reviewed: 69
depth: deep
findings:
  critical: 0
  warning: 0
  info: 0
  total: 0
---

# Code Review Report: carryon

## Summary

> Implementation status: All findings in this review have been addressed in the working tree. Additional hardening has also been implemented for financial request validation, globally capped query pagination, and DB-backed health checks. Driver online-hours tracking now persists online sessions; OTP attempt limiting is implemented in-memory and should move to Redis or DB before multi-instance deployment.

The carryon backend (Express/Prisma) and frontend (Next.js) show solid architectural foundations—Prisma prevents SQL injection, atomic `updateMany` is used for driver claims, and Stripe webhooks are ingested via a retry-loop inbox. However, a recurring pattern of **non-atomic financial mutations** and a **generic status-update endpoint that bypasses the delivery lifecycle state machine** introduce critical correctness and security flaws. The frontend admin proxy has a path-traversal vulnerability, and the Stripe webhook route falls back to unverified events when a secret is missing.

---

## Critical Issues

### CR-01: User can mark booking DELIVERED without OTP or proof of delivery

- **Status**: Done

- **File**: `backend/src/routes/booking.routes.js:296-351`
- **Severity**: Critical
- **Description**: `PUT /api/bookings/:id/status` allows a user to transition a booking from `ARRIVED_AT_DROP` to `DELIVERED`. This completely bypasses `deliveryLifecycle.js`, which enforces recipient OTP verification and proof-of-delivery (photo/recipient name). A malicious client can self-complete a delivery and trigger settlement without handoff verification.
- **Fix**: Remove `DELIVERED` from the allowed statuses on the user-facing generic status endpoint. Delivery completion must be exclusively handled by `POST /api/bookings/:id/verify-delivery` (user) or the driver lifecycle command.

### CR-02: User cancellation via PUT status sets paymentStatus to REFUNDED without issuing wallet credit

- **Status**: Done

- **File**: `backend/src/routes/booking.routes.js:323-325`
- **Severity**: Critical
- **Description**: When a user sends `status: 'CANCELLED'` to the generic `PUT /:id/status` endpoint, the code updates `paymentStatus` to `'REFUNDED'` but never calls `refundBooking`. The user loses their wallet balance, and because the booking is now `CANCELLED`, the dedicated `POST /:id/cancel` endpoint rejects any retry (`canUserCancel` returns false).
- **Fix**: Remove the `CANCELLED` case from the generic status endpoint. User cancellation must always flow through `POST /:id/cancel`, which handles the wallet refund.

### CR-03: Booking cancellation refund is not atomic with status update

- **Status**: Done

- **File**: `backend/src/routes/booking.routes.js:417-458`
- **Severity**: Critical
- **Description**: `POST /:id/cancel` commits the booking status to `CANCELLED` inside a Prisma transaction, then calls `refundBooking` afterwards. If the refund throws (e.g., DB deadlock, network issue), the booking remains cancelled with `paymentStatus: 'COMPLETED'` and the user is never refunded.
- **Fix**: Move the refund logic (wallet increment + transaction record + `paymentStatus` update) inside the same Prisma transaction that updates the booking status.

### CR-04: Path traversal in admin proxy allows SSRF to non-admin backend routes

- **Status**: Done

- **File**: `frontend/src/app/api/admin/[...path]/route.ts:25-27`
- **Severity**: Critical
- **Description**: The catch-all handler builds the target URL with `params.path.join("/")`. If a path segment is `..`, the `URL` constructor resolves it (e.g., `/api/admin/../auth/send-otp` → `/api/auth/send-otp`). The request is forwarded with the `x-admin-key` header, but it can reach non-admin routes, turning the Next.js frontend into an open proxy to the backend.
- **Fix**: Reject any path segment that is `.` or `..` before constructing the URL, and assert the resolved pathname still starts with `/api/admin/`.

### CR-05: Stripe webhook signature verification is optional, enabling event spoofing

- **Status**: Done

- **File**: `backend/src/routes/stripe-webhook.routes.js:12-18`
- **Severity**: Critical
- **Description**: If `STRIPE_WEBHOOK_SECRET` is not set, the route skips `constructEvent` and trusts `req.body` directly. An attacker can craft a fake `payment_intent.succeeded` event and credit their wallet without ever paying.
- **Fix**: Refuse to process the webhook if `STRIPE_WEBHOOK_SECRET` is missing. Return 500 and log a fatal configuration error.

### CR-06: Driver withdrawal crash leaves wallet deducted with no recovery

- **Status**: Done

- **File**: `backend/src/routes/driver-payouts.routes.js:136-169`
- **Severity**: Critical
- **Description**: The route deducts the withdrawal amount from the driver's wallet inside a transaction, then attempts a Stripe transfer. If the Node process crashes between the transaction commit and the Stripe response, the payout record stays in `PENDING` and the wallet remains decremented. There is no reconciliation job to recover or reverse stale `PENDING` payouts.
- **Fix**: Implement a background reconciliation loop (similar to `webhookInbox.js`) that queries `PENDING` payouts older than N minutes, verifies their state with Stripe, and either finalizes them or refunds the wallet.

---

## Warnings

### WR-01: OTP generation uses non-cryptographic Math.random()

- **Status**: Done

- **File**: `backend/src/lib/otp.js:10`, `backend/src/services/deliveryOtp.js:28-30`, `backend/src/services/bookingLifecycle.js:207`
- **Severity**: Warning
- **Description**: `Math.random()` is not cryptographically secure. For short numeric OTPs (4–6 digits), predictable generation weakens the security model.
- **Fix**: Use `crypto.randomInt(10 ** (n - 1), 10 ** n)` from Node.js `crypto`.

### WR-02: Memory leak in live tracking WebSocket rooms

- **Status**: Done

- **File**: `backend/src/services/liveTracking.js:9-15`
- **Severity**: Warning
- **Description**: The global `rooms` Map creates a new `Set` for each `bookingId`, but empty Sets are never removed when the last socket disconnects.
- **Fix**: In the socket `close` handler, delete the Map entry if `joinedRoom.size === 0`.

### WR-03: Chat unread count endpoint lacks booking ownership check

- **Status**: Done

- **File**: `backend/src/routes/chat.routes.js:68-82`
- **Severity**: Warning
- **Description**: `GET /api/chat/:bookingId/unread` counts unread messages without verifying the user owns the booking. Any authenticated user can poll unread counts for arbitrary bookings.
- **Fix**: Add a `prisma.booking.findFirst` guard to ensure `booking.userId === req.user.userId`.

### WR-04: Pickup OTP verification lacks rate limiting

- **Status**: Done

- **File**: `backend/src/routes/driver-jobs.routes.js:283-296`
- **Severity**: Warning
- **Description**: The 4-digit pickup OTP can be brute-forced because the verification endpoint has no rate limiting beyond generic IP-based auth limits.
- **Fix**: Add a per-booking or per-driver attempt counter (e.g., in Redis or DB) and reject after 5 failed attempts.

### WR-05: Missing coordinate validation defaults to null island (0,0)

- **Status**: Done

- **File**: `backend/src/routes/booking.routes.js:93-116`
- **Severity**: Warning
- **Description**: Address creation falls back to `latitude: 0, longitude: 0` when coordinates are missing, creating invalid geocoded addresses.
- **Fix**: Reject the request if coordinates are missing or non-finite instead of defaulting to zero.

### WR-06: Widespread code duplication

- **Status**: Done

- **Files**: `backend/src/lib/distance.js` & `backend/src/services/demandZones.js` (`haversineKm`); `backend/src/lib/money.js`, `backend/src/services/bookingLifecycle.js`, `backend/src/services/bookingPricing.js` (`money()`); `backend/src/routes/auth.routes.js` & `backend/src/services/deliveryOtp.js` (`maskEmail`, `getSupabaseAdmin`); `backend/src/routes/upload.routes.js`, `backend/src/routes/driver-documents.routes.js`, `backend/src/routes/driver-upload.routes.js` (Supabase client setup)
- **Severity**: Warning
- **Description**: Same logic is copy-pasted across files, increasing maintenance burden and drift risk.
- **Fix**: Extract shared helpers into `lib/` modules (e.g., `lib/supabase.js`, `lib/maskEmail.js`).

### WR-07: Vehicle type is not validated against allowed types

- **Status**: Done

- **File**: `backend/src/routes/driver-vehicle.routes.js:10-34`
- **Severity**: Warning
- **Description**: The endpoint accepts any `type` string and stores it. Invalid types break downstream dispatch vehicle-matching logic.
- **Fix**: Validate `type` against `VALID_VEHICLE_TYPES` from `businessConfig.js`.

### WR-08: Promo apply over-increments coupon usedCount

- **Status**: Done

- **File**: `backend/src/routes/promo.routes.js:91-105`
- **Severity**: Warning
- **Description**: `coupon.usedCount` is incremented unconditionally on every apply call, even if the user had already applied the coupon.
- **Fix**: Only increment `usedCount` when the `userCoupon` upsert actually creates a new record (use the `create` / `update` branches).

### WR-09: Driver auth middleware logs JWT signing key material

- **Status**: Done

- **File**: `backend/src/middleware/driverAuth.js:13,16,22,34`
- **Severity**: Warning
- **Description**: Console logs include the JWKS URI, signing key `kid`, and token prefixes, which may leak sensitive metadata in production logs.
- **Fix**: Remove or downgrade these logs to `debug` level, and never log key identifiers or token fragments in production.

### WR-10: Google Maps API key exposed to all authenticated users

- **Status**: Done

- **File**: `backend/src/routes/location.routes.js:69-79`
- **Severity**: Warning
- **Description**: `GET /api/location/map-config` returns the server-side `GOOGLE_MAPS_API_KEY` to any authenticated user.
- **Fix**: Return only a client-restricted key (or omit the endpoint) and enforce strict referrer restrictions in the Google Cloud console.

### WR-11: Admin ride request does not limit driverIds array size

- **Status**: Done

- **File**: `backend/src/routes/admin-notifications.routes.js:52-188`
- **Severity**: Warning
- **Description**: The `driverIds` array from the request body is passed directly into Prisma `in` clauses and notification creation. An extremely large array can degrade performance or hit query limits.
- **Fix**: Cap `driverIds.length` to a reasonable maximum (e.g., 100) and return 400 if exceeded.

### WR-12: Tip deduction race condition may overdraw wallet

- **Status**: Done

- **File**: `backend/src/routes/rating.routes.js:67-87`
- **Severity**: Warning
- **Description**: Wallet balance is checked outside a transaction, then decremented inside a transaction without re-checking. Concurrent tip requests could overdraw the wallet.
- **Fix**: Perform the balance check and decrement inside the same transaction, or use an atomic decrement with a post-check.

---

## Info / Suggestions

### IN-01: Unconditional Morgan dev logger

- **Status**: Done

- **File**: `backend/src/app.js:27`
- **Severity**: Info
- **Fix**: Use `morgan(process.env.NODE_ENV === 'production' ? 'combined' : 'dev')`.

### IN-02: Hardcoded company details in invoice endpoint

- **Status**: Done

- **File**: `backend/src/routes/invoice.routes.js:143-150`
- **Severity**: Info
- **Fix**: Move company metadata to environment variables or a configuration table.

### IN-03: TODO comment for unimplemented feature

- **Status**: Done

- **File**: `backend/src/routes/driver-earnings.routes.js:46`
- **Severity**: Info
- **Fix**: Implement online-hours tracking or remove the placeholder comment.

### IN-04: ETA field accepts any type without validation

- **Status**: Done

- **File**: `backend/src/routes/booking.routes.js:318-319`
- **Severity**: Info
- **Fix**: Validate that `eta` is a positive integer before persisting.

### IN-05: Duplicate image upload logic across three routes

- **Status**: Done

- **Files**: `backend/src/routes/upload.routes.js`, `backend/src/routes/driver-documents.routes.js`, `backend/src/routes/driver-upload.routes.js`
- **Severity**: Info
- **Fix**: Extract a shared `uploadToSupabase(bucket, file, path)` helper.

### IN-06: Dead code `serverQuote` function

- **Status**: Done

- **File**: `backend/src/services/bookingLifecycle.js:75-94`
- **Severity**: Info
- **Fix**: Remove the unused `serverQuote` export.

---

## Cross-Cutting Concerns

1. **State-machine bypass**: The generic `PUT /api/bookings/:id/status` endpoint allows users to drive the booking state machine directly, bypassing `deliveryLifecycle.js` guards. This is the root cause of CR-01 and CR-02. All status transitions that have side effects (refunds, OTP checks, earnings credit) should be handled by dedicated lifecycle commands, not a generic update route.

2. **Non-atomic financial operations**: Wallet mutations (cancellations, tips, withdrawals) are frequently performed outside the primary transaction or in separate transactions. This creates windows for partial failure and financial inconsistency. Every flow that moves money should debit/credit inside the same transaction that records the business event.

3. **Missing rate limiting on sensitive operations**: OTP verification (pickup and delivery) and some admin batch endpoints do not have per-resource rate limiting, leaving them open to brute-force or abuse.

---

## Positive Findings

- **Prisma ORM** is used consistently, eliminating SQL injection risks across the entire backend.
- **Atomic claim pattern**: Driver job acceptance uses `updateMany` with `status: 'SEARCHING_DRIVER', driverId: null`, preventing race-condition double-claims.
- **Idempotency keys** are implemented for booking creation, protecting against duplicate bookings on retries.
- **Webhook inbox with retry loop**: Stripe events are recorded and processed with exponential backoff, providing resilience against transient failures.
- **Clean auth separation**: Distinct middleware for user (`auth.js`), driver (`driverAuth.js`), and admin (`adminAuth.js`) keeps authorization logic clear and auditable.

---

_Reviewed: 2026-04-30_
_Reviewer: gsd-code-reviewer_
_Depth: deep_
