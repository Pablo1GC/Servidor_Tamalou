package com.tamalou.servidor.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Game;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.socketEntities.JsonField;
import com.tamalou.servidor.modelo.entidad.socketEntities.Match;
import com.tamalou.servidor.modelo.entidad.socketEntities.Package;
import com.tamalou.servidor.modelo.entidad.socketEntities.PackageWriter;
import com.tamalou.servidor.modelo.persistencia.FriendshipRepository;
import com.tamalou.servidor.modelo.persistencia.GameRepository;
import com.tamalou.servidor.modelo.persistencia.PlayerRepository;

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

    private FriendshipRepository friendshipRepository;
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;

    private ClientConnection clientConnection;


    public SignalManager(GameManager manager, FriendshipRepository friendshipRepository, PlayerRepository playerRepository, GameRepository gameRepository) {
        this.gameManager = manager;
        this.gson = new Gson();
        this.friendshipRepository = friendshipRepository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
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
                        case Signal.CREATE_PUBLIC_GAME                 -> manageCreateGame(player, pack.data.getAsJsonObject(), false);
                        case Signal.CREATE_PRIVATE_GAME                -> manageCreateGame(player, pack.data.getAsJsonObject(), true);
                        case Signal.JOIN_PUBLIC_GAME,
                                Signal.JOIN_PRIVATE_GAME               -> manageJoinGame(player, pack.data.getAsJsonObject());
                        case Signal.REQUEST_PUBLIC_GAME_LIST           -> manageGameList(player.writter);
                        case Signal.INVITE_PLAYER                      -> manageInvitePlayer(player.writter, pack.data.getAsJsonObject());
                        case Signal.REQUEST_FRIENDS_STATUS             -> manageRequestFriendsStatus(player);
                        case Signal.YES -> true; // keep managing if signal from isConnected is received
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

        if(resultSignal == Signal.UNION_EXITOSA_TORNEO){

            Game game = gameManager.listGames(key);
            player.writter.packAndWrite(Signal.UNION_EXITOSA_TORNEO,
                    new JsonField("game_name", game.getGameName()),
                    new JsonField("game_uid", key),
                    new JsonField("num_players", game.getPlayersList().size()));
        } else
            player.writter.packAndWrite(resultSignal);


        return resultSignal != Signal.UNION_EXITOSA_TORNEO;
    }


    private boolean manageCreateGame(Player player, JsonObject match, boolean isPrivate) throws NoSuchElementException {
        String gameName = match.get("match_name").getAsString();
        int maxRounds = match.get("match_rounds").getAsInt();

        Game game = new Game(isPrivate, gameName, maxRounds, gameManager, player);
        String key = gameManager.addGame(player, game);

        System.out.println(maxRounds);

        player.writter.packAndWrite(Signal.SUCCESSFUL_MATCH_CREATION, key);

        return false;
    }

    private boolean manageInvitePlayer(PackageWriter writer, JsonObject invitation){

        String sender_uid = invitation.get("sender_uid").getAsString();

        String sender_username = this.playerRepository.findById(sender_uid).getUsername();

        String friend_uid = invitation.get("receiver_uid").getAsString();
        String game_uid = invitation.get("game_uid").getAsString();

        Player friend = clientConnection.getConnectedPlayers().get(friend_uid);

        if (friend != null){
            friend.writter.packAndWrite(Signal.INVITATION_RECEIVED,
                    new JsonField("game_uid", game_uid),
                    new JsonField("sender_username", sender_username));
        }


        return true;
    }

    private boolean manageRequestFriendsStatus(Player player){
        List<Player> friends = friendshipRepository.findByUserId(player.getUid()).stream().map(friendship ->
                // Get user friends. (the friend uid, not the player's)
                friendship.getReceiver().getUid().equals(player.getUid())
                        ? friendship.getSender() : friendship.getReceiver()
        ).toList();

        friends.forEach((friend) -> {
            if (clientConnection.getConnectedPlayers().containsKey(friend.getUid())){
                friend.setConnected(true);
            }
        });

        player.writter.packAndWrite(Signal.FRIENDS_STATUS, friends);

        return true;
    }

    public void setClientConnection(ClientConnection clientConnection){this.clientConnection = clientConnection;}

    public GameRepository getGameRepository() {return gameRepository;}
}