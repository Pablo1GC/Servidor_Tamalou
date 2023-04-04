package com.tamalou.servidor.controlador;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Friendship;
import com.tamalou.servidor.modelo.persistencia.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling HTTP requests related to Friendships.
 */
@RestController
@RequestMapping("/friendships")
public class FriendshipController {

    @Autowired
    private final FriendshipRepository friendshipRepository;

    /**
     * Constructor for the FriendshipController class.
     *
     * @param friendshipRepository The FriendshipRepository instance to use for data access.
     */
    @Autowired
    public FriendshipController(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * HTTP GET method for retrieving all Friendships.
     *
     * @return A ResponseEntity containing a List of Friendship objects if found, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Friendship>> getAllFriendships() {
        return new ResponseEntity<>(friendshipRepository.findAll(), HttpStatus.FOUND);
    }

    /**
     * HTTP GET method for retrieving a Friendship by ID.
     *
     * @param id The ID of the Friendship to retrieve.
     * @return A ResponseEntity containing a Friendship object if found, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Friendship> getFriendshipById(@PathVariable Long id) {
        Friendship friendship = friendshipRepository.findById(id);
        if (friendship != null) {
            return new ResponseEntity<>(friendship, HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP POST method for creating a new Friendship.
     *
     * @param friendship The Friendship object to create.
     * @return A ResponseEntity with HttpStatus.CREATED if successful, or HttpStatus.BAD_REQUEST if unsuccessful.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Friendship> createFriendship(@RequestBody Friendship friendship) {
        try {
            friendshipRepository.save(friendship);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * HTTP PUT method for updating an existing Friendship.
     *
     * @param id         The ID of the Friendship to update.
     * @param friendship The updated Friendship object.
     * @return A ResponseEntity with HttpStatus.ACCEPTED if successful, or HttpStatus.NOT_FOUND if the Friendship to update was not found.
     */
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateFriendship(@PathVariable Long id, @RequestBody Friendship friendship) {
        Friendship existingFriendship = friendshipRepository.findById(id);
        if (existingFriendship != null) {
            friendship.setId(id);
            friendshipRepository.update(friendship);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP DELETE method for deleting a Friendship by ID.
     *
     * @param id The ID of the Friendship to delete.
     * @return A ResponseEntity containing the deleted Friendship object if successful, or HttpStatus.CONFLICT if the Friendship to delete was not found.
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Friendship> deleteFriendship(@PathVariable Long id) {
        Friendship friendship = friendshipRepository.findById(id);
        if (friendship != null) {
            friendshipRepository.delete(friendship);
            return new ResponseEntity<>(friendship, HttpStatus.GONE);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
