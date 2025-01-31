package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.BilliardBattleApplication;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolMessageCreator;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolProperties.AUTH_LOGIN;

public class LoginController implements Initializable {
    @FXML
    private AnchorPane parent;
    @FXML
    private Button login;
    @FXML
    private Button register;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        login.setOnAction(e -> {
            BilliardBattleApplication.outToServer.println(ProtocolMessageCreator.create(
                    AUTH_LOGIN, username.getText(), password.getText()));
        });
        register.setOnAction(event -> {
            BilliardBattleApplication.window.setScene(BilliardBattleApplication.register);
        });
    }
}

