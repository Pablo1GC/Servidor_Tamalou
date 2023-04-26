package com.tamalou.servidor;

import com.tamalou.servidor.socket.ClientConnection;
import com.tamalou.servidor.socket.SignalManager;
import com.tamalou.servidor.socket.TournamentManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@SpringBootApplication
public class Main {
    public static final int PUERTO = 9191;

    public static void main(String[] args) {

        // Spring
        //SpringApplication.run(Main.class, args);

        TournamentManager manejadorTorneos = new TournamentManager();
        SignalManager signalManager = new SignalManager(manejadorTorneos);
        ClientConnection clientConnection = new ClientConnection(manejadorTorneos, signalManager);
        Socket socketAlCliente = null;
        InetSocketAddress direccion = new InetSocketAddress(PUERTO);
        try (ServerSocket serverSocket = new ServerSocket()){
            serverSocket.bind(direccion);
            do {
                socketAlCliente = serverSocket.accept();
                clientConnection.conectarCliente(socketAlCliente);
            } while (true);
        } catch (IOException e) {
            System.err.println("SERVIDOR: Error de entrada/salida");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("SERVIDOR: Error -> " + e);
            e.printStackTrace();
        }
    }
}
