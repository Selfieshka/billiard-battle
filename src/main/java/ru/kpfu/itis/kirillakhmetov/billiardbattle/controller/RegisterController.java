package ru.kpfu.itis.kirillakhmetov.billiardbattle.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.MyApp;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    TextField username;
    @FXML
    PasswordField pass;
    @FXML
    PasswordField repass;
    @FXML
    Button signup;
    @FXML
    Button back;
    @FXML
    Pane parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.setOnAction(e -> {
            MyApp.window.setScene(MyApp.login);
        });
        signup.setOnAction(e -> {
            if (pass.getText().compareTo(repass.getText()) != 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Passwords do not match!");
                alert.show();
            } else {
                if (!username.getText().isEmpty() && !pass.getText().isEmpty()) {
                    MyApp.outToServer.println("signup#" + username.getText() + "#" + pass.getText() + "#" + 0);
                    username.setText("");
                    pass.setText("");
                    repass.setText("");
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error!");
                    alert.setHeaderText("No field can be empty");
                    alert.show();
                }
            }
        });
    }
}
