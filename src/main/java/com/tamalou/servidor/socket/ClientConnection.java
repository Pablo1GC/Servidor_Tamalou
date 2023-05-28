package com.tamalou.servidor.socket;

import com.google.gson.Gson;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.socketEntities.Package;
import com.tamalou.servidor.modelo.persistencia.FriendshipRepository;
import com.tamalou.servidor.modelo.persistencia.PlayerRepository;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class ClientConnection {

    private GameManager gameManager;
    private SignalManager signalManager;
    private final ConcurrentHashMap<String, Player> connectedPlayers;

    // @Autowired
    private Gson gson;

    private PlayerRepository playerRepository;
    private FriendshipRepository friendshipRepository;

    public ClientConnection(GameManager gameManager, SignalManager signalManager, PlayerRepository playerRepository){
        this.gameManager = gameManager;
        this.signalManager = signalManager;
        this.gson = new Gson();
        this.playerRepository = playerRepository;
        this.connectedPlayers = new ConcurrentHashMap<>();
    }

    public void connectClient(Socket clientSocket) {
        try {
            Player player = new Player(clientSocket);

            Package pack = player.reader.readPackage();
            System.out.println("Package: " + pack.toString());
            player.setUid(pack.data.getAsString());

            Player player1 = playerRepository.findById(player.getUid());
            player.setUsername(player1.getUsername());
            player.setImage(player1.getImage());
            player.setPoints(0);


            System.out.println("Player uid: " + player.getUid());
            int connectionSignal = pack.signal;
            if (connectionSignal == Signal.CONECTARSE)
                player.writter.packAndWrite(Signal.CONEXION_EXITOSA);
            else {
                clientSocket.close();
                return;
            }

            System.out.println("SERVER: Player with IP " + player.socket.getInetAddress().getHostName() + " has connected.");
            getConnectedPlayers().put(player.getUid(), player);

            signalManager.manage(player);

        } catch (IOException e) {
            System.err.println("SERVER: Input/Output Error");
            e.printStackTrace();
        } catch (NoSuchElementException e){
            System.out.println("SERVER: Client disconnected.");
        } catch (Exception e) {
            System.err.println("SERVER: Error -> " + e);
            e.printStackTrace();
        }
    }

    public void handleConnectedPlayers(){
        new Thread(() -> {
            while (true) {
                try {
                    synchronized (this) {

                        for (var iterator = connectedPlayers.entrySet().iterator(); iterator.hasNext(); ){
                            var player = iterator.next().getValue();
                            if (player.socket.isClosed() || !player.socket.isConnected()) {
                                iterator.remove();
                                System.out.println("Removed disconnected player: " + player.getUsername());
                            }
                        }

                        wait(1000);
                    }
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    public synchronized ConcurrentHashMap<String, Player> getConnectedPlayers(){ return this.connectedPlayers;}
}