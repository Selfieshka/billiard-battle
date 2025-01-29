package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.MyApp;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene.GameScene;

import java.net.URL;
import java.util.ResourceBundle;

public class OnlinePlayersController implements Initializable {
    @FXML
    Pane parent;
    @FXML
    Button back;
    @FXML
    Button play;
    @FXML
    TextField money;
    @FXML
    ListView<String> active;
    public static ObservableList<String> strings;
    double x = 0, y = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeDragable();
        back.setOnAction(e -> {
            MyApp.window.setScene(MyApp.menu);
        });
        active.getStylesheets().add(String.valueOf(getClass().getResource("/css/list-view.css")));
        active.setItems(strings);
        active.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        play.setOnAction(e -> {
            if (money.getText().isEmpty() || active.getSelectionModel().getSelectedItem().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error!");
                alert.setHeaderText("Please select a opponent and amount of money to bet!!");
                alert.show();
            } else if (GameScene.getPlayer1().getBalance() < Integer.parseInt(money.getText())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error!");
                alert.setHeaderText("Not enough money!!");
                alert.show();
            } else {
                MyApp.outToServer.println("canPlay#" + active.getSelectionModel().getSelectedItem() + "#" + money.getText());
            }
        });
    }

    private void makeDragable() {
        parent.setOnMousePressed(((event) -> {
            x = event.getSceneX();
            y = event.getSceneY();
        }));

        parent.setOnMouseDragged(((event) -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
            stage.setOpacity(0.8f);
        }));

        parent.setOnDragDone(((event) -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setOpacity(1.0f);
        }));

        parent.setOnMouseReleased(((event) -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setOpacity(1.0f);
        }));
    }
}
