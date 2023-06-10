package com.tamalou.servidor.controlador;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Friendship;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.FriendshipId;
import com.tamalou.servidor.modelo.persistencia.FriendshipRepository;
import jakarta.websocket.server.PathParam;
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
     * HTTP GET method for retrieving a Friendship by ID.
     *
     * @param friendshipId The FriendshipId of the Friendship to retrieve.
     * @return A ResponseEntity containing a Friendship object if found, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping(path = "/{friendshipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Friendship> getFriendshipById(@PathVariable FriendshipId friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId);
        if (friendship != null) {
            return new ResponseEntity<>(friendship, HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /**
     * HTTP GET method for retrieving a Friendship by id of both parties.
     *
     * @param id1 The ID of the first user.
     * @param id2 The ID of the second user.
     * @return A ResponseEntity containing a Friendship object if found, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Friendship> getFriendshipByUserId(@RequestParam(value = "userId1") String id1,
                                                            @RequestParam(value = "userId2") String id2) {
        Friendship friendship = friendshipRepository.findByUsersId(id1, id2);
        if (friendship != null) {
            return new ResponseEntity<>(friendship, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    /**
     * HTTP GET method for retrieving all pending friendship requests.
     * @return A ResponseEntity with a list of Friendship objects representing pending friendship requests if successful,
     * or an empty list with HttpStatus.OK if no pending requests are found.
     */
    @GetMapping(path = {"/{userUid}/friend-requests"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Friendship>> getPendingFriendshipRequests(@PathVariable String userUid) {

        List<Friendship> pendingFriendships = friendshipRepository.findPendingFriendshipRequests(userUid);
        if (pendingFriendships.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(pendingFriendships, HttpStatus.OK);
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
     * @param friendshipId The FriendshipId of the Friendship to update.
     * @param friendship   The updated Friendship object.
     * @return A ResponseEntity with HttpStatus.ACCEPTED if successful, or HttpStatus.NOT_FOUND if the Friendship to update was not found.
     */
    @PutMapping(path = "/{friendshipId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateFriendship(@PathVariable FriendshipId friendshipId, @RequestBody Friendship friendship) {
        Friendship existingFriendship = friendshipRepository.findById(friendshipId);
        if (existingFriendship != null) {
            friendship.setId(friendshipId);
            friendshipRepository.update(friendship);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /**
     * HTTP DELETE method for deleting a Friendship by ID.
     *
     * @param friendshipId The FriendshipId of the Friendship to delete.
     * @return A ResponseEntity containing the deleted Friendship object if successful, or HttpStatus.CONFLICT if the Friendship to delete was not found.
     */
    @DeleteMapping(path = "/{friendshipId}")
    public ResponseEntity<Friendship> deleteFriendship(@PathVariable FriendshipId friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId);
        if (friendship != null) {
            friendshipRepository.delete(friendship);
            return new ResponseEntity<>(friendship, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

}
