package com.tamalou.servidor.controlador;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Friendship;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.FriendshipId;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.FriendshipStatus;
import com.tamalou.servidor.modelo.entidad.socketEntities.JsonField;
import com.tamalou.servidor.modelo.persistencia.FriendshipRepository;
import com.tamalou.servidor.modelo.persistencia.PlayerRepository;
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
    private final PlayerRepository playerRepository;

    /**
     * Constructor for the FriendshipController class.
     *
     * @param friendshipRepository The FriendshipRepository instance to use for data access.
     */
    public FriendshipController(FriendshipRepository friendshipRepository, PlayerRepository playerRepository) {
        this.friendshipRepository = friendshipRepository;
        this.playerRepository = playerRepository;
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
        Player player = playerRepository.findById(userUid);
        List<Friendship> pendingFriendships = friendshipRepository.findPendingFriendshipRequests(player);

        return new ResponseEntity<>(pendingFriendships, HttpStatus.OK);

    }

    /**
     * HTTP POST method for creating a new Friendship.
     *
     * @param senderID The sender id
     * @param receiverID The Receiver id
     * * @return A ResponseEntity with HttpStatus.CREATED if successful, or HttpStatus.BAD_REQUEST if unsuccessful.
     */
    @PostMapping()
    public ResponseEntity<Friendship> createFriendship(@RequestParam(value = "senderId") String senderID,
                                                       @RequestParam(value = "receiverId") String receiverID) {
        Player sender = playerRepository.findById(senderID);
        Player receiver = playerRepository.findById(receiverID);

        Friendship friendship = new Friendship();
        FriendshipId friendshipId = new FriendshipId();
        friendshipId.setReceiverId(receiverID);
        friendshipId.setSenderId(senderID);
        friendship.setId(friendshipId);
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);

        try {
            friendshipRepository.save(friendship);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * HTTP PUT method for updating an existing Friendship.
     *
     * @param senderID The FriendshipId of the Friendship to update.
     * @param receiverID   The updated Friendship object.
     * @return A ResponseEntity with HttpStatus.ACCEPTED if successful, or HttpStatus.NOT_FOUND if the Friendship to update was not found.
     */
    @PutMapping()
    public ResponseEntity<Void> updateFriendship(@RequestParam(value = "senderId") String senderID,
                                                 @RequestParam(value = "receiverId") String receiverID,
                                                 @RequestBody() String decision) {
        Friendship friendship = friendshipRepository.findByUsersId(senderID, receiverID);

        if (friendship == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Gson gson = new Gson();

        friendship.setStatus(FriendshipStatus.valueOf(gson.fromJson(decision, JsonObject.class).get("decision").getAsString()));
        friendshipRepository.update(friendship);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    /**
     * HTTP DELETE method for deleting a Friendship by ID.
     *
     * @param uid1 The uid of the first player.
     * @param uid2 The uid of the second player.
     * @return A ResponseEntity containing the deleted Friendship object if successful, or HttpStatus.CONFLICT if the Friendship to delete was not found.
     */
    @DeleteMapping()
    public ResponseEntity<Friendship> deleteFriendship(@RequestParam(value = "uid1") String uid1,
                                                       @RequestParam(value = "uid2") String uid2) {
        System.out.println("SI");
        Friendship friendship = friendshipRepository.findByUsersId(uid1, uid2);
        if (friendship != null) {
            friendshipRepository.delete(friendship);
            return new ResponseEntity<>(friendship, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

}
