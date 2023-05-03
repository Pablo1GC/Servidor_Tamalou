package com.tamalou.servidor.socket;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Game;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class SignalManager {

    private ArrayList<Player> lobbyPlayers;

    private ArrayList<Player> gamePlayers;

    private GameManager gameManager;

    public SignalManager(GameManager manager) {
        this.gameManager = manager;
    }


    public void manage(Player player) {
        new Thread(() -> {
            boolean continueManage = false;
            do {
                try{
                    String strResult = player.reader.nextLine();
                    int signal = Signal.ERROR;

                    signal = Integer.parseInt(strResult);
                    System.out.println("Signal from " + player.getName() + " received by the server: " +signal);


                    continueManage = switch (signal) {
                        case Signal.CREAR_TORNEO_PUBLICO               -> manageCreateGame(player, false);
                        case Signal.CREAR_TORNEO_PRIVADO               -> manageCreateGame(player, true);
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
        HashMap<String, Game> publicGames = gameManager.showGames();
        writer.println(Signal.LISTA_TORNEOS);
        writer.println(publicGames.size());
        publicGames.forEach((key, value) -> {
            String name = value.getGameName();
            String players = value.getPlayersList().size() + " /  4";

            writer.println(
                    name + Signal.SEPARADOR +
                    players + Signal.SEPARADOR +
                    key);
        });

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


    private boolean manageCreateGame(Player player, boolean isPrivate) throws NoSuchElementException {
        String gameName = player.reader.nextLine();
        System.out.println("Info:" + gameName);

        player.writter.println(Signal.ENVIAR_NOMBRE);
        player.name = player.reader.nextLine();

        Game game = new Game(isPrivate, gameName);
        String key = gameManager.addGame(player, game);
        gameManager.joinPlayerToGame(player, key);

        if(isPrivate){
            player.writter.println(Signal.CLAVE_TORNEO);
            player.writter.println(key);
        }

        player.writter.println(Signal.CONEXION_EXITOSA_TORNEO);

        return false;
    }


    
    
}