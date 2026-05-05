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

## DECIDED NOT TO IMPLEMENT FOR MVP

1. Multi stop deliveries (one pickup, one dropoff only)
2. Advanced admin analytics (basic counts only for now)
3. Subscription or loyalty program

## OPEN ITEMS (DECISION NEEDED)

1. Cancellation fees policy and who receives the fee
2. SOS panic button behavior (direct 999 vs platform ops middleman)
3. Batched dispatch parameters (batch size and wait time)
4. Surge pricing (manual toggle vs auto algorithm vs none)
5. Scheduled booking dispatch timing
6. Driver background checks (provider selection and cost)
7. Item insurance and claims workflow
8. Driver incentives and quests
9. Driver penalty system
10. Chat content moderation
11. Driver document expiry tracking
12. Wait time charges
13. Toll and parking fee pass through
14. Promo code targeting
15. Retention loops
16. Post delivery tipping
17. Per user rate limiting

## ALREADY IMPLEMENTED

1. Address autocomplete
2. Geocoding and reverse geocoding
3. Route calculation
4. Static maps
5. Inline wallet top up at checkout

## FIXED

1. Inline wallet top up at checkout with bottom sheet on 402 error
2. Backend 402 error enriched with currentBalance, amountDue, shortfall
