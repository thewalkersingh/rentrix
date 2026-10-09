-- Clean slate between tests — HARD delete to bypass soft-delete
-- Order matters: child tables first
DELETE
FROM flat_images;
DELETE
FROM reviews;
DELETE
FROM flats;
DELETE
FROM user_details;
DELETE
FROM users;