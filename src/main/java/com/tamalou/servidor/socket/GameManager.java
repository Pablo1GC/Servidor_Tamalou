package com.tamalou.servidor.socket;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Game;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    private HashMap<String, Game> gameHashMap;

    public GameManager() {
        gameHashMap = new HashMap<>();
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
        HashMap<String, Game> publicTournaments = new HashMap<>();
        gameHashMap.forEach((key, value) -> {
            if (!value.isPrivate())
                publicTournaments.put(key, value);
        });
        return publicTournaments;
    }

    public synchronized int joinPlayerToGame(Player player, String gameKey) {
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