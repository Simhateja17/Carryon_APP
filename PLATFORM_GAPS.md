# CarryOn — Platform Gaps & Action Items

> **Audit Date:** 2026-04-27
> **Business Model:** Pure Marketplace (Uber-for-Parcels)
> **Platform Commission:** 12% (Driver keeps 88%)
> **Payment Model:** Wallet-only (no COD, no card-at-checkout)
> **Scope:** Customer app (KMP), Driver app (KMP), Admin dashboard (Next.js), Backend (Node/Express/Prisma)

This is the single source of truth for all missing features, known issues, and planned fixes. Review weekly. Move shipped items to the **FIXED** section at the bottom.

---

## TIER 0 — FIX BEFORE NEXT DRIVER ONBOARDING

These gaps directly hurt driver earnings, create exploit vectors, or break core UX. Fix them immediately.

### 1. Pricing Uses Straight-Line Distance (Revenue Leakage) — FIXED
- **Severity:** CRITICAL
- **Status:** FIXED 2026-04-28 — backend quote now uses route distance, vehicle base pricing, fallback road-distance estimation, and stores authoritative booking distance.
- **File:** `backend/src/services/bookingLifecycle.js` → `serverQuote()`
- **Problem:** `haversineKm()` calculates crow-flies distance. Real roads are 20–40% longer. You undercharge on every trip.
- **Impact:**
  - Drivers get paid less than actual driving distance → churn
  - Platform takes 12% of an already underpriced fare → lower GMV
  - Short trips may be unprofitable because `Vehicle.basePrice` exists in schema but is **never applied**
- **Fix:**
  1. Integrate Google Maps Distance Matrix API (or OSRM)
  2. Apply `basePrice` from `Vehicle` table in quote
  3. Add fallback: `haversine * 1.3` if API fails
  4. Store actual route distance in `Booking.distance`
- **Recommended Architecture (Backend as Single Source of Truth):**
  1. Create `POST /api/bookings/quote` endpoint
  2. Frontend sends: pickup lat/lng, delivery lat/lng, vehicle type
  3. Backend calls Google Maps Distance Matrix API (or OSRM)
  4. Backend calculates: `basePrice + (distance × ratePerKm) + tax`
  5. Backend returns: `{ distance, duration, price, breakdown }`
  6. Frontend displays this price — no independent calculation
  7. On booking confirm, backend already has validated distance/price
  8. Add caching: same route within 10 min = use cached distance
  9. Fallback: `haversine × 1.3` if Google API fails
- **Why not trust frontend distance?**
  - Security: malicious users could tamper with `estimatedPrice` in the API request
  - Consistency: frontend and backend must agree on the exact fare
  - Audit: `Booking` record must store the definitive, backend-verified price

### 2. Zero Cancellation Fees (Abuse Magnet) — OPEN ITEM
- **Severity:** CRITICAL
- **File:** `backend/src/routes/booking.routes.js` → `POST /:id/cancel`
- **Problem:** Customers get 100% refund regardless of when they cancel. Driver gets zero compensation for wasted fuel/time.
- **Impact:**
  - Customers will book multiple options and cancel freely
  - Drivers abandon platform after driving to pickups for nothing
  - No revenue protection
- **Open Question:** Who receives the cancellation fee? Platform, driver, or split?
- **Fix:**
  1. Tiered cancellation policy:
     - Before driver assigned → Full refund
     - After driver assigned → 10% fee + RM 2 driver compensation
     - After driver arrives → 25% fee + RM 5 driver compensation
     - After pickup → No cancellation (support-only)
  2. Add `cancellationFee`, `cancelledBy`, `cancelReason` to schema
  3. Compensate driver via `DriverWalletTransaction` (type: `CANCELLATION_COMPENSATION`)

### 3. No SOS / Panic Button — OPEN ITEM
- **Severity:** CRITICAL (Legal)
- **Status:** Completely missing from both apps
- **Impact:** If a driver or customer is in danger, there is no in-app emergency feature. Liability exposure.
- **Open Question:** Direct 999 dial vs. platform ops middleman? Same behavior for customer and driver apps?
- **Fix:**
  - SOS button in customer app and driver app
  - Press triggers: call emergency services, share live location to emergency contact + platform ops
  - Auto-create support ticket with location log

### 4. No Idempotency on Booking Creation — FIXED
- **Severity:** HIGH
- **Status:** FIXED 2026-04-28 — `Idempotency-Key` is required on booking creation; duplicate keys return the original booking without a second wallet debit.
- **File:** `backend/src/routes/booking.routes.js` → `POST /`
- **Problem:** Network retry can create duplicate bookings and double-charge wallet.
- **Decision:** Client-generated UUID (same pattern Uber uses). Future-proof: can swap Postgres for Redis later without changing API contract.
- **Fix:**
  1. Client generates UUID when user taps "Confirm Booking"
  2. Sends `Idempotency-Key: <uuid>` header on `POST /api/bookings`
  3. Backend stores `(key, bookingId, expiresAt)` for 24 hours
  4. On duplicate key → return original booking (201, idempotent success)
  5. **Merge wallet payment into booking creation** — single atomic call covers booking + payment
  6. When scaling: swap Postgres `IdempotencyKey` table for Redis, keep same header contract

### 5. No Audit Log = No Accountability — FIXED
- **Severity:** HIGH
- **Status:** FIXED 2026-04-28 — minimum viable audit log records critical booking, wallet, driver verification, document review, and admin booking mutations.
- **Impact:** If a refund happens, a price changes, or a driver is approved — you cannot answer "who did this and when?"
- **Decision:** Minimum viable audit log. Log only critical business events, not every API call or page view.
- **Fix:**
  - `AuditLog` table: `actorId`, `action`, `entityType`, `entityId`, `oldValue`, `newValue`, `timestamp`
  - **Log these events (minimum set):**
    1. Booking status changes (created, assigned, cancelled, completed)
    2. Price changes (who edited fare, old vs new amount)
    3. Wallet transactions (top-ups, payouts, refunds, penalties)
    4. Driver approval / suspension / document rejection
    5. Admin dashboard actions (any mutation via admin panel)
  - **Do NOT log:** Page views, app opens, search queries, chat messages (those live in their own tables)

---

## TIER 1 — FIX BEFORE SCALE (100+ trips/day)

### 6. Broadcast Dispatch Creates Thundering Herd — OPEN ITEM
- **Severity:** HIGH
- **File:** `backend/src/services/dispatch.js` → `notifyNearbyDrivers()`
- **Problem:** Notifies **all** online drivers within 10km at once. Race conditions, frustrated drivers, meaningless acceptance metrics.
- **Open Question:** What batch size and wait time? 3 drivers / 15 seconds? When to switch from broadcast to batched?
- **Fix:**
  1. Batched dispatch: notify top 3 closest drivers, wait 15s
  2. Rank by: proximity (40%) + rating (20%) + acceptance rate (20%) + completion rate (20%)
  3. Expand batch if no acceptance
  4. Track `acceptanceRate` per driver; penalize chronic rejectors

### 7. No Surge Pricing = No Supply Incentive — OPEN ITEM
- **Severity:** HIGH
- **File:** `backend/src/services/businessConfig.js`
- **Problem:** Flat rate card regardless of demand. At 6pm in rain with 50 bookings and 3 drivers, price stays the same.
- **Open Question:** Do we want surge pricing at all? If yes: manual toggle or auto algorithm?
- **Fix:**
  1. Zone-based heatmap (hexagons or polygons)
  2. Surge multiplier = `f(openBookings, onlineDrivers, avgWaitTime)`
  3. Cap at 3.0x
  4. Show "High demand — 1.5x fare" to customer before confirmation
  5. Show "Surge active — earn 1.5x" to driver

### 8. Scheduled Bookings Are Ghosts — OPEN ITEM
- **Severity:** HIGH
- **File:** `schema.prisma` → `Booking.scheduledTime` exists
- **Problem:** `scheduledTime` is stored but never dispatched. A booking for tomorrow sits in `SEARCHING_DRIVER` forever.
- **Open Question:** How early to dispatch scheduled bookings? Who handles them (any driver vs. opt-in drivers)?
- **Fix:**
  - Cron job opens scheduled bookings 45 min before `scheduledTime`
  - Early dispatch to drivers who opt into scheduled jobs
  - Customer reminder push 30 min before pickup

### 9. No Real-Time Tracking (WebSocket) — FIXED
- **Severity:** HIGH
- **Status:** FIXED 2026-04-28 — tracking WebSocket rooms are available per booking, driver location updates broadcast from backend position updates, and customer tracking falls back to polling on disconnect.
- **Impact:** Customer tracking is jerky and battery-heavy.
- **Decision:** Yes, implement Socket.io live tracking.
- **Fix:**
  - Socket.io or WebSocket room per `bookingId`
  - Driver emits location every 5s while on job
  - Customer subscribes to room for smooth animation
  - Fallback to polling on disconnect

### 10. No Driver Background Checks — OPEN ITEM
- **Severity:** HIGH
- **Status:** KYC docs uploaded but no verification partner
- **Impact:** You are trusting self-uploaded photos. No criminal record check.
- **Open Question:** Do we want criminal background checks? If yes, which provider and who pays?
- **Fix:**
  - Integrate background check API (local police clearance / Checkr)
  - `backgroundCheckStatus` field on Driver
  - Block onboarding until cleared; annual re-check

### 11. No Item Insurance / Claims Workflow — OPEN ITEM
- **Severity:** HIGH
- **Status:** Missing
- **Impact:** Driver breaks a TV? Package lost? Zero process. Customers will chargeback and post on social media.
- **Open Question:** Who pays for lost/damaged items? Platform, customer (insurance), or driver?
- **Fix:**
  - Add `declaredValue` and `insuranceOptIn` to booking flow
  - Insurance premium = 1% of declared value
  - Claims form with photo evidence
  - Admin claims review page

### 12. No Route-Based Distance in Frontend Quote — FIXED
- **Severity:** HIGH
- **Status:** FIXED 2026-04-28 — frontend displays backend quote results and sends idempotent booking creation; backend ignores caller-provided distance/price authority.
- **File:** `composeApp/.../RequestForRideScreen.kt`
- **Problem:** Frontend uses `LocationApi.calculateRoute()` (good), but backend `serverQuote()` uses `haversineKm()` (bad). Price mismatch possible.
- **Decision:** Backend is the single source of truth for pricing. Frontend displays only what backend returns.
- **Fix:**
  1. Backend calls Google Maps Distance Matrix API (or OSRM) in `serverQuote()`
  2. Create `POST /api/bookings/quote` endpoint
  3. Frontend sends pickup/delivery coordinates + vehicle type
  4. Backend returns validated `{ distance, duration, price, breakdown }`
  5. Frontend displays this price — no independent calculation
  6. On booking confirm, backend already has the real distance/price

---

## TIER 2 — TRUST, SAFETY & OPERATIONS

### 13. Wallet-Only Payments (Accepted Design Choice — Friction Remains)
- **Severity:** MEDIUM (intentional)
- **File:** `backend/src/routes/booking.routes.js` → `POST /`
- **Decision:** Platform deliberately does **NOT** support `CASH`, `CARD`, or `UPI`. All bookings are paid from pre-topped-up wallet.
- **Rationale:** Eliminates driver cash handling, COD fraud, and simplifies reconciliation.
- **Remaining Risk:** First-time users must top up before first booking → significant drop-off.
- **Mitigation:**
  1. Signup wallet bonus (e.g., RM 5 free credit)
  2. Inline top-up at checkout (already implemented — shows bottom sheet on 402)
  3. Consider removing `CASH`, `CARD`, `UPI` from schema enums if permanently unsupported

### 14. No Driver Incentives / Quests — OPEN ITEM
- **Severity:** MEDIUM-HIGH
- **Status:** Missing
- **Impact:** Drivers have no reason to drive more hours or accept low-paying trips.
- **Open Question:** Do we want driver bonuses/quests at all? If yes, what types and when to launch?
- **Fix:**
  - Quest engine: "Complete 10 trips this weekend → RM 50 bonus"
  - Peak-hour guarantee: "Earn min RM 15/hr 5–9pm"
  - Schema: `DriverIncentive`, `DriverQuestProgress`

### 15. No Driver Penalty System — OPEN ITEM
- **Severity:** MEDIUM-HIGH
- **Status:** Missing
- **Impact:** Drivers can cancel, no-show, or damage goods with zero consequences.
- **Open Question:** What penalties for driver misbehavior? Fine amount? Strike system? Immediate ban?
- **Fix:**
  - Deduction codes: `LATE_ARRIVAL`, `NO_SHOW`, `DAMAGED_GOODS`, `RUDE_BEHAVIOR`
  - Fine deducted from driver wallet
  - 3 strikes = temporary suspension
  - Appeal workflow

### 16. No Content Moderation on Chat — OPEN ITEM
- **Severity:** MEDIUM
- **Status:** Chat messages unfiltered
- **Impact:** PII sharing (phone numbers), abuse, threats — all unmonitored.
- **Open Question:** Do we want to moderate chat messages? What level of filtering?
- **Fix:**
  - Regex scan for phone numbers / emails → block or mask
  - Profanity filter
  - Report message button
  - Auto-suspend for repeated abuse

### 17. Driver Document Expiry Not Tracked — OPEN ITEM
- **Severity:** MEDIUM
- **Status:** `DriverDocument` has no `expiryDate`
- **Impact:** Driver with expired insurance stays online. Accident = platform liability.
- **Open Question:** Do we want auto-offline when documents expire? Who handles manual checks?
- **Fix:**
  - Add `expiryDate` to `DriverDocument`
  - Auto-notification 30 days before expiry
  - Auto-offline on expiry
  - Admin dashboard alert

### 18. No Wait-Time Charges — OPEN ITEM
- **Severity:** MEDIUM
- **Status:** Missing
- **Impact:** Driver waits 20 min at pickup for free.
- **Open Question:** Do we charge customers for driver wait time? Free buffer duration?
- **Fix:**
  - Free buffer: 5 min
  - Per-minute charge after buffer
  - Schema: `waitTimeMinutes`, `waitTimeCharge`

### 19. No Toll & Parking Fee Pass-Through — OPEN ITEM
- **Severity:** MEDIUM
- **Status:** Missing
- **Open Question:** Do we allow drivers to add toll/parking fees to the bill? Who approves?
- **Fix:**
  - Driver app: "Add extra charge" with photo receipt
  - Admin approval workflow
  - Customer notification: "Driver added toll fee RM 2.50"

### 20. No Multi-Stop / Multi-Drop Booking — DECIDED
- **Severity:** MEDIUM
- **Status:** 1 pickup + 1 delivery only
- **Impact:** Many parcel customers need 2–3 dropoffs. Competitors (Lalamove) support this.
- **Decision:** No multi-stop support for MVP. One pickup, one dropoff only. Revisit after product-market fit.
- **Fix:**
  - Not implementing for MVP
  - Future: Add up to 3 intermediate stops, route optimization, price per additional stop

---

## TIER 3 — GROWTH, RETENTION & ANALYTICS

### 21. Admin Analytics Dashboard Is Minimal — DECIDED
- **Severity:** HIGH
- **Status:** Shows total drivers, online drivers, active bookings only
- **Decision:** No advanced analytics for MVP. Basic counts are sufficient for now.
- **Fix:**
  - Not implementing for MVP
  - Future: GMV over time, revenue by zone/vehicle, driver metrics, customer retention, cancellation funnel

### 22. No Promo Code Targeting — OPEN ITEM
- **Severity:** MEDIUM
- **Status:** Coupons have flat/percentage discount only
- **Open Question:** Do we want targeted promos (new users only, dormant users, etc.) or flat codes for everyone?
- **Fix:**
  - User-segment targeting: new users, dormant users, high-value users
  - Vehicle-type-specific promos
  - Zone-specific promos
  - First-trip-free for new users

### 23. No Driver Heatmap / Demand Zones — FIXED
- **Severity:** MEDIUM
- **Status:** FIXED 2026-04-28 — backend demand-zone scoring is available at `GET /api/v1/driver/demand-zones`, with driver KMP response models and API access.
- **Impact:** Driver doesn't know where to position for more jobs.
- **Decision:** Yes, implement driver demand heatmap.
- **Fix:**
  - Driver-facing high-demand positioning guidance only
  - No surge multiplier or fare changes in this tranche

### 24. No Subscription / Loyalty Program — DECIDED
- **Severity:** LOW-MEDIUM
- **Status:** Missing
- **Decision:** No subscription or loyalty program for MVP. Pay-per-delivery only. Revisit after scale.
- **Fix:**
  - Not implementing for MVP
  - Future: "CarryOn Plus" — RM 9.90/mo for 10% off all deliveries, loyalty points per ringgit spent

### 25. No Retention Loops — OPEN ITEM
- **Severity:** MEDIUM
- **Status:** Missing
- **Open Question:** Do we want win-back campaigns for dormant customers? What channels (email, SMS, push)?
- **Fix:**
  - Win-back email/SMS for dormant customers
  - "We miss you" discount after 30 days of inactivity

### 26. No Tip Flow — OPEN ITEM
- **Severity:** LOW-MEDIUM
- **Status:** `Order.tipAmount` exists in schema but no UI/API
- **Open Question:** Do we want post-delivery tipping? If yes, minimum/maximum amounts?
- **Fix:**
  - Post-delivery tip screen
  - 100% to driver (no commission)

---

## TIER 4 — TECHNICAL & COMPLIANCE

### 27. Rate Limiting Per User Is Insufficient — OPEN ITEM
- **Severity:** MEDIUM
- **Status:** `express-rate-limit` on routes but not per-user for bookings
- **Open Question:** Do we want per-user rate limits? What limits for bookings, cancellations, logins?
- **Fix:**
  - Max 3 bookings per minute per user
  - Max 5 cancellations per hour
  - Max 10 login attempts per 5 minutes

### 28. No Webhook Reliability (Retries) — FIXED
- **Severity:** MEDIUM
- **Status:** FIXED 2026-04-28 — Stripe events are persisted in a webhook inbox, processed idempotently, retried with exponential backoff, and marked failed after the fourth failed processing attempt.
- **Decision:** Yes, implement automatic retry with exponential backoff.
- **Fix:**
  - Webhook event log with retry count
  - Exponential backoff retry
  - Alert if webhook fails >3 times

### 29. No API Versioning — FIXED
- **Severity:** LOW-MEDIUM
- **Status:** FIXED 2026-04-28 — canonical routes are mounted under `/api/v1/*`; legacy `/api/*` remains compatible with deprecation headers.
- **Decision:** Yes, add API versioning now before scale.
- **Fix:**
  - `/api/v1/...` prefix or `Accept-Version` header
  - Deprecation policy

### 30. Search & Discovery (Address Autocomplete) — ALREADY IMPLEMENTED
- **Severity:** MEDIUM
- **Details:** Address autocomplete, geocoding, reverse geocoding, route calculation, and static maps are all functional.
- **Frontend:** `SelectAddressScreen.kt` uses debounced `LocationApi.autocomplete()`
- **Backend:** `/api/location/autocomplete`, `/api/location/search-places`, `/api/location/geocode`, `/api/location/reverse-geocode`, `/api/location/calculate-route`
- **Remaining:** Address validation (is it serviceable?) — check if pickup/delivery is within operational zones

### 31. GDPR / PDPA Compliance Missing — FIXED
- **Severity:** HIGH
- **Status:** FIXED 2026-04-28 — user data export, anonymized account deletion, push-device cleanup, and privacy consent tracking are implemented.
- **Decision:** Yes, implement data export and account deletion.
- **Fix:**
  - "Download my data" button
  - "Delete my account" with cascading anonymization
  - Privacy policy and consent tracking

### 32. No Deep Linking / Universal Links — FIXED
- **Severity:** MEDIUM
- **Status:** FIXED 2026-04-28 — customer app parses tracking and referral app links, Android declares scheme and `carryon.app` intent filters, and referral links prefill the promo screen.
- **Decision:** Yes, implement deep links for tracking and referrals.
- **Fix:**
  - `carryon.app/track/BOOKING_ID`
  - Referral deep links: `carryon.app/ref/CODE`

---

## PRICING & ECONOMICS — DETAILED BREAKDOWN

| # | Feature | Status | Impact | Effort |
|---|---------|--------|--------|--------|
| 1.1 | Surge pricing | MISSING | HIGH | M |
| 1.2 | Route-based distance | MISSING | HIGH | M |
| 1.3 | Wait-time charges | MISSING | MEDIUM | S |
| 1.4 | Base fare + minimum fare | PARTIAL | MEDIUM | S |
| 1.5 | Toll/parking pass-through | MISSING | MEDIUM | M |
| 1.6 | Return trip pricing | MISSING | LOW | M |
| 1.7 | Night/weekend pricing | MISSING | MEDIUM | S |
| 1.8 | Fare breakdown transparency | PARTIAL | MEDIUM | S |

## DISPATCH & MATCHING — DETAILED BREAKDOWN

| # | Feature | Status | Impact | Effort |
|---|---------|--------|--------|--------|
| 2.1 | Batched auto-dispatch | BROADCAST-ONLY | HIGH | M |
| 2.2 | Driver acceptance rate tracking | MISSING | HIGH | S |
| 2.3 | Batch/pooling deliveries | SCHEMA ONLY | HIGH | L |
| 2.4 | Scheduled booking dispatch | MINIMAL | MEDIUM | M |
| 2.5 | Driver ETA to pickup | MISSING | MEDIUM | S |

## PAYMENTS & WALLET — DETAILED BREAKDOWN

| # | Feature | Status | Impact | Effort |
|---|---------|--------|--------|--------|
| 5.1 | Cash/Card/UPI at checkout | REJECTED BY BACKEND | — | — |
| 5.2 | Driver cash settlement | N/A (no COD) | — | — |
| 5.3 | Partial refunds | MISSING | MEDIUM | S |
| 5.4 | Tip management | SCHEMA ONLY | MEDIUM | S |
| 5.5 | Subscription/loyalty | MISSING | LOW | M |

---

## RECOMMENDED FIX ORDER

| Sprint | Focus | Deliverables |
|--------|-------|-------------|
| 1 | Pricing accuracy | Google Distance Matrix, basePrice, route polyline |
| 2 | Cancellations & safety | Cancellation fees, SOS button, audit log |
| 3 | Dispatch reliability | Batched dispatch, acceptance rate tracking |
| 4 | Real-time infra | WebSocket tracking, scheduled booking cron |
| 5 | Driver trust | Background checks, incentives engine, doc expiry |
| 6 | Growth & ops | Surge pricing, admin analytics, multi-stop |

---

## FIXED (Move items here as they ship)

| Date | Feature | PR / Commit |
|------|---------|-------------|
| 2026-04-28 | Backend-owned route pricing with base fare, route fallback, and stored authoritative distance | Verified: `npm test -- --runInBand`, `npm run build`, `./gradlew :composeApp:compileCommonMainKotlinMetadata`, `./gradlew :composeApp:compileDebugKotlinAndroid` |
| 2026-04-28 | Booking creation idempotency with atomic wallet debit and client retry key reuse | Verified: `npm test -- --runInBand`, `npm run build`, `./gradlew :composeApp:compileCommonMainKotlinMetadata`, `./gradlew :composeApp:compileDebugKotlinAndroid` |
| 2026-04-28 | Minimum viable audit log for critical business mutations | Verified: `npm test -- --runInBand`, `npm run build` |
| 2026-04-28 | Live tracking WebSocket rooms with REST polling fallback | Verified: `npm test -- --runInBand`, `./gradlew :composeApp:compileCommonMainKotlinMetadata`, `./gradlew :composeApp:compileDebugKotlinAndroid` |
| 2026-04-28 | Driver demand zones for high-demand positioning guidance without surge pricing | Verified: `npm test -- --runInBand`, `npm run build`, `./gradlew :composeApp:compileCommonMainKotlinMetadata -q` in customer and driver apps, `./gradlew :composeApp:compileDebugKotlinAndroid -q` in customer and driver apps |
| 2026-04-28 | Stripe webhook inbox with idempotent processing and exponential retry | Verified: `npm test -- --runInBand`, `npm run build` |
| 2026-04-28 | API versioning with `/api/v1/*` canonical routes and legacy deprecation headers | Verified: `npm test -- --runInBand`, `npm run build` |
| 2026-04-28 | Privacy export, anonymized account deletion, and consent tracking | Verified: `npm test -- --runInBand`, `npm run build`, `./gradlew :composeApp:compileCommonMainKotlinMetadata -q`, `./gradlew :composeApp:compileDebugKotlinAndroid -q` |
| 2026-04-28 | Customer deep links for tracking and referral promo prefill | Verified: `./gradlew :composeApp:compileCommonMainKotlinMetadata -q`, `./gradlew :composeApp:compileDebugKotlinAndroid -q` |
| 2026-04-27 | Inline wallet top-up at checkout (402 bottom sheet) | — |
| 2026-04-27 | Backend 402 error enriched with `currentBalance`, `amountDue`, `shortfall` | — |

---

*This document should be reviewed weekly and updated as features are shipped or priorities shift.*
