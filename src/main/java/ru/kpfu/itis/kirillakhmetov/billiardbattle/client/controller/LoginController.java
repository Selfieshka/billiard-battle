package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.BilliardBattleApplication;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    AnchorPane parent;
    @FXML
    Button login;
    @FXML
    Button register;
    @FXML
    TextField username;
    @FXML
    PasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        login.setOnAction(e -> {
            BilliardBattleApplication.outToServer.println("login#" + username.getText() + "#" + password.getText());
        });
        register.setOnAction(event -> {
            BilliardBattleApplication.window.setScene(BilliardBattleApplication.register);
        });
    }
}

