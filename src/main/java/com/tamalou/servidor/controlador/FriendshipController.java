package com.tamalou.servidor.controlador;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Friendship;
import com.tamalou.servidor.modelo.persistencia.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {
    @Autowired
    private final FriendshipRepository friendshipRepository;

    @Autowired
    public FriendshipController(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Friendship>> getAllFriendships() {
        return new ResponseEntity<>(friendshipRepository.findAll(), HttpStatus.FOUND);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Friendship> getFriendshipById(@PathVariable Long id) {
        Friendship friendship = friendshipRepository.findById(id);
        if (friendship != null) {
            return new ResponseEntity<>(friendship, HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Friendship> createFriendship(@RequestBody Friendship friendship) {
        try {
            friendshipRepository.save(friendship);
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }

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
