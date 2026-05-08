# CarryOn Platform Status

## DECIDED TO IMPLEMENT

1. Pricing accuracy via Google Maps Distance Matrix API on backend
2. Idempotency keys on booking creation using client generated UUID
3. Minimum audit log for critical business events only
4. Real time tracking via Socket.io
5. Backend as single source of truth for pricing
6. Driver demand heatmap
7. Webhook retry with exponential backoff
8. API versioning with prefix
9. GDPR and PDPA compliance with data export and account deletion
10. Deep linking for tracking and referrals
11. MVP regular-only immediate booking contract
12. MVP broadcast dispatch to all eligible nearby drivers (no batching)
13. Customer cancellation fee after driver assignment grace window
14. Driver-only SOS baseline
15. Driver document expiry reminders and auto-offline enforcement
16. Wait-time charges after pickup arrival grace period
17. Toll and parking pass-through with proof and admin approval

## DECIDED NOT TO IMPLEMENT FOR MVP

1. Multi stop deliveries (one pickup, one dropoff only)
2. Advanced admin analytics (basic counts only for now)
3. Subscription or loyalty program
4. Scheduled bookings
5. Pooling and priority booking modes
6. Batched dispatch
7. Surge pricing
8. Driver incentives and quests
9. Driver penalty system
10. Item insurance upsell and claims workflow
11. Customer SOS panic flow
12. Automated chat moderation

## OPEN ITEMS (DECISION NEEDED)

1. Driver background checks (provider selection and cost)
2. Promo code targeting
3. Retention loops
4. Post delivery tipping
5. Per user rate limiting

## ALREADY IMPLEMENTED

1. Address autocomplete
2. Geocoding and reverse geocoding
3. Route calculation
4. Static maps
5. Inline wallet top up at checkout

## FIXED

1. Inline wallet top up at checkout with bottom sheet on 402 error
2. Backend 402 error enriched with currentBalance, amountDue, shortfall
3. MVP launch policy backend: regular-only compatibility, cancellation fee split, wait-time charge, toll/parking approval workflow, driver SOS response metadata, document expiry gating
