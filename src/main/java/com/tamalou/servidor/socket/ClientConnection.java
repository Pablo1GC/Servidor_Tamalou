package com.tamalou.servidor.socket;

import com.google.gson.Gson;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.User;
import com.tamalou.servidor.modelo.entidad.socketEntities.Package;
import com.tamalou.servidor.modelo.persistencia.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ClientConnection {

    private GameManager gameManager;
    private SignalManager signalManager;

    // @Autowired
    private Gson gson;

    private UserRepository userRepository;

    public ClientConnection(GameManager gameManager, SignalManager signalManager, UserRepository userRepository){
        this.gameManager = gameManager;
        this.signalManager = signalManager;
        this.gson = new Gson();
        this.userRepository = userRepository;
    }

    public void connectClient(Socket clientSocket) {
        try {
            Player player = new Player(clientSocket);

            Package pack = player.reader.readPackage();
            System.out.println("Package: " + pack.toString());
            player.setUid(pack.data.getAsString());

            User player1 = userRepository.findById(player.getUid());
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
}