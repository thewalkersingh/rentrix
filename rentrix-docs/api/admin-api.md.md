# Admin API Documentation

## Base URL

```http
/api/v1/admin
```

## Authorization

Required Role:

```text
ROLE_ADMIN
```

---

## Verify Flat

### Request

```http
PATCH /admin/flats/{id}/verify
```

### Response

```json
{
  "id": 1,
  "verified": true
}
```

---

## Unverify Flat

### Request

```http
PATCH /admin/flats/{id}/verify
```

### Body

```json
{
  "verified": false
}
```

---

## Show Flat

### Request

```http
PATCH /admin/flats/{id}/visibility
```

### Body

```json
{
  "visible": true
}
```

---

## Hide Flat

### Request

```http
PATCH /admin/flats/{id}/visibility
```

### Body

```json
{
  "visible": false
}
```

---

## Get Review By Id

### Request

```http
GET /admin/reviews/{id}
```

---

## Delete Review

### Request

```http
DELETE /admin/reviews/{id}
```

---

## List Reviews For Moderation

### Request

```http
GET /admin/reviews?status=PENDING&page=0&size=20
```

### Supported Statuses

```text
PENDING
APPROVED
REJECTED
```

### Default Sort

```text
createdAt DESC
```

---

## Moderate Review

### Request

```http
PATCH /admin/reviews/{id}
```

### Body

```json
{
  "status": "APPROVED"
}
```

or

```json
{
  "status": "REJECTED"
}
```

### Response

```json
{
  "id": 15,
  "status": "APPROVED",
  "verifiedStay": true
}
```

---

## Security Notes

- All endpoints require ADMIN role.
- Security is enforced at both SecurityConfig and Controller level.
- Non-admin users receive HTTP 403 Forbidden.

---

## Version

Rentrix v0.1.4

Made with ❤️ by Diwakar Singh
