package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.BilliardBattleApplication;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    TextField username;
    @FXML
    PasswordField password;
    @FXML
    PasswordField repassword;
    @FXML
    Button signUp;
    @FXML
    Button back;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.setOnAction(e -> {
            BilliardBattleApplication.window.setScene(BilliardBattleApplication.login);
        });
        signUp.setOnAction(e -> {
            if (!password.getText().equals(repassword.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Пароли не совпадают");
                alert.show();
            } else {
                if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                    BilliardBattleApplication.outToServer.println("signup#" + username.getText() + "#" + password.getText() + "#" + 0);
                    username.setText("");
                    password.setText("");
                    repassword.setText("");
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка!");
                    alert.setHeaderText("Поля не могут быть пустыми");
                    alert.show();
                }
            }
        });
    }
}
