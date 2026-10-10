# Mocks — how to switch from mock to real API

## Overview

Every API call lives in `src/api/*.ts` and follows this pattern:

    USE_MOCKS ? mockXxx() : api.get('/real-endpoint')

The flag `USE_MOCKS` reads `VITE_ENABLE_MOCKS` from `.env`.

## Switching to real backend

1. Ensure Spring Boot exposes all endpoints below.
2. Enable CORS for the frontend origin (see CORS section).
3. In `.env` set:

       VITE_ENABLE_MOCKS=false

4. Restart the dev server (`npm run dev`).
5. Delete `src/api/mock/` if you want (not required — it becomes dead code).

No component or hook changes required.

## Expected endpoints (contract for Spring Boot)

### Auth

| Method | Path | Request | Response |
| -------- | ------ | --------- | ---------- |
| POST | /api/v1/auth/signup | { email, password, name, role } | { user, accessToken, refreshToken } |
| POST | /api/v1/auth/login | { email, password } | { user, accessToken, refreshToken } |
| POST | /api/v1/auth/refresh | { refreshToken } | { accessToken } |
| GET | /api/v1/auth/me | — | User |
| POST | /api/v1/auth/logout | — | 204 |

### Flats

| Method | Path | Query | Response |
|--------|------|-------|----------|
| GET | /api/v1/flats | city, state, minRent, maxRent, numberOfRooms, available, page, size, sort | Page<Flat> |
| GET | /api/v1/flats/:id | — | Flat |

### Reviews

| Method | Path | Request | Response |
| -------- | ------ | --------- | ---------- |
| GET | /api/v1/flats/:id/reviews | page, size | Page<Review> |
| POST | /api/v1/flats/:id/reviews | { title, content, rating } | Review |
| GET | /api/v1/users/me/reviews | — | Page<Review> |
| GET | /api/v1/admin/reviews | status=PENDING | Page<Review> |
| PATCH | /api/v1/admin/reviews/:id | { status: APPROVED\|REJECTED } | Review |

## Data shapes

See `src/types/*.ts` — those interfaces are the contract.

### Flat

    { id, address, city, state, numberOfRooms, area, rent,
      description, isAvailable, averageRating?, reviewCount? }

### Review

    { id, userId, userName, flatId, flatAddress?, title, content,
      rating, reviewDate, status }

### ReviewStatus

    'PENDING' | 'APPROVED' | 'REJECTED'

#### Note

    Newly submitted reviews default to PENDING.
    Only APPROVED reviews appear on the public flat detail page.
    mockMyReviews returns all of the user's reviews regardless of status.

## CORS for Spring Boot (when ready)

In your Spring config add:

    @Configuration
    public class CorsConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "https://your-vercel-url.app")
                .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        }
    }

Or in `application.yml`:

    app:
      cors:
        allowed-origins: http://localhost:5173,https://your-vercel-url.app

## Auth flow (JWT + refresh)

`src/api/client.ts` already implements:

1. Attach `Authorization: Bearer <accessToken>` on every request.
2. On 401 → call `/auth/refresh` once.
3. Retry original request with new access token.
4. If refresh fails → clear tokens → call `onUnauthorized()` (redirect to login).

Zustand store (Step 5) will call `tokenStore.set(access, refresh)` on login and
`tokenStore.clear()` on logout, and register `onUnauthorized` to redirect.

## Mock users (login credentials in mock mode)

| Email | Role | Password (any ≥4 chars) |
| ------- | ------ | -------------------------- |
| <tenant@rentrix.test> | TENANT | anything |
| <landlord@rentrix.test> | LANDLORD | anything |
| <admin@rentrix.test> | ADMIN | anything |

## Gotchas

- `maybeFail(0.02)` in `flats.mock.ts` randomly fails 2% of requests to exercise error UI. Remove when annoying.
- Mock DB is in-memory — refreshing the page resets everything.
- `mockMe()` always returns user id 1 (Aarav). Change during auth work.

## Status (updated YYYY-MM-DD)

### ✅ Deployed and working

- Frontend: <https://myrentrix.vercel.app>
- Backend: <https://rentrix-wj1j.onrender.com>
- Database: Aiven PostgreSQL

### ✅ Endpoints live on backend

- GET /api/v1/flats (paginated)
- GET /api/v1/flats/:id
- GET /api/v1/reviews
- GET /api/v1/reviews/:id

### 🔜 Endpoints still to build (Phase 2C)

- POST /api/v1/auth/signup
- POST /api/v1/auth/login
- POST /api/v1/auth/refresh
- GET  /api/v1/auth/me
- POST /api/v1/auth/logout
- POST /api/v1/flats/:id/reviews
- GET  /api/v1/users/me/reviews
- GET  /api/v1/admin/reviews
- PATCH /api/v1/admin/reviews/:id

### 🎭 Mocks currently in frontend

- src/api/mock/ still exists but USE_MOCKS=false in prod
- Delete the mock folder after Phase 2C ships

## File Storage

### Buckets

| Bucket | Visibility | Purpose |
| --- | --- | --- |
| `rentrix-media` | Public read | Flat photos |
| `rentrix-private` | Private | Proof-of-living documents (future) |

**Region:** `ap-south-1` (Mumbai)
**Public base URL:** `https://rentrix-media.s3.ap-south-1.amazonaws.com`

### Key structure

```
    rentrix-media/
    ├── flats/{flatId}/{uuid}.jpg ← flat photos
    └── users/{userId}/{uuid}.jpg ← avatars (future)

    rentrix-private/
    └── reviews/{reviewId}/{uuid}.pdf ← proof-of-living (future)
```

- **Entity-first layout** — cleanup on delete becomes `s3.deleteByPrefix("flats/{id}/")`
- **UUID filenames** — collision-proof, no PII leak
- **Original filename** stored in DB only, never in the S3 key

### Image processing pipeline

1. **Validate** — size ≤10 MB, MIME type ∈ {image/jpeg, image/png, image/webp}
2. **Re-encode** via Thumbnailator — strips EXIF (GPS, camera data), max 1920px longest edge, JPEG quality 0.85
3. **Upload** to S3 with `Cache-Control: public, max-age=31536000, immutable`
4. **Persist** metadata in `flat_images` (storage_key, public_url, size_bytes, display_order, is_primary)

### Rules

- Max **10 images per flat**
- First upload auto-becomes `is_primary = true`
- Deleting the primary auto-promotes the next image
- Delete is soft (DB) + best-effort (S3); orphaned S3 objects are acceptable
- Only **owner, creator, or ADMIN** can upload/delete/set-primary

### Local development

Credentials in `.env` (gitignored):

```
AWS_REGION=ap-south-1
AWS_ACCESS_KEY=...
AWS_SECRET_KEY=...
AWS_BUCKET=rentrix-media
AWS_PRIVATE_BUCKET=rentrix-private
AWS_PUBLIC_URL=https://rentrix-media.s3.ap-south-1.amazonaws.com
```

**Do not commit `.env`.** In prod, Render environment variables.

### Proof of Living

Reviews can optionally include proof that the tenant lived at the flat.
Documents are stored in **`rentrix-private`** (not public).

#### Storage layout
```
rentrix-private/
└── reviews/{reviewId}/{uuid}.{pdf|jpg}

```


#### Pipeline

1. **Upload** — `POST /reviews/{reviewId}/proof` (owner only, PENDING only)
2. **Validate** — PDF, JPG, PNG, WebP; max 20 MB
3. **Process**
   - PDF → passed through unchanged
   - Images → Thumbnailator: EXIF stripped, max 2400px, JPEG output
4. **Store** — private bucket, no public URL
5. **Verify** — admin approves the review → `proof_verified = true`
6. **Display** — public reviews show "Verified stay" badge only after admin approval

#### Access

- Public visitors never see the file
- Owner and admin get a **15-minute presigned URL** via `GET /reviews/{id}/proof-url`
- URL expires automatically; no persistent links

#### Lifecycle

| State | proofStorageKey | proofVerified | Public badge |
|---|---|---|---|
| No proof | null | false | — |
| Uploaded, pending | set | false | — |
| Approved | set | true | "Verified stay" |
| Rejected | set | false | — |
| Deleted | null | false | — |

#### Rules

- One proof per review
- Only the review owner can upload
- Upload allowed only while review status is PENDING
- Admin approval implicitly verifies the proof
- Rejection keeps the file for audit but clears `verified` flag