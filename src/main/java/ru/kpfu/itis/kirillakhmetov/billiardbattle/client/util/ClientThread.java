package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.BilliardBattleApplication;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller.GameController;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller.OnlinePlayersController;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Vector;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene.GameScene;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolMessageCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolProperties.*;

public class ClientThread implements Runnable {
    private final BufferedReader inFromServer;
    private final GameController gameController;

    public ClientThread(BufferedReader inFromServer, GameController gameController) {
        this.inFromServer = inFromServer;
        this.gameController = gameController;
    }

    public void run() {
        while (true) {
            try {
                String response = inFromServer.readLine();
                if (response != null) {
                    System.out.println("FROM SERVER: " + response);

                    List<String> responseParts = Arrays.stream(response.split(DELIMITER))
                            .filter(val -> !val.isEmpty())
                            .toList();

                    switch (responseParts.getFirst()) {
                        case SHOT_VELOCITY:
                            GameScene.getBalls()[0].setVelocity(
                                    Double.parseDouble(responseParts.get(1)),
                                    Double.parseDouble(responseParts.get(2))
                            );
                            break;
                        case CUE_ROTATE:
                            gameController.getStick().setVisible(true);
                            gameController.getStick().setRotate(Double.parseDouble(responseParts.get(1)));
                            gameController.getStick().setLayoutX(Double.parseDouble(responseParts.get(2)));
                            gameController.getStick().setLayoutY(Double.parseDouble(responseParts.get(3)));
                            break;
                        case PLAYER_HIT:
                            gameController.getStick().setVisible(false);
                            break;
                        case BALL_MOVE:
                            GameScene.getBalls()[0].setPosition(new Vector(
                                    Double.parseDouble(responseParts.get(1)),
                                    Double.parseDouble(responseParts.get(2))
                            ));
                            break;
                        case TECH_LOSE:
                            GameScene.getPlayer1().setWin(true);
                            GameScene.getPlayer2().setWin(false);
                            GameScene.setGameOver(true);
                            break;
                        case GAME_INIT:
                            GameScene.getPlayer2().setUsername(responseParts.get(1));
                            GameScene.getPlayer2().setId(responseParts.get(2));
                            GameScene.setBet(Integer.parseInt(responseParts.get(3)));
                            if (responseParts.get(4).equals(LOGIC_TRUE)) {
                                GameScene.getPlayer2().setMyTurn(false);
                                GameScene.getPlayer1().setMyTurn(true);
                            } else {
                                GameScene.getPlayer1().setMyTurn(false);
                                GameScene.getPlayer2().setMyTurn(true);
                            }
                            Platform.runLater(() -> {
                                try {
                                    BilliardBattleApplication.window.setScene(BilliardBattleApplication.game);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            });
                            break;
                        case AUTH_LOGIN:
                            if (responseParts.get(1).equals(LOGIC_FALSE)) {
                                if (responseParts.size() > 2) {
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error!");
                                        alert.setHeaderText("Can't log you in!");
                                        alert.setContentText("You are already logged in from another device!");
                                        alert.show();
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error!");
                                        alert.setHeaderText("Can't log you in!");
                                        alert.setContentText("User name or password is not correct!");
                                        alert.show();
                                    });
                                }
                            } else {
                                try {
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Welcome");
                                        alert.setHeaderText("Logged in as " + responseParts.get(1));
                                        alert.show();
                                        BilliardBattleApplication.window.setScene(BilliardBattleApplication.menu);
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                GameScene.getPlayer1().setUsername(responseParts.get(1));
                                GameScene.getPlayer1().setId(responseParts.get(2));
                                GameScene.getPlayer1().setBalance(Integer.parseInt(responseParts.get(3)));
                            }
                            break;
                        case AUTH_REGISTER:
                            if (responseParts.get(1).equals(LOGIC_TRUE)) {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Welcome");
                                    alert.setHeaderText("Congratulations! Signed up successful!");
                                    alert.show();
                                    BilliardBattleApplication.window.setScene(BilliardBattleApplication.login);
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Error!");
                                    alert.setHeaderText("Username already in use");
                                    alert.show();
                                });
                            }
                            break;
                        case "startActive":
                            OnlinePlayersController.setStrings(FXCollections.observableArrayList());
                            break;
                        case ACTIVE_PLAYER_LIST:
                            OnlinePlayersController.getStrings().add(responseParts.get(1));
                            break;
                        case "endActive":
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        BilliardBattleApplication.onlinePlayers = new Scene(FXMLLoader.load(Objects.requireNonNull(
                                                getClass().getResource("/view/online-players.fxml"))));
                                        BilliardBattleApplication.window.setScene(BilliardBattleApplication.onlinePlayers);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                        case REQUEST_CHALLENGE:
                            if (responseParts.get(1).equals(LOGIC_FALSE)) {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Error!");
                                    alert.setHeaderText("Opponent doesn't have enough money!!");
                                    alert.setContentText("Opponents balance is " + responseParts.get(2));
                                    alert.show();
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("New Game Request!");
                                    alert.setHeaderText("%s challenged you to a match of %s %s".formatted(responseParts.get(1), responseParts.get(2), "dollars!"));
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get() == ButtonType.OK) {
                                        BilliardBattleApplication.outToServer.println(ProtocolMessageCreator.create(GAME_START, responseParts.get(1), responseParts.get(2)));
                                    } else {
                                        BilliardBattleApplication.outToServer.println(ProtocolMessageCreator.create(CANCEL_INVITE, responseParts.get(1)));
                                    }
                                });
                            }
                            break;
                        case CANCEL_INVITE:
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Ошибка!");
                                alert.setHeaderText("Противник отклонил приглашение!");
                                alert.show();
                            });
                            break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
