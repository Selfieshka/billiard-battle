package ru.kpfu.itis.kirillakhmetov.billiardbattle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;

public class MyApp extends Application {
    public static Stage window;
    public static Scene login, register, menu, game, onlinePlayers;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent loginParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/view/login.fxml")));
        Parent registerParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/view/register.fxml")));
        Parent onlinePlayersParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/view/online-players.fxml")));
        FXMLLoader billiardLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/view/billiard-table.fxml")));

        login = new Scene(loginParent);
        register = new Scene(registerParent);
        onlinePlayers = new Scene(onlinePlayersParent);

        window = stage;
        window.setTitle("Billiard Battle");
        window.show();
        window.setScene(login);
        window.setResizable(false);

        Group menuGroup = new Group();
        menu = new Scene(menuGroup, 1100, 700);

        MenuScene menuScene = new MenuScene(menuGroup, window, menu);
//        window.setScene(menu);

        Group gameGroup = new Group();
        game = new Scene(gameGroup, 1100, 700, Color.rgb(51, 102, 153));
        GameScene gameScene = new GameScene(gameGroup, game, menu, billiardLoader.load(),window, billiardLoader.getController());
        gameScene.startGame();
        window.setScene(game);
    }
}
