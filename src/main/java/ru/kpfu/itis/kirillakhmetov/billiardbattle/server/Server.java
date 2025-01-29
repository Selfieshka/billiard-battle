package ru.kpfu.itis.kirillakhmetov.billiardbattle.server;

import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity.ServerProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(ServerProperties.PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                PrintWriter pr1 = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader br1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PlayerThread player1Thread = new PlayerThread(br1, pr1);
                player1Thread.addThread(player1Thread);
                Thread thread = new Thread(player1Thread);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
