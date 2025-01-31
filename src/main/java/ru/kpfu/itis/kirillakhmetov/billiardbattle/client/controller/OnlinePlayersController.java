package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.BilliardBattleApplication;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene.GameScene;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolMessageCreator;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolProperties.REQUEST_CHALLENGE;

public class OnlinePlayersController implements Initializable {
    @FXML
    private Button back;
    @FXML
    private Button play;
    @FXML
    private TextField money;
    @FXML
    private ListView<String> active;
    private static ObservableList<String> strings;

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
                BilliardBattleApplication.outToServer.println(ProtocolMessageCreator.create(
                        REQUEST_CHALLENGE, active.getSelectionModel().getSelectedItem(), money.getText()));
            }
        });
    }

    public static ObservableList<String> getStrings() {
        return OnlinePlayersController.strings;
    }

    public static void setStrings(ObservableList<String> strings) {
        OnlinePlayersController.strings = strings;
    }
}
