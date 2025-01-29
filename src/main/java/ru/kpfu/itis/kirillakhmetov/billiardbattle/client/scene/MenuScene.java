package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.BilliardBattleApplication;

public class MenuScene {
    public MenuScene(Group group, Stage window, Scene current) {
        Button gameButton = new Button("New Game");
        gameButton.getStyleClass().add("button-orange2");
        gameButton.setLayoutX(100);
        gameButton.setLayoutY(250 - 75);
        gameButton.setOnAction(e -> {
            BilliardBattleApplication.outToServer.println("active#" + GameScene.getPlayer1().getUsername());
        });

        Button logout = new Button("Logout");
        logout.getStyleClass().add("button-orange2");
        logout.setLayoutX(100);
        logout.setLayoutY(250 + 75 + 75 + 75 + 75);

        current.getStylesheets().add(String.valueOf(getClass().getResource("/css/menu.css")));
        group.getChildren().addAll(gameButton, logout);

        logout.setOnAction(event -> {
            BilliardBattleApplication.outToServer.println("logout");
            window.setScene(BilliardBattleApplication.login);
        });
    }
}
