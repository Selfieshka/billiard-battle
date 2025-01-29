package ru.kpfu.itis.kirillakhmetov.billiardbattle.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene.GameScene;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene.MenuScene;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity.ServerProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;


public class MyApp extends Application {
    public static Stage window;
    public static Scene login, register, menu, game, onlinePlayers;
    public static Socket clientSocket;
    public static PrintWriter outToServer;
    public static BufferedReader inFromServer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            clientSocket = new Socket(ServerProperties.HOST, ServerProperties.PORT);
            outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent loginParent;
        Parent registerParent;
//        Parent onlinePlayersParent;
        Parent billiardParent;
        FXMLLoader billiardLoader;
        try {
            loginParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login.fxml")));
            registerParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/register.fxml")));
//            onlinePlayersParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/view/online-players.fxml")));
            billiardLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/billiard-table.fxml")));
            billiardParent = billiardLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        login = new Scene(loginParent);
        register = new Scene(registerParent);
//        onlinePlayers = new Scene(onlinePlayersParent);

        window = stage;
        window.setTitle("Billiard Battle");
        window.show();

        Group menuGroup = new Group();
        menu = new Scene(menuGroup, 1100, 700);

        MenuScene menuScene = new MenuScene(menuGroup, window, menu);
//        window.setScene(menu);

        Group gameGroup = new Group();
        game = new Scene(gameGroup, 1100, 700, Color.rgb(51, 102, 153));
        GameScene gameScene = null;
        try {
            gameScene = new GameScene(gameGroup, game, menu, billiardParent, window, outToServer, inFromServer, billiardLoader.getController());
//            gameScene = new GameScene(gameGroup, game, menu, billiardParent, window, billiardLoader.getController());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        gameScene.startGame();
//        window.setScene(game);

        ClientThread clientThread;
        try {
            clientThread = new ClientThread(outToServer, inFromServer, billiardLoader.getController());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Thread t = new Thread(clientThread);
        t.start();
        customizeWindow();
    }

    private void customizeWindow() {
        window.setScene(login);
        window.setResizable(false);
        window.setOnCloseRequest(e -> {
            e.consume();
            if (window.getScene() == game) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit?");
                alert.setHeaderText("Confirmation Dialog");
                alert.setContentText("You will lose the game if you leave!!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    GameScene.getPlayer2().setWin(true);
                    GameScene.getPlayer1().setWin(false);
                    GameScene.setGameOver(true);
                    outToServer.println("Lt");
                    outToServer.println("logout");
                    window.close();

                    try {
                        outToServer.close();
                        inFromServer.close();
                        clientSocket.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    System.exit(0);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit?");
                alert.setHeaderText("Confirmation Dialog");
                alert.setContentText("Sure you want to exit the game?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    if (window.getScene() != login)
                        outToServer.println("logout");
                    window.close();
                    try {
                        outToServer.close();
                        inFromServer.close();
                        clientSocket.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    System.exit(0);
                }
            }
        });
    }
}
