package com.tamalou.servidor.controlador;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Friendship;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.User;
import com.tamalou.servidor.modelo.persistencia.FriendshipRepository;
import com.tamalou.servidor.modelo.persistencia.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
/**

 The UserController class is responsible for handling HTTP requests related to users and friendships.
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
     * HTTP GET method for retrieving a list of all users or users by username.
     *
     * @param username (optional) The username to search for.
     * @return A ResponseEntity containing the list of users or a HttpStatus NOT_FOUND if no users are found.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(value = "username", required = false) String username) {
        List<User> users;
        if (username != null) {
            users = userRepository.findByUsernameContainingIgnoreCase(username);
        } else {
            users = userRepository.findAll();
        }
        if (!users.isEmpty()) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP GET method for retrieving a user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A ResponseEntity containing the user or a HttpStatus NOT_FOUND if the user does not exist.
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userRepository.findById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.FOUND);
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
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        User existingUser = userRepository.findById(id);
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
    @DeleteMapping(path = "/{id}")
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
    @GetMapping(path = "/{uid}/friends", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getFriendsByUserId(@PathVariable String uid) {
        List<Friendship> friendships = friendshipRepository.findBySenderUid(uid);
        List<User> friends = new ArrayList<>();
        for (Friendship friendship : friendships) {
            User friend = friendship.getReceiver();
            if (friend.getUid().equals(uid)) {
                friend = friendship.getSender();
            }
            friend.setEmail(null);
            friends.add(friend);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }


}
