# ADR-0001: Use Broadcast Dispatch for MVP

## Status

Accepted

## Context

CarryOn is launching into an early marketplace where driver supply may be thin and operational tuning data is limited. Batched dispatch would reduce notification noise, but it adds timing, ranking, retry, and support complexity before the marketplace has enough volume to tune it reliably.

## Decision

For MVP, dispatch broadcasts each regular immediate booking to all eligible nearby online drivers:

- eligible drivers must be online, within the configured radius, and vehicle-compatible
- the configured launch defaults remain 10 km radius and 60 second offer expiry
- scheduled bookings, pooling, priority modes, and batched dispatch rounds are outside MVP scope

## Consequences

The dispatch module should keep one clear eligibility interface so matching rules have locality even while the behavior remains broadcast-based. Future batched dispatch can be reconsidered after launch metrics show enough driver density and notification noise to justify the added module depth.
