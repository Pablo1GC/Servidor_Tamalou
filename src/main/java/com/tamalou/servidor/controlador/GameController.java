package com.tamalou.servidor.controlador;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Game;
import com.tamalou.servidor.modelo.persistencia.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling HTTP requests related to games.
 */
@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    /**
     * HTTP GET method for retrieving all games.
     *
     * @return A ResponseEntity containing a List of Game objects if found, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        return new ResponseEntity<>(gameRepository.findAll(), HttpStatus.OK);
    }

    /**
     * HTTP GET method for retrieving a game by ID.
     *
     * @param id The ID of the game to retrieve.
     * @return A ResponseEntity containing a Game object if found, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable int id) {
        Game game = gameRepository.findById(id);
        if (game != null) {
            return new ResponseEntity<>(game, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP POST method for creating a new game.
     *
     * @param game The Game object to create.
     * @return A ResponseEntity with HttpStatus.CREATED if successful, or HttpStatus.BAD_REQUEST if unsuccessful.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createGame(@RequestBody Game game) {
        try {
            gameRepository.save(game);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * HTTP PUT method for updating an existing game.
     *
     * @param id   The ID of the game to update.
     * @param game The updated Game object.
     * @return A ResponseEntity with HttpStatus.ACCEPTED if successful, or HttpStatus.NOT_FOUND if the game to update was not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateGame(@PathVariable long id, @RequestBody Game game) {
        Game existingGame = gameRepository.findById(id);
        if (existingGame != null) {
            game.setId(id);
            gameRepository.update(game);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP DELETE method for deleting a game by ID.
     *
     * @param id The ID of the game to delete.
     * @return A ResponseEntity containing the deleted Game object if successful, or HttpStatus.CONFLICT if the game to delete was not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Game> deleteGame(@PathVariable int id) {
        Game game = gameRepository.findById(id);
        if (game != null) {
            gameRepository.delete(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}