# CarryOn — Critical Gaps & Action Items

> **Audit Date:** 2026-04-27
> **Business Model:** Pure Marketplace (Uber-for-Parcels)
> **Platform Commission:** 12% (Driver keeps 88%)

This file tracks the **most dangerous gaps** that will break unit economics, driver supply, or trust if not fixed before scaling. For the full inventory of all missing features, see `MISSING.md`.

---

## TIER 0 — FIX BEFORE NEXT DRIVER ONBOARDING

These gaps directly hurt driver earnings or create exploit vectors. Fix them immediately.

### 1. Pricing Uses Straight-Line Distance (Revenue Leakage)
- **Severity:** CRITICAL
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

### 2. Zero Cancellation Fees (Abuse Magnet)
- **Severity:** CRITICAL
- **File:** `backend/src/routes/booking.routes.js` → `POST /:id/cancel`
- **Problem:** Customers get 100% refund regardless of when they cancel. Driver gets zero compensation for wasted fuel/time.
- **Impact:**
  - Customers will book multiple options and cancel freely
  - Drivers abandon platform after driving to pickups for nothing
  - No revenue protection
- **Fix:**
  1. Tiered cancellation policy:
     - Before driver assigned → Full refund
     - After driver assigned → 10% fee + RM 2 driver compensation
     - After driver arrives → 25% fee + RM 5 driver compensation
     - After pickup → No cancellation (support-only)
  2. Add `cancellationFee`, `cancelledBy`, `cancelReason` to schema
  3. Compensate driver via `DriverWalletTransaction` (type: `CANCELLATION_COMPENSATION`)

### 3. Wallet-Only Payments (Accepted Design Choice — But Friction Remains)
- **Severity:** MEDIUM (intentional, but creates onboarding friction)
- **File:** `backend/src/routes/booking.routes.js` → `POST /`
- **Decision:** Platform deliberately does **NOT** support `CASH`, `CARD`, or `UPI` at checkout. All bookings are paid from pre-topped-up wallet.
- **Rationale:**
  - Eliminates driver cash handling and settlement complexity
  - Removes COD fraud and no-show risk
  - Simplifies reconciliation — every ringgit flows through platform ledger
- **Remaining Risk:**
  - First-time users must top up before their first booking → significant drop-off at activation
  - No "pay exact amount at checkout" option for users who don't want to hold a wallet balance
- **Fix / Mitigation:**
  1. Offer a **signup wallet bonus** (e.g., RM 5 free credit) so first-time users can book without entering payment details immediately
  2. Allow **exact-amount card checkout** (Stripe PaymentIntent for the precise fare, auto-credited to wallet then debited) so the UX feels like "pay now" even though it's technically wallet
  3. Remove `CASH`, `CARD`, `UPI` from schema enums if permanently unsupported to avoid confusion

---

## TIER 1 — FIX BEFORE SCALE (100+ trips/day)

### 4. Broadcast Dispatch Creates Thundering Herd
- **Severity:** HIGH
- **File:** `backend/src/services/dispatch.js` → `notifyNearbyDrivers()`
- **Problem:** Notifies **all** online drivers within 10km at once. Race conditions, frustrated drivers, meaningless acceptance metrics.
- **Impact:**
  - 20 drivers get push → 1 accepts → 19 are annoyed
  - No fairness — fastest-tap-wins, not best-driver-wins
  - Impossible to measure true demand because offers are spammed
- **Fix:**
  1. Batched dispatch: notify top 3 closest drivers, wait 15s
  2. Rank by: proximity (40%) + rating (20%) + acceptance rate (20%) + completion rate (20%)
  3. Expand batch if no acceptance
  4. Track `acceptanceRate` per driver; penalize chronic rejectors

### 5. No Surge Pricing = No Supply Incentive
- **Severity:** HIGH
- **File:** `backend/src/services/businessConfig.js`
- **Problem:** Flat rate card regardless of demand. At 6pm in rain with 50 bookings and 3 drivers, price stays the same.
- **Impact:**
  - Drivers have zero incentive to go online during peak
  - Customers wait forever, blame platform
  - Lost revenue during highest-demand periods
- **Fix:**
  1. Zone-based heatmap (divide city into hexagons or polygons)
  2. Surge multiplier = `f(openBookings, onlineDrivers, avgWaitTime)`
  3. Cap at 3.0x
  4. Show "High demand — 1.5x fare" to customer before confirmation
  5. Show "Surge active — earn 1.5x" to driver

---

## TIER 2 — TRUST & SAFETY (Legal / Reputation Risk)

### 6. No SOS / Panic Button
- **Severity:** CRITICAL (Legal)
- **Status:** Completely missing from both apps
- **Impact:** If a driver or customer is in danger, there is no in-app emergency feature. Liability exposure.
- **Fix:**
  - SOS button in customer app and driver app
  - Press triggers: call emergency services, share live location to emergency contact + platform ops
  - Auto-create support ticket with location log

### 7. No Item Insurance / Claims Workflow
- **Severity:** HIGH
- **Status:** Missing
- **Impact:** Driver breaks a TV? Package lost? You have zero process. Customers will chargeback and post on social media.
- **Fix:**
  - Add `declaredValue` and `insuranceOptIn` to booking flow
  - Insurance premium = 1% of declared value
  - Claims form with photo evidence
  - Admin claims review page
  - Payout from platform reserve or insurance partner

### 8. No Driver Background Checks
- **Severity:** HIGH
- **Status:** KYC docs uploaded but no verification partner integration
- **Impact:** You are trusting self-uploaded photos. No criminal record check.
- **Fix:**
  - Integrate background check API (e.g., local police clearance, Checkr)
  - `backgroundCheckStatus` field on Driver
  - Block onboarding until cleared
  - Annual re-check

### 9. No Content Moderation on Chat
- **Severity:** MEDIUM
- **Status:** Chat messages unfiltered
- **Impact:** PII sharing (phone numbers), abuse, threats — all unmonitored.
- **Fix:**
  - Regex scan for phone numbers / emails → block or mask
  - Profanity filter
  - Report message button
  - Auto-suspend for repeated abuse

---

## TIER 3 — OPERATIONAL BLINDNESS

### 10. Scheduled Bookings Are Ghosts
- **Severity:** HIGH
- **File:** `schema.prisma` → `Booking.scheduledTime` exists
- **Problem:** `scheduledTime` is stored but never dispatched. A booking for tomorrow sits in `SEARCHING_DRIVER` forever until someone manually notices.
- **Fix:**
  - Cron job (or Bull queue) opens scheduled bookings 45 min before `scheduledTime`
  - Early dispatch to drivers who opt into scheduled jobs
  - Customer reminder push 30 min before pickup

### 11. No Real-Time Tracking (WebSocket)
- **Severity:** HIGH
- **Status:** REST polling only
- **Impact:** Customer tracking is jerky and battery-heavy. Driver location updates hit DB on every ping.
- **Fix:**
  - Socket.io or WebSocket room per `bookingId`
  - Driver emits location every 5s while on job
  - Customer subscribes to room for smooth animation
  - Fallback to polling on disconnect

### 12. No Audit Log = No Accountability
- **Severity:** HIGH
- **Status:** Missing
- **Impact:** If a refund happens, a price changes, or a driver is approved — you cannot answer "who did this and when?"
- **Fix:**
  - `AuditLog` table: `actorId`, `action`, `entityType`, `entityId`, `oldValue`, `newValue`, `timestamp`
  - Log all: status changes, price edits, refunds, admin approvals, payout triggers

### 13. No Idempotency on Booking Creation
- **Severity:** HIGH
- **File:** `backend/src/routes/booking.routes.js` → `POST /`
- **Problem:** Network retry can create duplicate bookings and double-charge wallet. You retry on `orderCode` conflict but not on payment idempotency.
- **Fix:**
  - Require `Idempotency-Key` header on `POST /api/bookings`
  - Store processed keys for 24 hours
  - Return same booking on duplicate key

---

## TIER 4 — DRIVER RETENTION

### 14. No Driver Incentives / Quests
- **Severity:** MEDIUM-HIGH
- **Status:** Missing
- **Impact:** Drivers have no reason to drive more hours or accept low-paying trips.
- **Fix:**
  - Quest engine: "Complete 10 trips this weekend → RM 50 bonus"
  - Peak-hour guarantee: "Earn min RM 15/hr 5–9pm"
  - Consecutive trip bonus
  - Schema: `DriverIncentive`, `DriverQuestProgress`

### 15. No Driver Penalty System
- **Severity:** MEDIUM-HIGH
- **Status:** Missing
- **Impact:** Drivers can cancel, no-show, or damage goods with zero consequences.
- **Fix:**
  - Deduction codes: `LATE_ARRIVAL`, `NO_SHOW`, `DAMAGED_GOODS`, `RUDE_BEHAVIOR`
  - Fine deducted from driver wallet
  - 3 strikes = temporary suspension
  - Appeal workflow

### 16. Driver Document Expiry Not Tracked
- **Severity:** MEDIUM
- **Status:** `DriverDocument` has no `expiryDate`
- **Impact:** Driver with expired insurance stays online. Accident = platform liability.
- **Fix:**
  - Add `expiryDate` to `DriverDocument`
  - Auto-notification 30 days before expiry
  - Auto-offline on expiry
  - Admin dashboard alert

---

## TIER 5 — CUSTOMER EXPERIENCE

### 17. Multi-Stop / Multi-Drop Not Supported
- **Severity:** MEDIUM
- **Status:** 1 pickup + 1 delivery only
- **Impact:** Many parcel customers need 2–3 dropoffs. Competitors (Lalamove) support this.
- **Fix:**
  - Add up to 3 intermediate stops
  - Route optimization
  - Price per additional stop

### 18. No Wait-Time Charges
- **Severity:** MEDIUM
- **Status:** Missing
- **Impact:** Driver waits 20 min at pickup for free. Unfair.
- **Fix:**
  - Free buffer: 5 min
  - Per-minute charge after buffer
  - Schema: `waitTimeMinutes`, `waitTimeCharge`

### 19. No Tip Flow
- **Severity:** LOW-MEDIUM
- **Status:** `Order.tipAmount` exists in schema but no UI/API
- **Impact:** Missed driver happiness and extra earnings.
- **Fix:**
  - Post-delivery tip screen
  - 100% to driver (no commission)
  - Wallet or Stripe charge

---

## RECOMMENDED FIX ORDER

| Week | Focus | Deliverables |
|------|-------|-------------|
| 1 | Pricing accuracy | Google Distance Matrix, basePrice, route polyline |
| 2 | Payments & cancellations | Enable CASH/CARD, cancellation fee policy |
| 3 | Dispatch reliability | Batched dispatch, acceptance rate tracking |
| 4 | Safety & trust | SOS button, item insurance MVP, audit log |
| 5 | Real-time infra | WebSocket tracking, scheduled booking cron |
| 6 | Driver retention | Incentives engine, penalty system, doc expiry |
| 7–8 | Growth & ops | Surge pricing, admin analytics, multi-stop |

---

*Review this file weekly. Move items to "FIXED" section as they ship.*
