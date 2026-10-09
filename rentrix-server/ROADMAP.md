# Rentrix — Roadmap

## v0.1.1 (in progress)

- [x] Flat entity expansion (address embeddable, richer fields)
- [x] PATCH support for partial updates
- [x] Soft-delete via Hibernate `@SQLDelete` + `@SQLRestriction`
- [x] Extended filters (furnished, parking, propertyType)
- [x] Frontend updates for new Flat shape
- [x] Aiven migration

## v0.1.2 — Tenant-created review targets

The core insight: **reviews are the product, listings are an ingredient.**
Tenants should be able to add a flat they've lived in so they can review it,
even if no landlord has listed it.

### Backend

- [ ] Add `createdBy: User` to `Flat` (nullable)
- [ ] Add `verified: Boolean = false` to `Flat`
- [ ] Allow any authenticated user to create a flat (unverified)
- [ ] Landlord/admin-created flats default to `verified = true`
- [ ] Reuse review moderation pipeline for flat moderation
- [ ] Address normalization (lowercase, strip punctuation, trim)
- [ ] Duplicate detection (fuzzy match on addressLine + zip)
- [ ] Rule: unverified flat goes public after first approved review

### Frontend

- [ ] "Add a place to review" flow for tenants
- [ ] "My Flats" page for landlords
- [ ] Create / edit flat form
- [ ] Proof-of-living upload when submitting review on unverified flat
- [ ] Verified badge on flat cards

## v0.2.0 — Landlord claiming

- [ ] Landlord "claim this flat" flow (email/phone verification)
- [ ] Claimed flats show "Verified by landlord" badge
- [ ] Landlord can mark flat `available = true` → appears in rental listings
- [ ] Reviews from verified stays ranked higher than unverified

## v0.3.0+ — Later

- [ ] Flat images
- [ ] Amenities as a list
- [ ] Map integration (lat/lng on address)
- [ ] Batch-compute averageRating + reviewCount (denormalize)
- [ ] `PageResponse<T>` DTO for stable pagination
- [ ] Password reset flow
- [ ] Email verification

## Design principles

1. **Reviews first, listings second** — the product is tenant trust
2. **Anyone can add a review target** — bootstrap the corpus
3. **Landlord verification is opt-in** — create incentive, don't gate participation
4. **Moderation over prevention** — let users create, moderate afterward
5. **Proof-of-living comes later** — trust signals compound over time

## v0.1.2 — Tenant-created review targets

### Proof of living

When a tenant creates a flat to review, optionally allow them to upload proof:

- Rent agreement (PDF/image)
- Utility bill matching the address
- Landlord reference contact

Proof is:

- Stored privately (not shown publicly)
- Used to badge the review as "Verified stay"
- Moderated alongside the review itself
- Optional — reviews without proof still publish but show a subtler badge


## v0.1.3 ✅ Shipped (YYYY-MM-DD)

- [x] Privacy Policy and Terms of Service pages
- [x] Flat images (S3, EXIF-stripped, gallery, lightbox)
- [x] Proof-of-living upload with "Verified stay" badge
- [ ] Bottom navigation on mobile → moved to v0.1.4
- [ ] Better loading skeletons → moved to v0.1.4
- [ ] Denormalized averageRating → moved to v0.1.4

## v0.1.4 (next)
- [ ] Prometheus + Grafana observability
- [ ] Bottom navigation on mobile
- [ ] Loading skeleton polish
- [ ] Denormalize averageRating + reviewCount
- [ ] SEO / OG meta tags