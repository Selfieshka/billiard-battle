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
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.util.ClientThread;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity.ServerProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;

import static ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolProperties.LOGOUT;
import static ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolProperties.TECH_LOSE;


public class BilliardBattleApplication extends Application {
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
        window = stage;
        window.setTitle("Billiard Battle");
        window.setResizable(false);
        window.show();

        try {
            clientSocket = new Socket(ServerProperties.HOST, ServerProperties.PORT);
            outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Parent loginParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login.fxml")));
            Parent registerParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/register.fxml")));
            Parent onlinePlayersParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/online-players.fxml")));
            Parent menuParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/menu.fxml")));
            FXMLLoader billiardLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/billiard-table.fxml")));
            Parent billiardParent = billiardLoader.load();

            login = new Scene(loginParent);
            register = new Scene(registerParent);
            onlinePlayers = new Scene(onlinePlayersParent);
            menu = new Scene(menuParent);

            Group gameGroup = new Group();

            game = new Scene(gameGroup, 1100, 700, Color.rgb(51, 102, 153));

            GameScene gameScene = new GameScene(gameGroup, game, menu, billiardParent, window, billiardLoader.getController());
            gameScene.startGame();

            ClientThread clientThread = new ClientThread(inFromServer, billiardLoader.getController());
            Thread t = new Thread(clientThread);
            t.start();

            window.setScene(login);
            windowCloseRequest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void windowCloseRequest() {
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
                    outToServer.println(TECH_LOSE);
                    outToServer.println(LOGOUT);
                    closeAllResources();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit?");
                alert.setHeaderText("Confirmation Dialog");
                alert.setContentText("Sure you want to exit the game?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    if (window.getScene() != login) {
                        outToServer.println(LOGOUT);
                    }
                    closeAllResources();
                }
            }
        });
    }

    private void closeAllResources() {
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
