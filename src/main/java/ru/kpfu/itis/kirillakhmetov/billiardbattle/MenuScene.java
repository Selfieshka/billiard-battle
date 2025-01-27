package ru.kpfu.itis.kirillakhmetov.billiardbattle;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

public class MenuScene {
    private Group group;
    private Stage window;
    private ImageView imageView;
    private Button gameButton, exitbutton, logout;

    public MenuScene(Group group, Stage window, Scene current) {
        this.group = group;
        this.window = window;
        imageView = new ImageView();
//        imageView.setImage(new Image("sample/Cover Pics/Main-Game.jpg"));
        gameButton = new Button("New Game");
        exitbutton = new Button("Exit");
        logout = new Button("Logout");
        current.getStylesheets().add(String.valueOf(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/menu.css")));
        gameButton.getStyleClass().add("button-orange2");
        exitbutton.getStyleClass().add("button-orange2");
        logout.getStyleClass().add("button-orange2");
        gameButton.setLayoutX(100);
        gameButton.setLayoutY(250 - 75);
        exitbutton.setLayoutX(100);
        exitbutton.setLayoutY(250 + 75 + 75 + 75 + 75 + 75);
        logout.setLayoutX(100);
        logout.setLayoutY(250 + 75 + 75 + 75 + 75);
        group.getChildren().addAll(imageView, gameButton, exitbutton, logout);
//        gameButton.setOnAction(e -> {
//            Main.outToServer.println("active#" + GameScene.getPlayer1().getName());
//        });
        exitbutton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit?");
            alert.setHeaderText("Confirmation Dialog");
            alert.setContentText("Sure you want to exit the game?");
            Optional<ButtonType> result = alert.showAndWait();
//            if (result.get() == ButtonType.OK) {
//                Application.outToServer.println("logout");
//                window.close();
//                try {
//                    Application.outToServer.close();
//                    Application.inFromServer.close();
//                    Application.clientSocket.close();
//                } catch (Exception e1) {
//
//                }
//                System.exit(0);
//            }
        });
//        profileButton.setOnAction(e -> {
//            Main.outToServer.println("profile#" + GameScene.getPlayer1().getName());
//        });
//        leaderboardButton.setOnAction(e -> {
//            Main.outToServer.println("leaderboard");
//        });
//        logout.setOnAction(event -> {
//            Main.outToServer.println("logout");
//            window.setScene(Main.login);
//        });
    }
}
