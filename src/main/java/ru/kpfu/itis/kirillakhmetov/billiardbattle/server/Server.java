package ru.kpfu.itis.kirillakhmetov.billiardbattle.server;

import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity.ServerProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            try (ServerSocket serverSocket = new ServerSocket(ServerProperties.PORT)) {
                while (true) {
                    Socket socket = serverSocket.accept();

                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PlayerThread playerThread = new PlayerThread(bufferedReader, printWriter);

                    playerThread.addThread(playerThread);
                    executor.submit(playerThread);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                }
            }
        }
    }
}
