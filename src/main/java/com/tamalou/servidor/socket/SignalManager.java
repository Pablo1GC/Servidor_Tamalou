package com.tamalou.servidor.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Game;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.socketEntities.Match;
import com.tamalou.servidor.modelo.entidad.socketEntities.Package;
import com.tamalou.servidor.modelo.entidad.socketEntities.PackageWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.*;

public class SignalManager {

    private ArrayList<Player> lobbyPlayers;

    private ArrayList<Player> gamePlayers;

    private GameManager gameManager;

    // @Autowired
    private final Gson gson;

    public SignalManager(GameManager manager) {
        this.gameManager = manager;
        this.gson = new Gson();
    }


    public void manage(Player player) {
        new Thread(() -> {
            boolean continueManage;
            do {
                try{

                    Package pack = player.reader.readPackage();
                    int signal = pack.signal;

                    System.out.println("Signal from " + player.getUid() + " received by the server: " +signal);


                    continueManage = switch (signal) {
                        case Signal.CREAR_TORNEO_PUBLICO               -> manageCreateGame(player, pack.data.getAsJsonObject(), false);
                        case Signal.CREAR_TORNEO_PRIVADO               -> manageCreateGame(player, pack.data.getAsJsonObject(), true);
                        case Signal.UNIRSE_TORNEO_PUBLICO,
                                Signal.UNIRSE_TORNEO_PRIVADO -> manageJoinGame(player, pack.data.getAsJsonObject());
                        case Signal.SOLICITAR_LISTA_TORNEOS            -> manageGameList(player.writter);
                        default -> false;

                    };
                } catch (NoSuchElementException e){
                    System.out.println("Player with IP: " + player.socket.getInetAddress().getHostName() + " has disconnected.");
                    continueManage = false;
                }

            } while (continueManage);
        }).start();
    }


    private boolean manageGameList(PackageWriter writer){
        HashMap<String, Game> torneosPublicos = gameManager.showGames();
        List<Match> matches = new LinkedList<>();

        torneosPublicos.forEach((key, value) -> {
            String nombre = value.getGameName();
            int jugadores = value.getPlayersList().size();

            Match match = new Match();
            match.name = nombre;
            match.players = jugadores;
            match.key = key;

            matches.add(match);
        });

        writer.packAndWrite(Signal.LISTA_TORNEOS, matches);

        return true;

    }


    private boolean manageJoinGame(Player player, JsonObject data) throws NoSuchElementException {
        System.out.println("Join");

        String key = data.get("key").getAsString();

        int resultSignal = gameManager.joinPlayerToGame(player, key);

        System.out.println("Key: " + key);
        System.out.println("Result: " + resultSignal);

        if(resultSignal == Signal.UNION_EXITOSA_TORNEO)
            player.writter.packAndWrite(Signal.UNION_EXITOSA_TORNEO, gameManager.listGames(key).getGameName());
        else
            player.writter.packAndWrite(resultSignal);


        return resultSignal != Signal.UNION_EXITOSA_TORNEO;
    }


    private boolean manageCreateGame(Player player, JsonObject match, boolean isPrivate) throws NoSuchElementException {

        String gameName = match.get("match_name").getAsString();
        int maxRounds = match.get("match_rounds").getAsInt();

        Game game = new Game(isPrivate, gameName, maxRounds);
        String key = gameManager.addGame(player, game);
        gameManager.joinPlayerToGame(player, key);

        System.out.println(maxRounds);

        if(isPrivate){
            player.writter.packAndWrite(Signal.CLAVE_TORNEO, key);
        }

        player.writter.packAndWrite(Signal.CONEXION_EXITOSA_TORNEO);

        return false;
    }
}