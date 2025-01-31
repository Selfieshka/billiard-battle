package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.thread;

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
                        case SHOT_VELOCITY ->
                                shotVelocity(Double.parseDouble(responseParts.get(1)), Double.parseDouble(responseParts.get(2)));
                        case CUE_ROTATE -> rotateCue(Double.parseDouble(responseParts.get(1)),
                                Double.parseDouble(responseParts.get(2)),
                                Double.parseDouble(responseParts.get(3)));
                        case PLAYER_HIT -> hitPlayer();
                        case BALL_MOVE -> moveBall(Double.parseDouble(responseParts.get(1)),
                                Double.parseDouble(responseParts.get(2)));
                        case TECH_LOSE -> loseTechnical();
                        case GAME_INIT -> initGame(responseParts.get(1), responseParts.get(2),
                                Integer.parseInt(responseParts.get(3)), responseParts.get(4));
                        case AUTH_LOGIN -> signIn(responseParts);
                        case AUTH_REGISTER -> signUp(responseParts.get(1));
                        case START_ACTIVE_PLAYER_LIST -> startActivePlayerList();
                        case ACTIVE_PLAYER_LIST -> addPlayerToList(responseParts.get(1));
                        case END_ACTIVE_PLAYER_LIST -> endActivePlayerList();
                        case REQUEST_CHALLENGE -> createRequestChallenge(responseParts.get(1), responseParts.get(2));
                        case CANCEL_INVITE -> cancelInvite();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void cancelInvite() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка!");
            alert.setHeaderText("Противник отклонил приглашение!");
            alert.show();
        });
    }

    private void createRequestChallenge(String username, String money) {
        if (username.equals(LOGIC_FALSE)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка");
                alert.setHeaderText("У оппонента не хватает денег");
                alert.setContentText("Количество денег у оппонента %s".formatted(money));
                alert.show();
            });
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Новое приглашение в игру!");
                alert.setHeaderText("%s приглашает вас в игру на %s %s".formatted(username, money, "монет!"));
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    BilliardBattleApplication.outToServer.println(ProtocolMessageCreator.create(GAME_START, username, money));
                } else {
                    BilliardBattleApplication.outToServer.println(ProtocolMessageCreator.create(CANCEL_INVITE, username));
                }
            });
        }
    }

    private void endActivePlayerList() {
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
    }

    private void addPlayerToList(String username) {
        OnlinePlayersController.getStrings().add(username);
    }

    private void startActivePlayerList() {
        OnlinePlayersController.setStrings(FXCollections.observableArrayList());
    }

    private void signUp(String flag) {
        if (flag.equals(LOGIC_TRUE)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Добро пожаловать!");
                alert.setHeaderText("Поздравляем! Регистрация прошла успешно!");
                alert.show();
                BilliardBattleApplication.window.setScene(BilliardBattleApplication.login);
            });
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Такое имя уже занято");
                alert.show();
            });
        }
    }

    private void signIn(List<String> responseParts) {
        if (responseParts.get(1).equals(LOGIC_FALSE)) {
            if (responseParts.size() > 2) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно войти");
                    alert.setContentText("Вы уже вошли в аккаунт на другом устройстве!");
                    alert.show();
                });
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно войти");
                    alert.setContentText("Имя или пароль неверные");
                    alert.show();
                });
            }
        } else {
            try {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Добро пожаловать");
                    alert.setHeaderText("Вошли как %s".formatted(responseParts.get(1)));
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
    }

    private void initGame(String username, String id, Integer bet, String flag) {
        GameScene.getPlayer2().setUsername(username);
        GameScene.getPlayer2().setId(id);
        GameScene.setBet(bet);
        if (flag.equals(LOGIC_TRUE)) {
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
    }

    private void loseTechnical() {
        GameScene.getPlayer1().setWin(true);
        GameScene.getPlayer2().setWin(false);
        GameScene.setGameOver(true);
    }

    private void moveBall(double x, double y) {
        GameScene.getBalls()[0].setPosition(new Vector(x, y));
    }

    private void hitPlayer() {
        gameController.getStick().setVisible(false);
    }

    private void rotateCue(double v, double v1, double v2) {
        gameController.getStick().setVisible(true);
        gameController.getStick().setRotate(v);
        gameController.getStick().setLayoutX(v1);
        gameController.getStick().setLayoutY(v2);
    }

    private void shotVelocity(Double x, Double y) {
        GameScene.getBalls()[0].setVelocity(x, y);
    }
}
