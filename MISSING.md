# CarryOn — Missing Features & Gaps Audit

> **Context:** CarryOn is a pure marketplace logistics platform (Uber-for-parcels model).
> **Commission Model:** Driver keeps `DRIVER_COMMISSION_RATE` (default 88%), platform takes 12%.
> **Current Scope:** Customer mobile app (KMP), Driver mobile app (KMP), Admin web dashboard (Next.js), Backend (Node/Express/Prisma/Postgres).
> **Date:** 2026-04-27

---

## 1. PRICING & ECONOMICS

### 1.1 Surge Pricing (Dynamic Pricing)
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** No demand-supply balancing. When demand exceeds available drivers in a zone, prices should increase automatically to incentivize driver supply and throttle demand.
- **What exists:** Static `VEHICLE_RATE_PER_KM` with Regular / Priority / Pooling modes only.
- **What's needed:**
  - Zone-based heatmap tracking
  - Surge multiplier engine (e.g., 1.2x, 1.5x, 2.0x)
  - Algorithm: `surgeMultiplier = f(demandRequests, availableDrivers, avgAcceptTime)`
  - Cap on max multiplier (e.g., 3.0x)
  - Customer-facing "High demand" UX before booking confirmation
  - Driver notification: "Surge pricing active in your area"

### 1.2 Route-Based Distance Calculation
- **Status:** MISSING — using Haversine straight-line distance
- **Impact:** HIGH
- **Details:** `serverQuote()` uses `haversineKm()` (crow-flies). Real roads are 20–40% longer.
- **What's needed:**
  - Google Maps Distance Matrix API or OSRM integration
  - Actual driving distance + duration for pricing
  - Route polyline storage for tracking accuracy
  - Fallback to haversine + 1.3x multiplier if API fails

### 1.3 Wait-Time Charges
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** If driver arrives at pickup and customer is not ready, there is no charge for waiting.
- **What's needed:**
  - Free wait-time buffer (e.g., 5 minutes)
  - Per-minute wait charge after buffer
  - Schema: `waitTimeMinutes`, `waitTimeCharge` on Booking
  - Driver app UI: "Start wait timer" / "Customer ready"

### 1.4 Base Fare + Minimum Fare
- **Status:** PARTIAL — `Vehicle.basePrice` exists in schema but not used in `serverQuote()`
- **Impact:** MEDIUM
- **Details:** Pricing is purely `distance * ratePerKm`. No base fare, no minimum order value.
- **What's needed:**
  - Apply `basePrice` from Vehicle table in quote
  - Enforce `minimumFare` per vehicle type
  - Ensure driver still gets fair amount on short trips

### 1.5 Toll & Parking Fee Pass-Through
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** No mechanism for drivers to add tolls, parking, or ferry fees.
- **What's needed:**
  - Driver app: "Add extra charge" with photo receipt
  - Admin approval workflow for extra charges
  - Customer notification: "Driver added toll fee RM 2.50"

### 1.6 Return Trip Pricing
- **Status:** MISSING
- **Impact:** LOW-MEDIUM
- **Details:** No support for customer requesting driver wait and return, or book a round trip.
- **What's needed:**
  - "Return trip" toggle in booking flow
  - Reduced return-trip rate (e.g., 50% of one-way)
  - Multi-leg booking support

### 1.7 Night / Weekend / Holiday Pricing
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** No time-based rate adjustments.
- **What's needed:**
  - Time-of-day multipliers in `businessConfig.js`
  - Holiday calendar integration
  - Pre-booking price preview: "Night surcharge applies"

### 1.8 Fare Breakdown Transparency
- **Status:** PARTIAL
- **Impact:** MEDIUM
- **Details:** Backend calculates total, but doesn't decompose for customer/driver.
- **What's needed:**
  - Customer sees: Base fare + Distance charge + Surge + Wait time + Discount = Total
  - Driver sees: Gross fare − Platform fee (12%) = Your earning

---

## 2. DISPATCH & MATCHING

### 2.1 Auto-Dispatch Algorithm (Broadcast → Directed)
- **Status:** BROADCAST-ONLY
- **Impact:** HIGH
- **Details:** Currently notifies ALL online drivers within 10km radius. This is noisy and creates race conditions.
- **What's needed:**
  - Batched dispatch: notify top-3 closest drivers first, wait 15s, then expand
  - Driver score ranking: proximity + rating + acceptance rate + completion rate
  - Round-robin fairness: don't always send to the same 3 drivers
  - "Offer timeout" with automatic re-dispatch

### 2.2 Driver Acceptance Rate & Consequences
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** Drivers can reject infinitely with no penalty. This breaks dispatch reliability.
- **What's needed:**
  - Track `acceptanceRate`, `rejectionCount`, `ignoreCount`
  - Penalty: temporary de-prioritization in dispatch queue
  - Reward: priority dispatch for >90% acceptance rate
  - Auto-offline after N consecutive ignores

### 2.3 Batch / Multi-Stop Deliveries (Pooling)
- **Status:** SCHEMA EXISTS (pooling rate), NO LOGIC
- **Impact:** HIGH
- **Details:** Pooling mode exists in pricing but no actual batching engine.
- **What's needed:**
  - Route optimization for multiple pickups/dropoffs
  - Compatibility matching (similar routes, time windows)
  - Customer discount for accepting pooling
  - Driver earning calculation for batched trips

### 2.4 Scheduled Booking Dispatch Window
- **Status:** MINIMAL
- **Impact:** MEDIUM
- **Details:** `scheduledTime` is stored but dispatch logic doesn't pre-reserve drivers or dispatch at scheduled time.
- **What's needed:**
  - Cron job to open scheduled bookings 30–60 min before pickup
  - Early-bird dispatch to drivers who prefer scheduled jobs
  - Customer reminder push: "Your driver will arrive in 30 min"

### 2.5 Driver ETA to Pickup (Not Customer ETA)
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** No calculation of how long it takes driver to reach pickup.
- **What's needed:**
  - Google Directions API for driver→pickup ETA
  - Customer sees: "Driver is 8 minutes away" (not just distance)

---

## 3. CANCELLATION & REFUNDS

### 3.1 Cancellation Fee Policy
- **Status:** MISSING — always full refund
- **Impact:** HIGH
- **Details:** `refundBooking()` always refunds 100%. No protection against abuse.
- **What's needed:**
  - Tiered cancellation policy:
    - Before driver assigned → Full refund
    - After driver assigned → Small fee (e.g., RM 2) + compensate driver for opportunity cost
    - After driver arrives → Higher fee (e.g., RM 5) + partial driver compensation
    - After pickup → No cancellation allowed (or manual support only)
  - Schema: `cancellationFee`, `cancelledBy`, `cancelReason`
  - Driver cancellation also needs policy: if driver cancels after accepting, they don't get paid and may be penalized.

### 3.2 Cancellation Reasons
- **Status:** PARTIAL — reason text accepted but not structured
- **Impact:** MEDIUM
- **What's needed:**
  - Enum of reasons: `CUSTOMER_NOT_READY`, `WRONG_ADDRESS`, `DRIVER_TOO_FAR`, `CHANGE_OF_PLANS`, `VEHICLE_ISSUE`, `OTHER`
  - Analytics on cancellation reasons
  - Automatic support ticket creation for certain reasons

### 3.3 No-Show Handling
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** What happens if driver arrives and customer is unreachable?
- **What's needed:**
  - Driver can mark "Customer no-show" after 10 min wait
  - Customer charged no-show fee
  - Driver compensated for trip to pickup
  - Booking auto-cancelled

---

## 4. REAL-TIME TRACKING & COMMUNICATION

### 4.1 WebSocket / Socket.io for Live Location
- **Status:** MISSING — uses REST polling + push notifications
- **Impact:** HIGH
- **Details:** Customer tracking screen must poll repeatedly. No true real-time driver location stream.
- **What's needed:**
  - WebSocket or Socket.io room per booking
  - Driver emits `location-update` every 3–5 seconds while on job
  - Customer subscribes to room and sees smooth map animation
  - Fallback to polling if socket disconnects

### 4.2 Route Polyline & Navigation Integration
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** No stored route path. Tracking just shows driver dot, not the planned road path.
- **What's needed:**
  - Store Google Maps polyline for pickup→dropoff route
  - Show route overlay to customer
  - Deep-link to Google Maps / Waze for driver navigation

### 4.3 Driver Location Update Throttling
- **Status:** MISSING
- **Impact:** LOW-MEDIUM
- **Details:** `location.routes.js` exists but no throttling, batching, or accuracy filtering.
- **What's needed:**
  - Minimum accuracy threshold (e.g., <50m)
  - Batch updates every 10 seconds instead of every GPS tick
  - Only update when moved >20m to save battery & DB writes

---

## 5. PAYMENTS & WALLET

### 5.1 Cash on Delivery (COD) / Cash Payment Handling
- **Status:** SCHEMA EXISTS (`CASH`, `UPI`, `CARD`, `WALLET`), BUT ONLY WALLET IS ALLOWED
- **Impact:** HIGH
- **Details:** `booking.routes.js` explicitly rejects everything except `WALLET`.
- **What's needed:**
  - Enable CASH and CARD payments
  - For CASH: driver collects cash, settles with platform (driver owes platform the 12% fee, or customer pays platform directly)
  - For CARD: Stripe payment intent at booking time or after delivery
  - For UPI: Integration with local UPI provider

### 5.2 Driver Cash Settlement
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** If cash is collected by driver, how does platform get its 12%?
- **What's needed:**
  - Driver ledger: `CASH_COLLECTED` transactions
  - Driver owes platform: periodic settlement (daily/weekly)
  - Auto-deduct from driver wallet, or driver must top up to clear negative balance

### 5.3 Customer Partial Refunds
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** Refunds are always full amount. No support for partial refund (e.g., discount applied incorrectly).
- **What's needed:**
  - Admin-initiated partial refund UI
  - Partial refund in Stripe
  - Wallet partial credit support

### 5.4 Tip Management
- **Status:** SCHEMA EXISTS (`Order.tipAmount`), NO FLOW
- **Impact:** MEDIUM
- **Details:** Customer can tip, but no UI flow or backend API for tipping after delivery.
- **What's needed:**
  - Post-delivery tip screen: "Add tip for driver?"
  - Tip goes 100% to driver (no commission)
  - Wallet or Stripe charge for tip

### 5.5 Subscription / Loyalty Program
- **Status:** MISSING
- **Impact:** LOW-MEDIUM
- **Details:** No recurring revenue model or customer retention mechanism beyond referrals.
- **What's needed:**
  - Subscription tiers: e.g., "CarryOn Plus" — RM 9.90/mo for 10% off all deliveries
  - Loyalty points per ringgit spent
  - Points redemption for discounts

---

## 6. DRIVER EXPERIENCE

### 6.1 Driver Incentives & Quests
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** No gamification or earning boosts to influence driver behavior.
- **What's needed:**
  - Quests: "Complete 10 trips this weekend, earn RM 50 bonus"
  - Peak-hour guarantees: "Earn at least RM 15/hr between 5–9pm"
  - Consecutive trip bonus
  - Schema: `DriverIncentive`, `DriverQuest` models

### 6.2 Driver Penalty System
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** No consequences for bad driver behavior.
- **What's needed:**
  - Penalty reasons: late arrival, no-show, rude behavior, damaged goods, wrong delivery
  - Fine deduction from driver wallet
  - Temporary suspension / permanent ban workflow
  - Appeal process

### 6.3 Driver Document Expiry & Renewal
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** `DriverDocument` stores upload date but no expiry date.
- **What's needed:**
  - `expiryDate` on `DriverDocument`
  - Auto-notification 30 days before expiry
  - Auto-offline driver if license/insurance expires
  - Admin dashboard alert for expiring docs

### 6.4 Driver Shift / Break Management
- **Status:** MISSING
- **Impact:** LOW-MEDIUM
- **Details:** Driver toggles online/offline manually. No shift scheduling or mandatory break reminders.
- **What's needed:**
  - Optional shift scheduling
  - Fatigue alert: "You've been online 4 hours. Take a 15-min break."
  - Auto-offline after 12 hours continuous

### 6.5 Driver Heatmap / Demand Zones
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** Driver doesn't know where to position for more jobs.
- **What's needed:**
  - In-app heatmap showing high-demand zones
  - "Head to [Area] — high demand + 1.3x surge"

### 6.6 Driver Referral Program
- **Status:** MISSING
- **Impact:** LOW-MEDIUM
- **Details:** Customer referral exists (`Referral` model), but no driver-refer-driver program.
- **What's needed:**
  - Driver referral code
  - Bonus for referring new drivers who complete N trips

---

## 7. CUSTOMER EXPERIENCE

### 7.1 Multi-Stop / Multi-Drop Booking
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** Only 1 pickup and 1 delivery address. Many parcel deliveries need multiple drops.
- **What's needed:**
  - Add up to 3–5 dropoff points
  - Route optimization
  - Price per additional stop

### 7.2 Item Details & Insurance
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** No item description, value declaration, or goods-in-transit insurance.
- **What's needed:**
  - Item category: Document, Electronics, Fragile, Food, Furniture, etc.
  - Declared value (for insurance)
  - Insurance premium: e.g., 1% of declared value
  - Claims workflow for lost/damaged items

### 7.3 Preferred / Favorite Drivers
- **Status:** MISSING
- **Impact:** LOW-MEDIUM
- **Details:** No way for customer to request a driver they've rated highly before.
- **What's needed:**
  - "Add to favorites" after delivery
  - Priority dispatch to favorite drivers (if online)

### 7.4 Delivery Instructions & Photo at Pickup
- **Status:** PARTIAL — `notes` field exists but no structured instructions
- **Impact:** MEDIUM
- **What's needed:**
  - Checklist: "Leave at door", "Call upon arrival", "Fragile", "Require signature"
  - Customer-upload photo of item for driver reference
  - Driver-upload photo at pickup as proof of condition

### 7.5 Estimated Time of Arrival (ETA) Accuracy
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** ETA is static `booking.duration`. Doesn't update based on traffic or driver actual position.
- **What's needed:**
  - Live ETA updates via Google Directions API
  - Push notification if ETA changes significantly: "Delay expected — new arrival 6:15pm"

### 7.6 Guest Checkout / Book for Someone Else
- **Status:** PARTIAL
- **Details:** Can enter receiver details, but sender must have account.
- **What's needed:**
  - Guest checkout (no account required)
  - "Book for someone else" with SMS notification to receiver

---

## 8. TRUST & SAFETY

### 8.1 SOS / Panic Button
- **Status:** MISSING
- **Impact:** CRITICAL
- **Details:** No emergency safety feature for either customer or driver.
- **What's needed:**
  - SOS button in both apps
  - Triggers: call emergency services, share live location with emergency contact + platform
  - Incident reporting workflow

### 8.2 Driver Background Checks
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** Document upload exists, but no integration with background check provider.
- **What's needed:**
  - Integration with background check API (e.g., Checkr, local police clearance)
  - `backgroundCheckStatus` on Driver
  - Annual re-check requirement

### 8.3 Delivery Verification Beyond OTP
- **Status:** PARTIAL — photo proof exists, no signature
- **Impact:** MEDIUM
- **What's needed:**
  - Digital signature capture on driver device
  - ID verification for high-value items
  - Recipient ID photo for restricted goods

### 8.4 Goods Insurance / Claims
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** No protection for lost, damaged, or stolen goods.
- **What's needed:**
  - Optional insurance at booking
  - Claims submission with photos
  - Admin claims review dashboard
  - Payout from platform or insurance partner

### 8.5 Content Moderation for Chat
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** In-app chat has no profanity filter or abuse detection.
- **What's needed:**
  - Message scanning for PII sharing (phone numbers)
  - Profanity / threat detection
  - Report message button
  - Auto-block for repeated abuse

---

## 9. ADMIN & OPERATIONS

### 9.1 Advanced Analytics Dashboard
- **Status:** MINIMAL
- **Impact:** HIGH
- **Details:** Admin dashboard shows: total drivers, online drivers, active bookings, notifications sent. No business intelligence.
- **What's needed:**
  - GMV (Gross Merchandise Value) over time
  - Revenue by vehicle type, zone, time
  - Driver metrics: churn rate, avg earnings/hr, acceptance rate distribution
  - Customer metrics: retention (D1, D7, D30), repeat rate, CAC
  - Cancellation funnel: at what stage do bookings cancel?
  - Failed dispatch rate: bookings that never got a driver

### 9.2 Zone / Geofence Management
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** No operational zones. Can't open/close service areas.
- **What's needed:**
  - Polygon-based service zones
  - Per-zone pricing adjustments
  - Zone-level driver caps
  - "Service not available in this area" UX

### 9.3 Manual Booking Management
- **Status:** PARTIAL — can create ride requests
- **Impact:** MEDIUM
- **What's needed:**
  - Edit/cancel any booking from admin
  - Reassign driver manually
  - Issue full/partial refund from admin
  - Apply promo retroactively

### 9.4 Customer Support CRM
- **Status:** BASIC — tickets exist, no CRM view
- **Impact:** MEDIUM
- **What's needed:**
  - Unified customer view: bookings, payments, tickets, ratings
  - Ticket assignment to agents
  - SLA tracking (first response time, resolution time)
  - Macros / canned responses

### 9.5 Driver Payout Approval
- **Status:** MISSING — payouts likely automatic via Stripe
- **Impact:** MEDIUM
- **What's needed:**
  - Admin review of driver earnings before payout
  - Hold payout if disputes open
  - Scheduled payout batches (weekly)

### 9.6 Content Management (CMS)
- **Status:** MISSING
- **Impact:** LOW
- **Details:** Help articles exist but no admin UI to manage them.
- **What's needed:**
  - Admin CRUD for HelpArticles
  - In-app banner management
  - Push notification template management

---

## 10. GROWTH & RETENTION

### 10.1 Promo Code Sophistication
- **Status:** BASIC
- **Impact:** MEDIUM
- **Details:** Coupons have flat/percentage discount with min order and expiry. No advanced targeting.
- **What's needed:**
  - User-segment targeting: new users only, dormant users, high-value users
  - Vehicle-type-specific promos
  - Zone-specific promos
  - First-trip-free for new users
  - Referral code tracking with attribution

### 10.2 Push Notification Campaigns
- **Status:** BASIC — can send to all/online drivers
- **Impact:** MEDIUM
- **What's needed:**
  - Customer push campaigns (not just drivers)
  - Scheduled campaigns
  - A/B testing for push copy
  - Rich push with images
  - Deep-linking to specific screens

### 10.3 Retention Loops
- **Status:** MISSING
- **Impact:** MEDIUM
- **What's needed:**
  - Win-back email/SMS for dormant customers
  - "We miss you" discount after 30 days of inactivity
  - Trip milestone celebrations ("10th delivery — RM 5 off!")

---

## 11. TECHNICAL & INFRASTRUCTURE

### 11.1 Idempotency Keys for Payments
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** `booking.routes.js` retries 3 times on order code conflict, but no idempotency key for payment/booking creation. Double-booking or double-charge possible on network retry.
- **What's needed:**
  - Idempotency key header (`Idempotency-Key`) for POST /api/bookings
  - Store processed keys for 24 hours

### 11.2 Rate Limiting per User
- **Status:** PARTIAL — `express-rate-limit` on routes but not per-user for bookings
- **Impact:** MEDIUM
- **What's needed:**
  - Max 3 bookings per minute per user
  - Max 5 cancellations per hour
  - Max 10 login attempts per 5 minutes

### 11.3 Webhook Reliability (Retries)
- **Status:** MISSING
- **Details:** Stripe webhook handler exists but no dead-letter queue for failed webhooks.
- **What's needed:**
  - Webhook event log with retry count
  - Exponential backoff retry
  - Alert if webhook fails >3 times

### 11.4 API Versioning
- **Status:** MISSING
- **Impact:** LOW-MEDIUM
- **Details:** All routes under `/api/*` with no versioning. Mobile app updates will break old clients.
- **What's needed:**
  - `/api/v1/...` prefix or `Accept-Version` header
  - Deprecation policy

### 11.5 Search & Discovery (Addresses)
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** No Google Places API integration for address autocomplete.
- **What's needed:**
  - Address autocomplete with Places API
  - Address validation (is it serviceable?)
  - Pin drop on map with address reverse geocoding

### 11.6 Audit Logging
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** No immutable audit trail of who changed what and when.
- **What's needed:**
  - `AuditLog` table: actor, action, entityType, entityId, oldValue, newValue, timestamp
  - Log all status changes, price changes, refunds, admin actions

---

## 12. COMPLIANCE & LEGAL

### 12.1 GDPR / PDPA Compliance
- **Status:** MISSING
- **Impact:** HIGH
- **Details:** No data export or deletion flow.
- **What's needed:**
  - "Download my data" button
  - "Delete my account" with cascading anonymization
  - Privacy policy and consent tracking
  - Data retention policies (auto-delete chat after 90 days?)

### 12.2 Terms of Service Acceptance
- **Status:** MISSING
- **Impact:** MEDIUM
- **What's needed:**
  - Versioned TOS for customers and drivers
  - Block app usage until TOS accepted
  - Log acceptance timestamp

### 12.3 Tax Invoice Compliance
- **Status:** PARTIAL — `Invoice` model exists but verify local requirements
- **Impact:** MEDIUM
- **Details:** Malaysia requires SST (Sales & Service Tax). Current `taxRate` is 6% (GST-era?).
- **What's needed:**
  - Update tax rate to current SST if applicable
  - Invoice numbering must be sequential per LHDN requirements
  - E-invoice integration (if mandated)

---

## 13. PLATFORM-SPECIFIC GAPS

### 13.1 iOS App Store / Google Play Requirements
- **Status:** UNVERIFIED
- **Impact:** HIGH
- **What's needed:**
  - App Tracking Transparency (ATT) prompt for iOS
  - Location permission rationale strings
  - Background location mode justification
  - In-app purchase compliance (if selling subscriptions)

### 13.2 Deep Linking / Universal Links
- **Status:** MISSING
- **Impact:** MEDIUM
- **Details:** No deep-link schema for sharing tracking URLs or promo referrals.
- **What's needed:**
  - `carryon.app/track/BOOKING_ID`
  - Referral deep links: `carryon.app/ref/CODE`
  - Firebase Dynamic Links or equivalent

---

## SUMMARY — PRIORITY MATRIX

| Priority | Feature | Effort | Business Impact |
|----------|---------|--------|-----------------|
| P0 | Route-based distance (Google Maps) | M | Revenue accuracy, customer trust |
| P0 | Cancellation fee policy | M | Driver protection, revenue protection |
| P0 | Cash/Card payment enablement | L | Market reach, conversion |
| P0 | SOS/Panic button | M | Legal/safety requirement |
| P0 | Idempotency keys | S | Prevent double charges |
| P1 | Surge pricing | L | Supply-demand balance |
| P1 | WebSocket live tracking | M | Core UX expectation |
| P1 | Auto-dispatch algorithm | M | Driver experience, efficiency |
| P1 | Admin analytics dashboard | M | Business operations |
| P1 | Item insurance / claims | M | Trust, competitive differentiation |
| P1 | Driver incentives/quests | M | Supply growth |
| P2 | Multi-stop bookings | L | Revenue per trip |
| P2 | Wait-time charges | S | Driver fairness |
| P2 | Driver heatmap | S | Supply positioning |
| P2 | Promo targeting | M | Growth efficiency |
| P2 | Audit logging | S | Compliance, debugging |
| P3 | Subscription program | M | Retention, LTV |
| P3 | Background checks | M | Safety, compliance |
| P3 | Content moderation | S | Trust & safety |
| P3 | Deep linking | S | Growth, viral loop |

---

## RECOMMENDED NEXT STEPS

1. **Immediate (This Sprint):** Fix pricing accuracy — integrate Google Distance Matrix API and add `basePrice` to quotes.
2. **Short-term (Next 2 Sprints):** Implement cancellation fees, enable cash/card payments, and add idempotency keys.
3. **Medium-term (Next Quarter):** Build surge pricing, WebSocket tracking, auto-dispatch algorithm, and admin analytics.
4. **Long-term (Next 2 Quarters):** Multi-stop support, insurance/claims, subscription program, and advanced growth tools.

---

*This document should be reviewed monthly and updated as features are shipped or priorities shift.*
