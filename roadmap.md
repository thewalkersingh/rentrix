# Rentrix — Roadmap

The story and history of a place before you move in.

**Design principles**
1. **Reviews first, listings second** — the product is tenant trust
2. **Anyone can add a review target** — bootstrap the corpus
3. **Landlord verification is opt-in** — create incentive, don't gate participation
4. **Moderation over prevention** — let users create, moderate afterward
5. **Proof-of-living compounds trust** — signals tier over time

---

## ✅ v0.1.0 — Foundation
*Shipped*

- Auth (JWT + refresh)
- Flat listings + detail pages
- Deploy pipeline (Vercel + Render + Aiven)
- Docker multi-stage build

---

## ✅ v0.1.1 — Rich flat data
*Shipped*

- Flat entity expansion (embedded Address, richer fields)
- PATCH support for partial updates
- Soft-delete via Hibernate `@SQLDelete` + `@SQLRestriction`
- Extended filters (furnished, parking, propertyType)
- Admin controls (verify, hide)
- Frontend updates for new Flat shape

---

## ✅ v0.1.2 — Tenant-created review targets
*Shipped*

**Core insight:** reviews are the product, listings are an ingredient.
Tenants should be able to add a flat they've lived in so they can review it,
even if no landlord has listed it.

### Backend
- `createdBy`, `verified`, `visible` fields on `Flat`
- Any authenticated user can create a flat (unverified)
- Landlord/admin-created flats default to `verified = true, visible = true`
- First approved review auto-promotes a hidden flat to visible
- Batch-compute `averageRating` + `reviewCount`
- Review-based filters (`minRating`, `hasReviews`)

### Frontend
- "Add a place to review" flow
- My Flats page with role-aware labels
- FlatForm (create + edit)
- Verified badge on cards and detail
- Review filter UI
- Navbar overhaul (avatar dropdown, "Add place" CTA)

---

## ✅ v0.1.3 — Legal, images, and verified stays
*Shipped*

### Legal
- Privacy Policy (GDPR + DPDP + CCPA)
- Terms of Service

### Flat images
- S3 storage (`rentrix-media`, public read)
- EXIF stripping, resize to 1920px, JPEG output
- Multi-image gallery with lightbox
- Owner-only uploader (drag-drop, set-primary, delete)
- Max 10 images per flat

### Proof-of-living
- S3 private bucket (`rentrix-private`)
- Upload rent agreement, utility bill, or bank statement
- 15-minute presigned URL for admin view
- "Verified stay" badge on approved reviews with proof

---

## ✅ v0.1.4 — Multi-file uploads
*Shipped*

- Multi-proof schema (`review_proofs` table, up to 3 per review)
- Grid-based uploader (5 cols for flat images, 3 cols for proofs)
- Multi-file picker support
- Admin proof list + per-file signed URL opening
- Multipart upload limits raised to 15 MB

---

## ✅ v0.1.5 — Foundation audit
*Shipped 2026-10-13 — no new features*

Full backend audit. Cleaned up debt before adding features again.

### Critical fixes
- NPE on `FlatSpecification` when filters empty
- Wrong field name for `available` filter (`isAvailable` → `available`)
- Deleted reviews counted in `averageRating` and `hasReviews` aggregates
- In-memory pagination from EntityGraph on paginated queries
- Test context broken by `@Profile("!test")` on S3 beans
- `User` soft-delete was actually hard-delete (added `@SQLDelete` + `@SQLRestriction`)
- Aiven schema drift (`missing column [name]`) — backfilled via SQL

### Hardening
- `@SQLDelete` + `@SQLRestriction` on `Review`, `FlatImage`, `ReviewProof`
- `@BatchSize(20)` on `Flat.images` and `Review.proofs` (replaces EntityGraph on paginated queries)
- Added validation to `User` entity and `AddressDto`
- Fixed `User.getUsername()` contract (was returning `firstName`)
- Removed dead code: `UserServiceImpl`, `Issue` feature, unused mappers, duplicate repository

### Tests
- Suite grew: 64 → 67
- Fixed broken assertions (Java `assert` is a no-op without `-ea`)
- Added `-ea` to Surefire
- New tests: `available` filter, self-review prevention, unverify flat

### Config
- `pom.xml` cleanup (removed Postgres, H2 to test scope, versions to properties)
- Removed unused `problemDetails.enabled` and dialect overrides
- `docker-compose.yml` uses MySQL 8.4 (was Postgres)

---

## ✅ v0.1.6 — Upload reliability + schema safety
*Shipped 2026-10-23 — no new features*

Two surgical fixes addressing the biggest reliability problems from v0.1.5.

### Client-side image compression
**Problem:** Render's free tier (512 MB) OOMs when Thumbnailator decodes a
5 MB JPEG into a ~50 MB bitmap. Result: 502 on every upload over ~1 MB.

**Fix:** Compress images in the browser before upload.
- Files >1 MB → resized to 1920px, ~800 KB, JPEG q0.85
- Files <1 MB → pass through untouched
- PDFs → pass through
- Fallback: if compression fails, upload original

**Impact:**
- Upload size reduced ~10× (5 MB → 400 KB)
- Server memory per upload: ~50 MB → ~5 MB
- No more OOM crashes on Render free tier
- No visible quality loss

### Flyway migrations
**Problem:** Schema drift broke prod twice in v0.1.5 — manual `ALTER TABLE`
was forgotten, `ddl-auto: validate` refused to boot.

**Fix:** All schema changes tracked as versioned SQL files.
- `V1__initial_schema.sql` captures current schema (7 tables, all FKs, indexes)
- `baseline-on-migrate: true` (existing DBs baselined at V1, no data loss)
- `ddl-auto: validate` in all profiles
- Future changes become `V2__xxx.sql`, `V3__yyy.sql`, applied on startup

**Impact:**
- No more manual migrations on Aiven
- Every schema change versioned in git
- New environments (staging, preview) created from scratch
- Prod deploys can't silently fail on schema drift

### Tests
- No changes — 67/67 still green
- Manual verification: upload 5 MB photo on prod → succeeds
- Manual verification: Flyway baseline applied cleanly on Aiven

---

## 🚧 v0.1.7 — Presigned uploads + observability
*In progress*

Completes the upload architecture (client compression was step 1) and adds
real-time visibility into prod.

### Presigned S3 URLs (primary)

**Current architecture:**
- Browser → Backend (receives 400KB–20MB) → Thumbnailator → S3 → DB
- Client-side compression reduced typical uploads to ~400 KB, but proof
PDFs up to 20 MB still go through the backend** and risk OOM on the free
tier.

**New architecture:**
1. Browser → Backend: "Presign a URL for flat 47 image upload"
Backend → Browser: { uploadUrl, key, publicUrl }

2. Browser → S3: PUT directly (backend never sees bytes)

3. Browser → Backend: "Confirm. Key=flats/47/abc.jpg, size=400KB"
Backend → DB: INSERT flat_images row
Backend → Browser: { id, url, displayOrder, isPrimary }


**Backend never receives file bytes.** OOM becomes impossible — regardless
of file size.

**Backend**
- [ ] `PresignedUploadService` — generates presigned PUT URLs via `S3Presigner`
- [ ] `POST /flats/{id}/images/presigned-url`
  - Body: `{ filename, contentType, sizeBytes }`
  - Returns: `{ uploadUrl, key, publicUrl, expiresAt }`
  - Validates: MIME type in allowlist, size within limit
  - Uses `S3Presigner.presignPutObject` with 5-min expiry
- [ ] `POST /flats/{id}/images/confirm`
  - Body: `{ key, sizeBytes, contentType, originalFilename }`
  - Verifies: S3 object exists (HEAD request), size matches, MIME matches
  - Generates `FlatImage` entity, sets `displayOrder` and `isPrimary`
  - Returns: `{ id, url, displayOrder, isPrimary }`
- [ ] Same pair for proof uploads (private bucket)
  - `POST /reviews/{id}/proofs/presigned-url`
  - `POST /reviews/{id}/proofs/confirm`
- [ ] Rate limits: max 10 presign requests per user per hour
- [ ] Cleanup job: orphaned S3 objects older than 1 hour (files presigned but never confirmed)
- [ ] **Keep the old multipart endpoints** as fallback for 1 release (safer rollout)

**Frontend**
- [ ] New `usePresignedUpload` hook — orchestrates the 3-step flow
  - 1. Request presigned URL from backend
  - 2. PUT to S3 directly using `fetch` (not Axios — Axios would attach our auth header; S3 doesn't want it)
  - 3. Confirm with backend
- [ ] Show progress bar during direct-to-S3 upload (fetch has no progress; use XHR)
- [ ] Update `ImageGridUploader` and `ProofUploader` to use new hook
- [ ] Error handling:
  - Presigned URL expired → retry once (fresh URL)
  - S3 upload failed → toast "Upload failed, try again"
  - Confirm failed → log object key to console for manual cleanup
- [ ] Compression still runs before presign request (so S3 gets compressed bytes)

**Security**
- [ ] Presigned URL TTL: 5 min (short window)
- [ ] Content-length enforced in presign policy (prevents oversized uploads)
- [ ] Content-type enforced in presign policy
- [ ] Key prefix locked to `flats/{id}/` and `reviews/{id}/` — client can't upload to arbitrary paths
- [ ] Ownership check on presign: same as current upload — owner/creator/ADMIN only

### Prometheus endpoint (secondary)

- [ ] `micrometer-registry-prometheus` dependency in `pom.xml`
- [ ] Expose `/actuator/prometheus` in `management.endpoints.web.exposure.include`
- [ ] Permit `/actuator/prometheus` in `SecurityConfig` (auth required, ADMIN role, or IP allowlist — see discussions)
- [ ] Grafana Cloud account (free tier)
- [ ] Scrape Render backend into Grafana Cloud
- [ ] Basic dashboard: request rate, 5xx rate, p50/p95 latency, JVM heap

### Deferred to v0.1.8

- [ ] Password reset — real feature, can wait one release
- [ ] Denormalized `averageRating` for scale
- [ ] Cloudflare R2 migration (evaluate after presigned URLs shipped)
- [ ] Bottom nav on mobile
- [ ] Loading skeletons
- [ ] SEO / OG tags
---

## 🚧 v0.1.8 — Upload architecture + scale
*Later*

These become worth doing once you have real traffic or hit real limits.

### Presigned S3 URLs
Currently: browser → backend → S3. Backend sees every byte.
After: browser → S3 directly. Backend only stores metadata.

- [ ] Backend: `POST /flats/{id}/images/presigned-url` — returns S3 upload URL + key
- [ ] Backend: `POST /flats/{id}/images/confirm` — accepts key + metadata, saves DB row
- [ ] Same flow for proof uploads (private bucket)
- [ ] Frontend: replace multipart POST with presigned flow
- [ ] Remove multipart upload endpoints (or keep as fallback)

**When to do this:** if users upload 20 MB PDFs, if concurrent uploads
become a bottleneck, or if the free-tier Render starts OOMing again despite
compression.

### Other scale prep
- [ ] Denormalized `averageRating` + `reviewCount` (for 1000+ flats)
- [ ] Cloudflare R2 instead of S3 (zero egress, better latency)
- [ ] Move S3 bucket to `us-west-1` to match Render region
- [ ] Async image processing on a background thread
- [ ] Increase dimension cap (6000×6000 → 8000×8000) after memory work

---

## 🚧 v0.2.0 — Landlord claiming
*Planned*

Turning the listing side of the platform into a first-class citizen.

- [ ] Landlord "claim this flat" flow (email/phone verification)
- [ ] Claimed flats show "Verified by landlord" badge
- [ ] Landlord can mark flat `available = true` → appears in rental listings
- [ ] Reviews from verified stays ranked higher than unverified
- [ ] Admin analytics dashboard (flats/day, reviews/day, moderation queue depth)

---

## 💭 v0.3.0+ — Later

- [ ] Flat amenities as a list (parking, gym, pool, etc.)
- [ ] Map integration (lat/lng on address)
- [ ] Email verification on signup
- [ ] Image uploads on reviews (photos of the flat from tenant)
- [ ] Public API for third-party integrations
- [ ] Custom domain (`rentix.app`)
- [ ] Payment rails (if ever needed for premium listings)

---

## 🗂 Archived — deferred / superseded

Items that were scoped but dropped or replaced:

- ~~`PageResponse<T>` DTO~~ — shipped in v0.1.2
- ~~Address normalization + fuzzy dedup~~ — deferred indefinitely; not a priority at current scale
- ~~Email verification~~ — deferred to v0.3.0+
- ~~Bottom nav in v0.1.6~~ — moved to v0.1.7 (password reset more valuable)
- ~~Prometheus in v0.1.6~~ — moved to v0.1.7 (same reason)

---

## 📅 Cadence

Weekly releases. One PR per week to the upstream repo (`latelespaul/rent-history`).
Each release is scoped to one or two high-impact changes — no mega-PRs.