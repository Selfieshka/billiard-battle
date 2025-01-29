package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.BilliardBattleApplication;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene.GameScene;

import java.net.URL;
import java.util.ResourceBundle;

public class OnlinePlayersController implements Initializable {
    @FXML
    Button back;
    @FXML
    Button play;
    @FXML
    TextField money;
    @FXML
    ListView<String> active;
    public static ObservableList<String> strings;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.setOnAction(e -> {
            BilliardBattleApplication.window.setScene(BilliardBattleApplication.menu);
        });

        active.getStylesheets().add(String.valueOf(getClass().getResource("/css/list-view.css")));
        active.setItems(strings);
        active.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        play.setOnAction(e -> {
            if (money.getText().isEmpty() || active.getSelectionModel().getSelectedItem().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Выберите соперника и укажите сумму денег");
                alert.show();
            } else if (GameScene.getPlayer1().getBalance() < Integer.parseInt(money.getText())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Не хватает денег");
                alert.show();
            } else {
                BilliardBattleApplication.outToServer.println("canPlay#" + active.getSelectionModel().getSelectedItem() + "#" + money.getText());
            }
        });
    }
}
