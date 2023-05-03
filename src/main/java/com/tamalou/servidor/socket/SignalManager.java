package com.tamalou.servidor.socket;

import com.google.gson.Gson;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Game;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.socketEntities.Match;
import com.tamalou.servidor.modelo.entidad.socketEntities.Package;

import java.io.PrintStream;
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
            boolean continueManage = false;
            do {
                try{
                    String line = player.reader.nextLine();
                    Package pack = gson.fromJson(line, Package.class);
                    int signal = pack.signal;

                    System.out.println("Signal from " + player.getName() + " received by the server: " +signal);


                    continueManage = switch (signal) {
                        case Signal.CREAR_TORNEO_PUBLICO               -> manageCreateGame(player, line, false);
                        case Signal.CREAR_TORNEO_PRIVADO               -> manageCreateGame(player, line, true);
                        case Signal.UNIRSE_TORNEO_PUBLICO,
                                Signal.UNIRSE_TORNEO_PRIVADO -> mamageJoinGame(player);
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


    private boolean manageGameList(PrintStream writer){
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

        Package<List<Match>> matchPackage = new Package<>(Signal.LISTA_TORNEOS, matches);
        writer.println(gson.toJson(matchPackage));

        return true;

    }


    private boolean mamageJoinGame(Player player) throws NoSuchElementException {
        System.out.println("Join");
        String key = player.reader.nextLine();

        player.writter.println(Signal.ENVIAR_NOMBRE);
        player.name = player.reader.nextLine();

        int result = gameManager.joinPlayerToGame(player, key);

        System.out.println("Key: " + key);
        System.out.println("Result: " + result);
        player.writter.println(result);

        if(result == Signal.UNION_EXITOSA_TORNEO){
            player.writter.println(Signal.NOMBRE_TORNEO);
            player.writter.println(gameManager.listGames(key).getGameName());

        }

        return result != Signal.UNION_EXITOSA_TORNEO;
    }


    private boolean manageCreateGame(Player player, String packageLine, boolean isPrivate) throws NoSuchElementException {

        Match match = gson.fromJson(packageLine, Package.MatchPackage.class).data;

        String gameName = match.name;
        player.name = match.creatorName;

        Game game = new Game(isPrivate, gameName);
        String key = gameManager.addGame(player, game);
        gameManager.joinPlayerToGame(player, key);

        if(isPrivate){
            player.writter.printf("{\"signal\": %d, \"data\": \"%s\"}\n",Signal.CLAVE_TORNEO, key);
        }

        player.writter.printf("{\"signal\": %d}\n", Signal.CONEXION_EXITOSA_TORNEO);

        return false;
    }
}