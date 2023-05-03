package com.tamalou.servidor.socket;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ClientConnection {

    private GameManager gameManager;
    private SignalManager signalManager;

    public ClientConnection(GameManager gameManager, SignalManager signalManager){
        this.gameManager = gameManager;
        this.signalManager = signalManager;
    }

    public void connectClient(Socket clientSocket) {
        try {
            Player player = new Player(clientSocket);

            int connectionSignal = Integer.parseInt(player.reader.nextLine());
            if (connectionSignal == Signal.CONECTARSE)
                player.writter.println(Signal.CONEXION_EXITOSA);
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