package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.BilliardBattleApplication;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene.GameScene;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolMessageCreator;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolProperties.ACTIVE_PLAYER_LIST;

public class MenuController implements Initializable {
    @FXML
    private Button gameButton;

    public void initialize(URL url, ResourceBundle rb) {
        gameButton.setOnAction(e -> {
            BilliardBattleApplication.outToServer.println(ProtocolMessageCreator.create(
                    ACTIVE_PLAYER_LIST, GameScene.getPlayer1().getUsername()));
        });
    }
}