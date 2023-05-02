CREATE TABLE friends
(
    user1_id  integer NOT NULL REFERENCES filmorate_users (user_id),
    user2_id  integer NOT NULL REFERENCES filmorate_users (user_id),
    confirmed Boolean DEFAULT false,
    PRIMARY KEY (user1_id, user2_id)
);

ALTER TABLE friends
    ADD CONSTRAINT not_self_friend CHECK (user1_id != user2_id);