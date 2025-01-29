package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.MyApp;

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
    TextField txtfield;
    @FXML
    PasswordField passfield;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        login.setOnAction(e -> {
            MyApp.outToServer.println("login#" + txtfield.getText() + "#" + passfield.getText());
        });
        register.setOnAction(event -> {
            MyApp.window.setScene(MyApp.register);
        });
    }
}

