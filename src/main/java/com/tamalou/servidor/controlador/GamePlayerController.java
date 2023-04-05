package com.tamalou.servidor.controlador;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.GamePlayer;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.GamePlayerId;
import com.tamalou.servidor.modelo.persistencia.GamePlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


/**

 REST controller for managing game players.
 */
@RestController
@RequestMapping("/game-players")
public class GamePlayerController {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    /**

     HTTP GET method for getting all game players.
     @return List of GamePlayer objects with a HttpStatus.OK status.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GamePlayer>> getAllGamePlayers() {
        return new ResponseEntity<>(gamePlayerRepository.findAll(), HttpStatus.OK);
    }
    /**

     HTTP GET method for getting a specific game player by its game ID and user ID.
     @param gameId The ID of the game.
     @param userId The ID of the user.
     @return GamePlayer object with a HttpStatus.FOUND status if found or a HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping(path = "/{gameId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GamePlayer> getGamePlayerById(@PathVariable long gameId, @PathVariable String userId) {
        GamePlayerId id = new GamePlayerId(gameId, userId);
        GamePlayer gamePlayer = gamePlayerRepository.findById(id);
        if (gamePlayer != null) {
            return new ResponseEntity<>(gamePlayer, HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    /**

     HTTP POST method for creating a new game player.
     @param gamePlayer The GamePlayer object to create.
     @return GamePlayer object with a HttpStatus.CREATED status if successful or a HttpStatus.BAD_REQUEST if unsuccessful.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GamePlayer> createGamePlayer(@RequestBody GamePlayer gamePlayer) {
        try {
            gamePlayerRepository.save(gamePlayer);
            return new ResponseEntity<>(gamePlayer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    /**

     HTTP PUT method for updating an existing game player.
     @param gameId The ID of the game.
     @param userId The ID of the user.
     @param gamePlayer The updated GamePlayer object.
     @return GamePlayer object with a HttpStatus.OK status if successful or a HttpStatus.NOT_FOUND if the game player is not found.
     */
    @PutMapping(path = "/{gameId}/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GamePlayer> updateGamePlayer(@PathVariable Long gameId, @PathVariable String userId, @RequestBody GamePlayer gamePlayer) {
        GamePlayerId id = new GamePlayerId(gameId, userId);
        GamePlayer existingGamePlayer = gamePlayerRepository.findById(id);
        if (existingGamePlayer != null) {
            gamePlayer.setId(id);
            gamePlayerRepository.update(gamePlayer);
            return new ResponseEntity<>(gamePlayer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    /**

     HTTP DELETE method for deleting a game player by its game ID and user ID.
     @param gameId The ID of the game.
     @param userId The ID of the user.
     @return HttpStatus.NO_CONTENT if the game player is successfully deleted or HttpStatus.NOT_FOUND if the game player is not found.
     */
    @DeleteMapping(path = "/{gameId}/{userId}")
    public ResponseEntity<Void> deleteGamePlayerById(@PathVariable Long gameId, @PathVariable String userId) {
        GamePlayerId id = new GamePlayerId(gameId, userId);
        Optional<GamePlayer> gamePlayer = Optional.ofNullable(gamePlayerRepository.findById(id));
        if (gamePlayer.isPresent()) {
            gamePlayerRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * HTTP GET method for getting all game players by game ID.
     *
     * @param gameId The ID of the game.
     * @return List of GamePlayer objects with a HttpStatus.OK status.
     */
    @GetMapping(path = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GamePlayer>> getGamePlayerByGameId(@PathVariable long gameId) {
        List<GamePlayer> gamePlayers = gamePlayerRepository.findByGameId(gameId);
        if (!gamePlayers.isEmpty()) {
            return new ResponseEntity<>(gamePlayers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



}
