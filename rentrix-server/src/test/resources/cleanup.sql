-- Clean slate between tests — HARD delete to bypass soft-delete
DELETE
FROM reviews;
DELETE
FROM flats;
DELETE
FROM user_details;
DELETE
FROM users;