package com.tamalou.servidor.socket;

import com.google.gson.Gson;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.socketEntities.Package;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ClientConnection {

    private GameManager gameManager;
    private SignalManager signalManager;

    // @Autowired
    private Gson gson;

    public ClientConnection(GameManager gameManager, SignalManager signalManager){
        this.gameManager = gameManager;
        this.signalManager = signalManager;
        this.gson = new Gson();
    }

    public void connectClient(Socket clientSocket) {
        try {
            Player player = new Player(clientSocket);

            String line = player.reader.nextLine();
            int connectionSignal = gson.fromJson(line, Package.class).signal;
            if (connectionSignal == Signal.CONECTARSE)
                player.writter.println(gson.toJson(new Package<>(Signal.CONEXION_EXITOSA, null)));
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