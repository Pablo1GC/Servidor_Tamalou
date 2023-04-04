-- This script creates four tables: player, friendship, game, and game_player.

-- DROP TABLES (in case they already exist)
DROP TABLE IF EXISTS game_player;
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS player;

-- Each table has its own set of columns and relationships to other tables.

CREATE TABLE player (
                        email VARCHAR(255),
                        username VARCHAR(255),
                        uid VARCHAR(255) PRIMARY KEY,
                        image BLOB
);

-- The player table stores information about each player, including their email, username, unique ID, and image.

CREATE TABLE friendship (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            sender VARCHAR(255),
                            receiver VARCHAR(255),
                            status ENUM('pending', 'accepted', 'rejected') NOT NULL DEFAULT 'pending',
                            FOREIGN KEY (sender) REFERENCES player(uid),
                            FOREIGN KEY (receiver) REFERENCES player(uid)
);

-- The friendship table represents a many-to-many relationship between players. It includes an ID, the unique IDs of the players sending and receiving the friend request, and the status of the request (pending, accepted, or rejected).

CREATE TABLE game (
                      id_game INT PRIMARY KEY AUTO_INCREMENT,
                      num_rounds INT
);

-- The game table stores information about each game, including a unique ID and the number of rounds to be played.

CREATE TABLE game_player (
                             id_game INT,
                             id_user VARCHAR(255),
                             score INT,
                             winner BOOLEAN,
                             PRIMARY KEY (id_game, id_user),
                             FOREIGN KEY (id_game) REFERENCES game(id_game),
                             FOREIGN KEY (id_user) REFERENCES player(uid)
);

-- The game_player table represents a many-to-many relationship between games and players. It includes the unique IDs of the game and player, the player's score, and whether or not they were the winner.


-- INSERT INTO player table
INSERT INTO player (email, username, uid, image) VALUES
                                                     ('john@example.com', 'johndoe', '123456', NULL),
                                                     ('jane@example.com', 'janedoe', '789012', NULL),
                                                     ('bob@example.com', 'bobsmith', '345678', NULL),
                                                     ('alice@example.com', 'alicewonderland', '901234', NULL),
                                                     ('steve@example.com', 'stevecarell', '567890', NULL);

-- INSERT INTO friendship table
INSERT INTO friendship (sender, receiver, status) VALUES
                                                      ('123456', '789012', 'accepted'),
                                                      ('789012', '123456', 'accepted'),
                                                      ('345678', '901234', 'pending'),
                                                      ('901234', '567890', 'rejected'),
                                                      ('567890', '901234', 'accepted');

-- INSERT INTO game table
INSERT INTO game (num_rounds) VALUES
                                  (10),
                                  (5),
                                  (15),
                                  (8),
                                  (12);

-- INSERT INTO game_player table
INSERT INTO game_player (id_game, id_user, score, winner) VALUES
                                                              (1, '123456', 100, true),
                                                              (1, '789012', 80, false),
                                                              (2, '345678', 50, false),
                                                              (2, '567890', 60, true),
                                                              (3, '123456', 150, true),
                                                              (3, '567890', 120, false),
                                                              (4, '901234', 80, true),
                                                              (4, '123456', 70, false),
                                                              (5, '345678', 110, false),
                                                              (5, '567890', 140, true);