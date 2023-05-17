-- This script creates four tables: user, friendship, game, and game_player.

-- DROP TABLES (in case they already exist)
DROP TABLE IF EXISTS game_player;
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS user;
DROP PROCEDURE IF EXISTS get_games_by_player;
DROP PROCEDURE IF EXISTS get_players_and_scores_by_game;
DROP PROCEDURE IF EXISTS get_total_games_played;
DROP PROCEDURE IF EXISTS get_average_score;
DROP PROCEDURE IF EXISTS get_games_won;
DROP PROCEDURE IF EXISTS get_games_lost;
DROP PROCEDURE IF EXISTS get_average_play_time;
DROP PROCEDURE IF EXISTS get_player_game_info;


-- Each table has its own set of columns and relationships to other tables.

CREATE TABLE user
(
    email    VARCHAR(255),
    username VARCHAR(255),
    uid      VARCHAR(255) PRIMARY KEY,
    image    MEDIUMBLOB
);

-- The player table stores information about each player, including their email, username, unique ID, and image.

CREATE TABLE friendship
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    sender   VARCHAR(255),
    receiver VARCHAR(255),
    status   ENUM ('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'pending',
    FOREIGN KEY (sender) REFERENCES user (uid),
    FOREIGN KEY (receiver) REFERENCES user (uid)
);

-- The friendship table represents a many-to-many relationship between players. It includes an ID, the unique IDs of the players sending and receiving the friend request, and the status of the request (pending, accepted, or rejected).

CREATE TABLE game
(
    id_game    INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    num_rounds INT
);

-- The game table stores information about each game, including a unique ID and the number of rounds to be played.

CREATE TABLE game_player
(
    id_game    INT,
    id_user    VARCHAR(255),
    score      INT,
    winner     BOOLEAN,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_game, id_user),
    FOREIGN KEY (id_game) REFERENCES game (id_game),
    FOREIGN KEY (id_user) REFERENCES user (uid)
);

-- The game_player table represents a many-to-many relationship between games and players. It includes the unique IDs of the game and player, the player's score, and whether or not they were the winner. The start_time column is set to the current timestamp by default when a new row is inserted, and the end_time column is updated to the current timestamp automatically when the row is updated.


-- Procedure to get the total number of games played by a player
DELIMITER //
CREATE PROCEDURE get_total_games_played(player_uid VARCHAR(255))
BEGIN
    SELECT COUNT(*) AS total_games_played FROM game_player WHERE id_user = player_uid;
END//
DELIMITER ;

-- Procedure to get the average score of a player in all games played
DELIMITER //
CREATE PROCEDURE get_average_score(player_uid VARCHAR(255))
BEGIN
    SELECT AVG(score) AS average_score FROM game_player WHERE id_user = player_uid;
END//
DELIMITER ;

-- Procedure to get the number of games won by a player
DELIMITER //
CREATE PROCEDURE get_games_won(player_uid VARCHAR(255))
BEGIN
    SELECT COUNT(*) AS games_won FROM game_player WHERE id_user = player_uid AND winner = TRUE;
END//
DELIMITER ;

-- Procedure to get the number of games lost by a player
DELIMITER //
CREATE PROCEDURE get_games_lost(player_uid VARCHAR(255))
BEGIN
    SELECT COUNT(*) AS games_lost FROM game_player WHERE id_user = player_uid AND winner = FALSE;
END//
DELIMITER ;

-- Procedure to get the average play time per game for a player, returns the result in minutes.
DELIMITER //
CREATE PROCEDURE get_average_play_time(player_uid VARCHAR(255))
BEGIN
    SELECT AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_play_time
    FROM (SELECT gp.id_game, gp.id_user, MIN(gp.start_time) AS start_time, MAX(gp.end_time) AS end_time
          FROM game_player gp
                   INNER JOIN game g ON gp.id_game = g.id_game
          WHERE gp.id_user = player_uid
          GROUP BY gp.id_game, gp.id_user) AS play_times;
END//
DELIMITER ;

-- Procedure to get the number of games played

DELIMITER //
CREATE PROCEDURE get_games_played_by_player(player_uid VARCHAR(255))
BEGIN
    SELECT g.*
    FROM game_player gp
        JOIN game g ON gp.id_game = g.id_game
    WHERE gp.id_user = player_uid;
END//
DELIMITER ;

-- procedure to get the players and their scores in a single game

DELIMITER //
CREATE PROCEDURE get_players_and_scores_in_game(game_id INT)
BEGIN
    SELECT u.*, gp.score
    FROM game_player gp
        JOIN user u ON gp.id_user = u.uid
    WHERE gp.id_game = game_id;
END//
DELIMITER ;



DELIMITER //

-- Procedure to retrieve the game information (name, date, score) for a player based on their UID
DELIMITER //

-- Procedure to retrieve the game information (name, date, score) for a player based on their UID
CREATE PROCEDURE get_player_game_info(player_uid VARCHAR(255))
BEGIN
    -- Retrieve the game information for the given player UID
    SELECT g.name AS 'Game Name', DATE_FORMAT(gp.start_time, '%d/%m/%Y %H:%i') AS 'Game Date', gp.score AS 'Player Score', g.id_game AS 'id'
    FROM game_player gp
             INNER JOIN game g ON gp.id_game = g.id_game
    WHERE gp.id_user = player_uid;
END //

DELIMITER ;




-- INSERT INTO player table
INSERT INTO user (email, username, uid, image)
VALUES ('francoernestocollins@gmail.com', 'johndoe', '0gNvZ6L6DnP1LNoiPCQ6mlpom0j1', NULL),
       ('jane@example.com', 'janedoe', '789012', NULL),
       ('bob@example.com', 'bobsmith', '345678', NULL),
       ('alice@example.com', 'alicewonderland', '901234', NULL),
       ('steve@example.com', 'stevecarell', '567890', NULL);

-- INSERT INTO friendship table
INSERT INTO friendship (sender, receiver, status)
VALUES ('0gNvZ6L6DnP1LNoiPCQ6mlpom0j1', '789012', 'accepted'),
       ('789012', '0gNvZ6L6DnP1LNoiPCQ6mlpom0j1', 'accepted'),
       ('345678', '901234', 'pending'),
       ('901234', '567890', 'rejected'),
       ('567890', '901234', 'accepted');

-- INSERT INTO game table
INSERT INTO game (name, num_rounds)
VALUES ('Game 1', 10),
       ('Game 2', 5),
       ('Game 3', 15),
       ('Game 4', 8),
       ('Game 5', 12);


-- INSERT INTO game_player table
INSERT INTO game_player (id_game, id_user, score, winner)
VALUES (1, '0gNvZ6L6DnP1LNoiPCQ6mlpom0j1', 100, true),
       (1, '789012', 80, false),
       (2, '345678', 50, false),
       (2, '567890', 60, true),
       (3, '0gNvZ6L6DnP1LNoiPCQ6mlpom0j1', 150, true),
       (3, '567890', 120, false),
       (4, '901234', 80, true),
       (4, '0gNvZ6L6DnP1LNoiPCQ6mlpom0j1', 70, false),
       (5, '345678', 110, false),
       (5, '567890', 140, true);


DELIMITER //

