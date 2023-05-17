package com.tamalou.servidor.controlador;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Friendship;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.PlayerGameInfo;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.User;
import com.tamalou.servidor.modelo.persistencia.FriendshipRepository;
import com.tamalou.servidor.modelo.persistencia.UserRepository;
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
     * Autowired UserRepository instance to perform user-related database operations.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Autowired FriendshipRepository instance to perform friendship-related database operations.
     */
    @Autowired
    private FriendshipRepository friendshipRepository;


    /**
     * HTTP GET method for retrieving a user by ID.
     *
     * @param id    The ID of the user to retrieve.
     * @param token The access token from the fetching user.
     * @return A ResponseEntity containing the user or a HttpStatus NOT_FOUND if the user does not exist.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserById(@PathVariable String id,
                                            @RequestParam(value = "token", required = false) String token) {
        User user = userRepository.findById(id);
        if (user != null) {

            // Do not provide too much information if the user
            // fetching the info doesn't validate its identity.
            if (token == null) { // TODO: Validate token
                user.setEmail(null);
            }


            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP POST method for creating a new user.
     *
     * @param user The user object to create.
     * @return A ResponseEntity containing the created user or a HttpStatus BAD_REQUEST if the user could not be created.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            userRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }

    /**
     * HTTP PUT method for updating an existing user.
     *
     * @param id   The ID of the user to update.
     * @param user The updated user object.
     * @return A ResponseEntity containing the updated user or a HttpStatus NOT_FOUND if the user does not exist.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        User existingUser = userRepository.findById(id);
        System.out.println(user.toString());
        if (existingUser != null) {
            user.setUid(id);
            userRepository.update(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP DELETE method for deleting a User by ID.
     *
     * @param id The ID of the User to be deleted.
     * @return ResponseEntity<User> The deleted User with HttpStatus GONE if successful, or HttpStatus CONFLICT if User not found.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable String id) {
        User user = userRepository.findById(id);
        if (user != null) {
            userRepository.delete(user);
            return new ResponseEntity<>(user, HttpStatus.GONE);
        } else {
            return new ResponseEntity<>(user, HttpStatus.CONFLICT);
        }
    }

    /**
     * HTTP GET method for retrieving friends of a User by User ID.
     *
     * @param uid The User ID for which friends need to be retrieved.
     * @return ResponseEntity containing a list of User objects representing the friends of the User.
     */
    @GetMapping(value = "/{uid}/friends", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getFriendsByUserId(@PathVariable String uid) {
        List<Friendship> friendships = friendshipRepository.findBySenderUid(uid);
        List<User> friends = new ArrayList<>();
        for (Friendship friendship : friendships) {
            User friend = friendship.getReceiver();
            if (friend.getUid().equals(uid)) {
                friend = friendship.getSender();
            }
            friends.add(friend);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }


    /**
     * REST endpoint for retrieving user statistics.
     *
     * @param uid The unique identifier of the user.
     * @return A ResponseEntity containing a Map with the user's statistics if the user exists, or an error message if not found or an error occurred.
     */
    @GetMapping("/{uid}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String uid) {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalGamesPlayed", userRepository.getTotalGamesPlayed(uid));
            stats.put("averageScore", userRepository.getAverageScore(uid));
            stats.put("gamesWon", userRepository.getGamesWon(uid));
            stats.put("gamesLost", userRepository.getGamesLost(uid));
            stats.put("averagePlayTime", userRepository.getAveragePlayTime(uid));
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (NoResultException e) {
            Map<String, Object> error = Collections.singletonMap("error", "User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            Map<String, Object> error = Collections.singletonMap("error", "An error occurred while fetching user stats");
            System.out.println(e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * REST endpoint for retrieving the id of all games played by a user.
     *
     * @param uid The unique identifier of the user.
     * @return A ResponseEntity containing the total number of games played by the user if the user exists, or an error message if not found or an error occurred.
     */
    @GetMapping("/{uid}/games-played")
    public ResponseEntity<List<Integer>> getTotalGamesPlayed(@PathVariable String uid) {
        ArrayList<Integer> resultList = userRepository.getGamesPlayed(uid);
        if (!resultList.isEmpty()) {
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{uid}/games/{gameId}/scores")
    public ResponseEntity<List<Object>>  getPlayersAndScores(@PathVariable String uid, @PathVariable String gameId) {
        List<Object> resultList = userRepository.getPlayersAndScoresInGame(gameId);
        if (!resultList.isEmpty()) {
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{uid}/games")
    public ResponseEntity<List<PlayerGameInfo>>getPlayerGamesInfo(@PathVariable String uid) {
        List<PlayerGameInfo> resultList = userRepository.getPlayerGamesInfo(uid);
        if (!resultList.isEmpty()) {
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
