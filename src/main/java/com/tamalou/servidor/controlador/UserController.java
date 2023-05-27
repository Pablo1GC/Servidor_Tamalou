package com.tamalou.servidor.controlador;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Friendship;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.PlayerGameInfo;
import com.tamalou.servidor.modelo.persistencia.FriendshipRepository;
import com.tamalou.servidor.modelo.persistencia.PlayerRepository;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * The UserController class is responsible for handling HTTP requests related to users and friendships.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    /**
     * Autowired UserRepository instance to perform Player-related database operations.
     */
    @Autowired
    private PlayerRepository playerRepository;

    /**
     * Autowired FriendshipRepository instance to perform friendship-related database operations.
     */
    @Autowired
    private FriendshipRepository friendshipRepository;


    /**
     * HTTP GET method for retrieving a Player by ID.
     *
     * @param id    The ID of the Player to retrieve.
     * @param token The access token from the fetching Player.
     * @return A ResponseEntity containing the Player or a HttpStatus NOT_FOUND if the Player does not exist.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> getUserById(@PathVariable String id,
                                              @RequestParam(value = "token", required = false) String token) {
        Player Player = playerRepository.findById(id);
        if (Player != null) {

            // Do not provide too much information if the Player
            // fetching the info doesn't validate its identity.
            if (token == null) { // TODO: Validate token
                Player.setEmail(null);
            }


            return new ResponseEntity<>(Player, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP POST method for creating a new Player.
     *
     * @param Player The Player object to create.
     * @return A ResponseEntity containing the created Player or a HttpStatus BAD_REQUEST if the Player could not be created.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createUser(@RequestBody Player Player) {
        try {
            playerRepository.save(Player);
            return new ResponseEntity<>(Player, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }

    /**
     * HTTP PUT method for updating an existing Player.
     *
     * @param id   The ID of the Player to update.
     * @param Player The updated Player object.
     * @return A ResponseEntity containing the updated Player or a HttpStatus NOT_FOUND if the Player does not exist.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> updateUser(@PathVariable String id, @RequestBody Player Player) {
        Player existingUser = playerRepository.findById(id);
        System.out.println(Player.toString());
        if (existingUser != null) {
            Player.setUid(id);
            playerRepository.update(Player);
            return new ResponseEntity<>(Player, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Player, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP DELETE method for deleting a Player by ID.
     *
     * @param id The ID of the Player to be deleted.
     * @return ResponseEntity<Player> The deleted Player with HttpStatus GONE if successful, or HttpStatus CONFLICT if Player not found.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Player> deleteUser(@PathVariable String id) {
        Player Player = playerRepository.findById(id);
        if (Player != null) {
            playerRepository.delete(Player);
            return new ResponseEntity<>(Player, HttpStatus.GONE);
        } else {
            return new ResponseEntity<>(Player, HttpStatus.CONFLICT);
        }
    }

    /**
     * HTTP GET method for retrieving friends of a Player by Player ID.
     *
     * @param uid The Player ID for which friends need to be retrieved.
     * @return ResponseEntity containing a list of Player objects representing the friends of the Player.
     */
    @GetMapping(value = "/{uid}/friends", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Player>> getFriendsByUserId(@PathVariable String uid) {
        List<Friendship> friendships = friendshipRepository.findBySenderUid(uid);
        List<Player> friends = new ArrayList<>();
        for (Friendship friendship : friendships) {
            Player friend = friendship.getReceiver();
            if (friend.getUid().equals(uid)) {
                friend = friendship.getSender();
            }
            friends.add(friend);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }


    /**
     * REST endpoint for retrieving Player statistics.
     *
     * @param uid The unique identifier of the Player.
     * @return A ResponseEntity containing a Map with the Player's statistics if the Player exists, or an error message if not found or an error occurred.
     */
    @GetMapping("/{uid}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String uid) {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalGamesPlayed", playerRepository.getTotalGamesPlayed(uid));
            stats.put("averageScore", playerRepository.getAverageScore(uid));
            stats.put("gamesWon", playerRepository.getGamesWon(uid));
            stats.put("gamesLost", playerRepository.getGamesLost(uid));
            stats.put("averagePlayTime", playerRepository.getAveragePlayTime(uid));
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (NoResultException e) {
            Map<String, Object> error = Collections.singletonMap("error", "Player not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            Map<String, Object> error = Collections.singletonMap("error", "An error occurred while fetching Player stats");
            System.out.println(e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * REST endpoint for retrieving the id of all games played by a Player.
     *
     * @param uid The unique identifier of the Player.
     * @return A ResponseEntity containing the total number of games played by the Player if the Player exists, or an error message if not found or an error occurred.
     */
    @GetMapping("/{uid}/games-played")
    public ResponseEntity<List<Integer>> getTotalGamesPlayed(@PathVariable String uid) {
        ArrayList<Integer> resultList = playerRepository.getGamesPlayed(uid);
        if (!resultList.isEmpty()) {
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{uid}/games/{gameId}/scores")
    public ResponseEntity<List<Object>>  getPlayersAndScores(@PathVariable String uid, @PathVariable String gameId) {
        List<Object> resultList = playerRepository.getPlayersAndScoresInGame(gameId);
        if (!resultList.isEmpty()) {
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{uid}/games")
    public ResponseEntity<List<PlayerGameInfo>>getPlayerGamesInfo(@PathVariable String uid) {
        List<PlayerGameInfo> resultList = playerRepository.getPlayerGamesInfo(uid);
        if (!resultList.isEmpty()) {
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
