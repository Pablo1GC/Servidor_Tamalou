package com.tamalou.servidor.socket;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Game;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.persistencia.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    private ConcurrentHashMap<String, Game> gameHashMap;

    public GameManager() {
        gameHashMap = new ConcurrentHashMap<>();
    }

    public String addGame(Player owner, Game game) {
        boolean incorrect = true;
        String gameKey;
        do {
            gameKey = randomString(10);
            if (gameHashMap.get(gameKey) == null) {
                gameHashMap.put(gameKey, game);
                System.out.println("Tournament created, key: " + gameKey);
                incorrect = false;
            }
        } while (incorrect);
        return gameKey;
    }

    public Game listGames(String gameKey) {
        return gameHashMap.get(gameKey);
    }


    /**
     * ¿Por qué synchronized?
     *
     * @return
     */
    public synchronized HashMap<String, Game> showGames() {
        HashMap<String, Game> publicGames = new HashMap<>();
        gameHashMap.forEach((key, value) -> {
            if (!value.isPrivate())
                publicGames.put(key, value);
        });
        return publicGames;
    }

    public synchronized int joinPlayerToGame(Player player, String gameKey) {
        for(Map.Entry<String, Game> entry : gameHashMap.entrySet()){
            Game game = entry.getValue();

            for (Player p : game.getPlayersList()){
                if (p.getUid().equals(player.getUid())){
                    player.setPoints(p.getPoints());
                    game.getPlayersList().remove(p);
                    p = null;
                    game.getPlayersList().add(player);
                }
            }
        }
        if (gameHashMap.get(gameKey) == null)
            return Signal.TORNEO_INEXISTENTE;

        if (gameHashMap.get(gameKey).getPlayersList().size() == 4) {
            return Signal.TORNEO_LLENO;
        } else {
            gameHashMap.get(gameKey).addPlayer(player);
            return Signal.UNION_EXITOSA_TORNEO;
        }
    }


    private String randomString(int length) {
        // Characters
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder finalString = new StringBuilder();
        for (int x = 0; x < length; x++) {
            int randomIndex = randomIndexInRange(0, characters.length() - 1);
            char randomCharacter = characters.charAt(randomIndex);
            finalString.append(randomCharacter);
        }
        return finalString.toString();
    }

    private int randomIndexInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


}