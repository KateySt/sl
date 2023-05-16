--liquibase formatted sql
--changeset kate:inset-position-01
INSERT INTO position_entity (position)
VALUES ('manager');
INSERT INTO position_entity (position)
VALUES ('java');
INSERT INTO position_entity (position)
VALUES ('photograph');
INSERT INTO position_entity (position)
VALUES ('painter');
INSERT INTO position_entity (position)
VALUES ('engineer');
INSERT INTO position_entity (position)
VALUES ('javascript');
INSERT INTO position_entity (position)
VALUES ('typescript');
INSERT INTO position_entity (position)
VALUES ('data engineer');

----
INSERT INTO user_position ( position_id,user_id)
VALUES (1,1);
INSERT INTO user_position ( position_id,user_id)
VALUES (1,2);
INSERT INTO user_position ( position_id,user_id)
VALUES (1,3);
INSERT INTO user_position ( position_id,user_id)
VALUES (2,4);
INSERT INTO user_position ( position_id,user_id)
VALUES (2,5);
INSERT INTO user_position ( position_id,user_id)
VALUES (2,6);
INSERT INTO user_position ( position_id,user_id)
VALUES (2,7);
INSERT INTO user_position ( position_id,user_id)
VALUES (3,8);
INSERT INTO user_position ( position_id,user_id)
VALUES (3,9);
INSERT INTO user_position ( position_id,user_id)
VALUES (3,10);
INSERT INTO user_position ( position_id,user_id)
VALUES (1,10);

INSERT INTO user_position ( position_id,user_id)
VALUES (4,11);
INSERT INTO user_position ( position_id,user_id)
VALUES (4,12);
INSERT INTO user_position ( position_id,user_id)
VALUES (4,13);
INSERT INTO user_position ( position_id,user_id)
VALUES (4,14);
INSERT INTO user_position ( position_id,user_id)
VALUES (4,15);
INSERT INTO user_position ( position_id,user_id)
VALUES (5,16);
INSERT INTO user_position ( position_id,user_id)
VALUES (5,17);
INSERT INTO user_position ( position_id,user_id)
VALUES (5,18);
INSERT INTO user_position ( position_id,user_id)
VALUES (5,19);
INSERT INTO user_position ( position_id,user_id)
VALUES (8,20);
INSERT INTO user_position ( position_id,user_id)
VALUES (6,20);

INSERT INTO user_position ( position_id,user_id)
VALUES (6,21);
INSERT INTO user_position ( position_id,user_id)
VALUES (6,22);
INSERT INTO user_position ( position_id,user_id)
VALUES (6,23);
INSERT INTO user_position ( position_id,user_id)
VALUES (5,24);
INSERT INTO user_position ( position_id,user_id)
VALUES (3,21);
INSERT INTO user_position ( position_id,user_id)
VALUES (1,22);
INSERT INTO user_position ( position_id,user_id)
VALUES (4,23);
INSERT INTO user_position ( position_id,user_id)
VALUES (6,24);
INSERT INTO user_position ( position_id,user_id)
VALUES (7,25);
INSERT INTO user_position ( position_id,user_id)
VALUES (7,26);
INSERT INTO user_position ( position_id,user_id)
VALUES (7,27);
INSERT INTO user_position ( position_id,user_id)
VALUES (7,28);
INSERT INTO user_position ( position_id,user_id)
VALUES (4,29);
INSERT INTO user_position ( position_id,user_id)
VALUES (2,30);
INSERT INTO user_position ( position_id,user_id)
VALUES (3,30);
---
-- changeset kate:4
INSERT INTO position_entity (position)
VALUES ('Frontend developer');

INSERT INTO user_position ( position_id,user_id)
VALUES (9,32);
INSERT INTO user_position ( position_id,user_id)
VALUES (4,32);