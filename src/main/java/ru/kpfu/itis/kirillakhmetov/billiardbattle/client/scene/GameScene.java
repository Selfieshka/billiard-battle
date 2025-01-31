package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.BilliardBattleApplication;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller.GameController;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Ball;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Player;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Vector;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolMessageCreator;

import java.util.ArrayList;
import java.util.Optional;

import static ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.BallType.*;
import static ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolProperties.*;


public class GameScene {
    private final Stage window;
    private final Group group;
    private final Scene scene;
    private final Scene menu;
    private final GameController gameController;
    private static Ball[] balls = new Ball[16];
    private final boolean[] potted = new boolean[16];
    private static final Player player1 = new Player("");
    private static final Player player2 = new Player("");
    private final ArrayList<Integer> thisTurnPottedBalls = new ArrayList<>();
    private static int turnNum = 1;
    private static boolean isTurn;
    private boolean isFoul;
    private static boolean gameOver;
    private double stack_y = 605;
    private boolean flagForTurnChange;
    private boolean flagForFoulCheck;
    private boolean flagForOtherTypeBallCollisionFoulCheck;
    private boolean flagForNoBallHitFoulCheck = true;

    private static final Label labelForTurnChange = new Label();
    private final Label labelForBallTypePlayer1 = new Label();
    private final Label labelForBallTypePlayer2 = new Label();
    private final Label labelForGameOver = new Label();
    private final Label labelForPlayerWins = new Label();
    private final Label labelForPressAnyKey = new Label();
    private final Label labelForFoul = new Label();
    private static final Label player1label = new Label();
    private static final Label player2Label = new Label();

    private final ImageView[] BallSolid = new ImageView[7];
    private final ImageView[] BallStripes = new ImageView[7];
    private final ImageView[] BallKala = new ImageView[2];

    private final Timeline timeline = new Timeline();
    private static boolean gamePause;
    private static int bet;

    public GameScene(Group group, Scene scene, Scene menu, Parent root, Stage window, GameController gameController) {
        this.window = window;
        this.group = group;
        this.menu = menu;
        this.gameController = gameController;
        this.scene = scene;
        player1label.setLayoutX(240);
        player1label.setLayoutY(10);
        player2Label.setLayoutX(760);
        player2Label.setLayoutY(10);
        player1label.getStyleClass().add("label-player2");
        player2Label.getStyleClass().add("label-player2");
        group.getChildren().addAll(player1label, player2Label);
        ImageView imageView1 = new ImageView();
        ImageView imageView2 = new ImageView();
        imageView1.setLayoutX(100);
        imageView1.setLayoutY(0);
        imageView2.setLayoutX(880);
        imageView2.setLayoutY(0);
        imageView1.setFitWidth(122);
        imageView2.setFitWidth(122);
        imageView1.setFitHeight(122);
        imageView2.setFitHeight(122);

        group.getChildren().addAll(root, imageView1, imageView2);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("/css/menu.css")));
        initializeBalls();
        player1.setMyTurn(true);
        isTurn = true;
        labelForTurnChange.setLayoutX(462);
        labelForTurnChange.setLayoutY(81);
        labelForTurnChange.setText("%s ударил".formatted(player1.getUsername()));
        labelForTurnChange.getStyleClass().add("label-player");
        labelForFoul.setLayoutX(531);
        labelForFoul.setLayoutY(655);
        labelForFoul.setText("ФОЛ!!!");
        labelForFoul.getStyleClass().add("label-player");
        group.getChildren().add(labelForTurnChange);
        group.getChildren().addAll(labelForBallTypePlayer1, labelForBallTypePlayer2);
        group.getChildren().addAll(labelForGameOver, labelForPlayerWins, labelForPressAnyKey, labelForFoul);
        labelForBallTypePlayer1.setVisible(false);
        labelForBallTypePlayer2.setVisible(false);
        labelForGameOver.setVisible(false);
        labelForPlayerWins.setVisible(false);
        labelForPressAnyKey.setVisible(false);
        labelForFoul.setVisible(false);
        for (int i = 0; i < 7; i++) {
            BallSolid[i] = new ImageView(String.valueOf(getClass().getResource("/img/ball2D/Ball" + (i + 1) + ".png")));
            BallStripes[i] = new ImageView(String.valueOf(getClass().getResource("/img/ball2D/Ball" + (i + 9) + ".png")));
            BallSolid[i].setFitWidth(30);
            BallSolid[i].setFitHeight(30);
            BallStripes[i].setFitHeight(30);
            BallStripes[i].setFitWidth(30);
            group.getChildren().addAll(BallSolid[i], BallStripes[i]);
            BallStripes[i].setVisible(false);
            BallSolid[i].setVisible(false);
        }
        for (int i = 0; i < 2; i++) {
            BallKala[i] = new ImageView(String.valueOf(getClass().getResource("/img/ball2D/Ball" + 8 + ".png")));
            BallKala[i].setFitWidth(30);
            BallKala[i].setFitHeight(30);
            BallKala[i].setVisible(false);
            group.getChildren().add(BallKala[i]);
        }
        BallKala[0].setLayoutX(171);
        BallKala[0].setLayoutY(660);
        BallKala[1].setLayoutX(678);
        BallKala[1].setLayoutY(660);
        Button leave = new Button("Покинуть");
        leave.setLayoutX(2);
        leave.setLayoutY(70);
        leave.getStyleClass().add("button-game");
        leave.setOnAction(event -> {
            if (!gameOver) {
                showLeaveAlert();
            }
        });
        group.getChildren().add(leave);
    }

    public void initializeBalls() {
        balls[4] = new Ball(865, 325, "/img/ball3D/4.png", SOLID_BALL, 4);
        balls[12] = new Ball(865, 350, "/img/ball3D/12.png", STRIPED_BALL, 12);
        balls[3] = new Ball(865, 375, "/img/ball3D/3.png", SOLID_BALL, 3);
        balls[9] = new Ball(865, 400, "/img/ball3D/9.png", STRIPED_BALL, 9);
        balls[7] = new Ball(865, 425, "/img/ball3D/7.png", SOLID_BALL, 7);

        balls[1] = new Ball(841, 338, "/img/ball3D/1.png", SOLID_BALL, 1);
        balls[15] = new Ball(841, 363, "/img/ball3D/15.png", STRIPED_BALL, 15);
        balls[2] = new Ball(841, 388, "/img/ball3D/2.png", SOLID_BALL, 2);
        balls[5] = new Ball(841, 413, "/img/ball3D/5.png", SOLID_BALL, 5);

        balls[14] = new Ball(817, 350, "/img/ball3D/14.png", STRIPED_BALL, 14);
        balls[8] = new Ball(817, 375, "/img/ball3D/8.png", BLACK_BALL, 8);
        balls[10] = new Ball(817, 400, "/img/ball3D/10.png", STRIPED_BALL, 10);

        balls[11] = new Ball(793, 363, "/img/ball3D/11.png", STRIPED_BALL, 11);
        balls[6] = new Ball(793, 388, "/img/ball3D/6.png", SOLID_BALL, 6);

        balls[13] = new Ball(769, 375, "/img/ball3D/13.png", STRIPED_BALL, 13);

        balls[0] = new Ball(346, 375, "/img/ball3D/0.png", CUE_BALL, 0);

        for (int i = 0; i < 16; i++) {
            group.getChildren().add(balls[i].DrawBall());
        }
    }

    public static void setVelocity(double x, double y) {
        balls[0].setVelocity(x, y);
        BilliardBattleApplication.outToServer.println(ProtocolMessageCreator.create(SHOT_VELOCITY, x, y));
    }

    private void reInitialize() {
        for (int i = 0; i < 16; i++) {
            group.getChildren().remove(balls[i].getSphere());
        }
        initializeBalls();
        turnNum = 1;
        stack_y = 605;
        flagForTurnChange = false;
        flagForFoulCheck = false;
        flagForOtherTypeBallCollisionFoulCheck = false;
        flagForNoBallHitFoulCheck = true;
        isFoul = false;
        gamePause = false;
        thisTurnPottedBalls.clear();
        for (int i = 0; i < 16; i++) {
            potted[i] = false;
        }
        player1.setMyTurn(true);
        player2.setMyTurn(false);
        gameOver = false;
        isTurn = true;
        labelForBallTypePlayer1.setVisible(false);
        labelForBallTypePlayer2.setVisible(false);
        labelForGameOver.setVisible(false);
        labelForPlayerWins.setVisible(false);
        labelForPressAnyKey.setVisible(false);
        labelForFoul.setVisible(false);
        for (int i = 0; i < 7; i++) {
            BallStripes[i].setVisible(false);
            BallSolid[i].setVisible(false);
        }
        for (int i = 0; i < 2; i++) {
            BallKala[i].setVisible(false);
        }
        player1.setWin(false);
        player2.setWin(false);
        player1.setBallType(null);
        player2.setBallType(null);
        player2.setAllBallsPotted(false);
        player1.setAllBallsPotted(false);
    }

    private void update() {
        if (turnNum == 1) {
            showLabelPlayerIsBreaking();
        }
        boolean flag = false;
        moveCueBall();
        for (int i = 0; i < 16; i++) {
            if (!balls[i].getVelocity().isNull()) {
                flag = true;
                flagForTurnChange = true;
            }
            updateBalls(i);
            checkForPocket(i);
        }
        if (flag) {
            isTurn = false;
        } else {
            if (!flagForTurnChange) {
                isTurn = true;
                showLabelTurnForPlayer();
            } else {
                isFoul = false;
                checkForCases();
                checkAllPottedBalls();
                if (isFoul && !gameOver) {
                    stopGame();
                    showAlert();
                    startFromPause();
                }
                flagForTurnChange = false;
                flagForFoulCheck = false;
                flagForOtherTypeBallCollisionFoulCheck = false;
                flagForNoBallHitFoulCheck = true;
                turnNum++;
                isTurn = true;

                if (thisTurnPottedBalls.contains(0)) {
                    balls[0].setPosition(new Vector(346, 375));
                    balls[0].getSphere().setVisible(true);
                }
                for (int i = 1; i <= 7; i++) {
                    if (potted[i]) {
                        BallSolid[i - 1].setVisible(false);
                    }
                }
                for (int i = 9; i <= 15; i++) {
                    if (potted[i]) {
                        BallStripes[i - 9].setVisible(false);
                    }
                }
                if (player1.isAllBallsPotted()) {
                    BallKala[0].setVisible(true);
                }
                if (player2.isAllBallsPotted()) {
                    BallKala[1].setVisible(true);
                }
                thisTurnPottedBalls.clear();
            }
        }
    }

    private void showLabelPlayerIsBreaking() {
        player1label.setText(player1.getUsername());
        player2Label.setText(player2.getUsername());
        if (player1.isMyTurn())
            labelForTurnChange.setText("%s ударил".formatted(player1.getUsername()));
        else {
            labelForTurnChange.setText("%s ударил".formatted(player2.getUsername()));
        }
    }

    private void showLabelTurnForPlayer() {
        if (player1.isMyTurn()) {
            labelForTurnChange.setText("Ход у %s".formatted(player1.getUsername()));
        } else {
            labelForTurnChange.setText("Ход у %s".formatted(player2.getUsername()));
        }
    }

    private void showLeaveAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Покинуть игру");
        alert.setHeaderText("Вы проиграете, если покинете игру!");
        alert.setContentText("Вы уверены?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                player2.setWin(true);
                player1.setWin(false);
                gameOver = true;
                window.setScene(menu);
                reInitialize();
                BilliardBattleApplication.outToServer.println(TECH_LOSE);
            }
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ФОЛ!!!");

        if (thisTurnPottedBalls.contains(0))
            alert.setHeaderText("Вы забили белый шар");
        else if (flagForOtherTypeBallCollisionFoulCheck)
            alert.setHeaderText("Вы должны попасть по мячу своего типа");
        else
            alert.setHeaderText("Вы должны попасть по шару");

        if (player1.isMyTurn()) {
            alert.setContentText("Шар у %s".formatted(player1.getUsername()));
        } else {
            alert.setContentText("Шар у %s".formatted(player2.getUsername()));
        }
        alert.show();
    }

    private void checkAllPottedBalls() {
        if (player1.getBallType() == null)
            return;
        boolean f = false;
        if (player1.isMyTurn()) {
            checkPottedBallNow(f, player1);
        } else {
            checkPottedBallNow(f, player2);
        }
    }

    private void checkPottedBallNow(boolean f, Player player1) {
        if (player1.getBallType().equals(SOLID_BALL)) {
            for (int i = 1; i <= 7; i++) {
                if (!potted[i]) {
                    f = true;
                    break;
                }
            }
        } else {
            for (int i = 9; i <= 15; i++) {
                if (!potted[i]) {
                    f = true;
                    break;
                }
            }
        }
        if (!f) {
            player1.setAllBallsPotted(true);
        }
    }

    private void updateBalls(int ball_num) {
        if (balls[ball_num].getVelocity().getSize() <= 8e-2) {
            balls[ball_num].setVelocity(0, 0);
        } else {
            balls[ball_num].getPosition().setX(balls[ball_num].getPosition().getX() + balls[ball_num].getVelocity().getX());
            balls[ball_num].getPosition().setY(balls[ball_num].getPosition().getY() + balls[ball_num].getVelocity().getY());
            for (Ball b : balls) {
                if (ball_num != b.getBallNumber() && balls[ball_num].collides(b)) {
                    if (ball_num == 0 && !flagForOtherTypeBallCollisionFoulCheck && player1.getBallType() == null) {
                        flagForOtherTypeBallCollisionFoulCheck = true;
                        if (b.getBallType().equals(BLACK_BALL)) {
                            flagForFoulCheck = true;
                        }
                    }
                    if (ball_num == 0 && !flagForOtherTypeBallCollisionFoulCheck && player1.getBallType() != null) {
                        flagForOtherTypeBallCollisionFoulCheck = true;
                        if (player1.isMyTurn()) {
                            if (!player1.getBallType().equals(b.getBallType())) {
                                flagForFoulCheck = b.getBallNumber() != 8 || !player1.isAllBallsPotted();
                            }
                        } else {
                            if (!player2.getBallType().equals(b.getBallType())) {
                                flagForFoulCheck = b.getBallNumber() != 8 || !player2.isAllBallsPotted();
                            }
                        }
                    }
                    if (ball_num == 0) {
                        flagForNoBallHitFoulCheck = false;
                    }
                    balls[ball_num].getPosition().setX(balls[ball_num].getPosition().getX() - balls[ball_num].getVelocity().getX());
                    balls[ball_num].getPosition().setY(balls[ball_num].getPosition().getY() - balls[ball_num].getVelocity().getY());
                    balls[ball_num].transferEnergy(b);
                    break;
                }
            }
            balls[ball_num].updateWallCollision();
            balls[ball_num].applyTableFriction();
            balls[ball_num].spin();
        }
        balls[ball_num].getSphere().setLayoutX(balls[ball_num].getPosition().getX());
        balls[ball_num].getSphere().setLayoutY(balls[ball_num].getPosition().getY());
    }

    private void moveCueBall() {
        balls[0].getSphere().addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isTurn && isFoul && player1.isMyTurn()) {
                gameController.getStick().setVisible(false);
                gameController.getCircle().setVisible(false);
                gameController.getLine().setVisible(false);
                gameController.getPredictedLine().setVisible(false);
                balls[0].getSphere().setCursor(Cursor.CLOSED_HAND);
                if ((event.getSceneX() <= 937 && event.getSceneX() >= 157) && (event.getSceneY() >= 180 && event.getSceneY() <= 568)) {
                    balls[0].setPosition(new Vector(event.getSceneX(), event.getSceneY()));
                    BilliardBattleApplication.outToServer.println(
                            ProtocolMessageCreator.create(BALL_MOVE, event.getSceneX(), event.getSceneY()));
                }

            } else if (isTurn && turnNum == 1 && player1.isMyTurn()) {
                gameController.getStick().setVisible(false);
                gameController.getCircle().setVisible(false);
                gameController.getLine().setVisible(false);
                gameController.getPredictedLine().setVisible(false);
                balls[0].getSphere().setCursor(Cursor.CLOSED_HAND);
                if ((event.getSceneX() <= 344 && event.getSceneX() >= 155) && (event.getSceneY() >= 170 && event.getSceneY() <= 570)) {
                    balls[0].setPosition(new Vector(event.getSceneX(), event.getSceneY()));
                    BilliardBattleApplication.outToServer.println(
                            ProtocolMessageCreator.create(BALL_MOVE, event.getSceneX(), event.getSceneY()));
                }
            }
        });

    }

    private void checkForCases() {
        boolean flag = false;
        if (turnNum == 1) {
            if (thisTurnPottedBalls.isEmpty()) {
                flag = true;
            } else {
                for (Integer thisTurnPottedBall : thisTurnPottedBalls) {
                    if (thisTurnPottedBall == 8) {
                        loss();
                    } else if (thisTurnPottedBall == 0) {
                        isFoul = true;
                        flag = true;
                    } else potted[thisTurnPottedBall] = true;
                }
            }

        } else if (turnNum >= 2 && player1.getBallType() == null) {
            if (thisTurnPottedBalls.isEmpty()) {
                flag = true;
            } else {
                int firstPuttedBallNum = thisTurnPottedBalls.getFirst();
                if (firstPuttedBallNum >= 1 && firstPuttedBallNum < 8) {
                    if (player1.isMyTurn()) {
                        player1.setBallType(SOLID_BALL);
                        player2.setBallType(STRIPED_BALL);
                    } else {
                        player1.setBallType(STRIPED_BALL);
                        player2.setBallType(SOLID_BALL);
                    }
                    showLabel();
                } else if (firstPuttedBallNum >= 9 && firstPuttedBallNum <= 15) {
                    if (player1.isMyTurn()) {
                        player1.setBallType(STRIPED_BALL);
                        player2.setBallType(SOLID_BALL);
                    } else {
                        player1.setBallType(STRIPED_BALL);
                        player2.setBallType(STRIPED_BALL);
                    }
                    showLabel();
                }
                for (Integer thisTurnPottedBall : thisTurnPottedBalls) {
                    if (thisTurnPottedBall == 8) {
                        loss();
                    } else if (thisTurnPottedBall == 0) {
                        isFoul = true;
                        flag = true;
                    } else potted[thisTurnPottedBall] = true;
                }
            }
        } else {
            if (thisTurnPottedBalls.isEmpty()) {
                flag = true;
            } else if (thisTurnPottedBalls.size() == 1 && thisTurnPottedBalls.getFirst() == 8) {
                if (player1.isMyTurn()) {
                    checkPottedBallType(player1);
                } else {
                    checkPottedBallType(player2);
                }
            } else {
                int firstPuttedBallNum = thisTurnPottedBalls.getFirst();
                if (player1.isMyTurn()) {
                    if (!player1.getBallType().equals(balls[firstPuttedBallNum].getBallType())) {
                        flag = true;
                    }
                    for (Integer thisTurnPottedBall : thisTurnPottedBalls) {
                        if (thisTurnPottedBall == 8) {
                            loss();
                        } else if (thisTurnPottedBall == 0) {
                            isFoul = true;
                            flag = true;
                        } else {
                            potted[thisTurnPottedBall] = true;
                        }
                    }
                } else {
                    if (!player2.getBallType().equals(balls[firstPuttedBallNum].getBallType())) {
                        flag = true;
                    }
                    for (Integer thisTurnPottedBall : thisTurnPottedBalls) {
                        if (thisTurnPottedBall == 8) {
                            loss();
                        } else if (thisTurnPottedBall == 0) {
                            isFoul = true;
                            flag = true;
                        } else {
                            potted[thisTurnPottedBall] = true;
                        }
                    }
                }
            }
        }
        if (flagForFoulCheck || flagForNoBallHitFoulCheck) {
            isFoul = true;
        }
        if (flag || flagForFoulCheck || flagForNoBallHitFoulCheck) {
            changeTurn();
        }
        labelForFoul.setVisible(isFoul);
    }

    private void checkPottedBallType(Player player2) {
        boolean flag = false;
        if (player2.getBallType().equals(SOLID_BALL)) {
            for (int i = 1; i <= 7; i++) {
                if (!potted[i]) {
                    flag = true;
                    loss();
                }
            }
        } else {
            for (int i = 9; i <= 15; i++) {
                if (!potted[i]) {
                    flag = true;
                    loss();
                }
            }
        }
        if (!flag) {
            win();
        }
    }

    private void showLabel() {
        labelForBallTypePlayer1.setVisible(true);
        labelForBallTypePlayer2.setVisible(true);
        labelForBallTypePlayer1.setLayoutX(186);
        labelForBallTypePlayer2.setLayoutX(719);
        labelForBallTypePlayer1.setLayoutY(624);
        labelForBallTypePlayer2.setLayoutY(624);
        if (player1.getBallType().equals(SOLID_BALL)) {
            labelForBallTypePlayer1.setText("%s сплошные".formatted(player1.getUsername()));
            labelForBallTypePlayer2.setText("%s полосатые".formatted(player2.getUsername()));
            int place1 = 360, place2 = 147;
            for (int i = 0; i < 7; i++) {
                BallSolid[i].setLayoutX(531 - place1);
                BallSolid[i].setLayoutY(660);
                BallStripes[i].setLayoutX(531 + place2);
                BallStripes[i].setLayoutY(660);
                place1 -= 45;
                place2 += 45;
                BallStripes[i].setVisible(true);
                BallSolid[i].setVisible(true);
            }
        } else {
            labelForBallTypePlayer1.setText(player1.getUsername() + " is Stripes");
            labelForBallTypePlayer2.setText(player2.getUsername() + " is Solids");
            int place1 = 360;
            int place2 = 147;
            for (int i = 0; i < 7; i++) {
                BallStripes[i].setLayoutX(531 - place1);
                BallSolid[i].setLayoutY(660);
                BallSolid[i].setLayoutX(531 + place2);
                BallStripes[i].setLayoutY(660);
                place1 -= 45;
                place2 += 45;
                BallStripes[i].setVisible(true);
                BallSolid[i].setVisible(true);
            }
        }
        labelForBallTypePlayer1.getStyleClass().add("label-player");
        labelForBallTypePlayer2.getStyleClass().add("label-player");
    }

    private void loss() {
        if (player1.isMyTurn()) {
            player1.setWin(false);
            player2.setWin(true);
        } else {
            player1.setWin(true);
            player2.setWin(false);
        }
        gameOver = true;
    }

    private void win() {
        if (player1.isMyTurn()) {
            player1.setWin(true);
            player2.setWin(false);
        } else {
            player1.setWin(false);
            player2.setWin(true);
        }
        gameOver = true;
    }

    private void gameOver() {
        labelForGameOver.setVisible(true);
        labelForPlayerWins.setVisible(true);
        labelForPressAnyKey.setVisible(true);
        labelForGameOver.setLayoutX(425);
        labelForGameOver.setLayoutY(169);
        labelForPlayerWins.setLayoutX(415);
        labelForPlayerWins.setLayoutY(242);
        labelForPressAnyKey.setLayoutX(240);
        labelForPressAnyKey.setLayoutY(500);
        labelForGameOver.setText("Game Over");
        if (player1.isWin()) labelForPlayerWins.setText(player1.getUsername() + " Wins!");
        else labelForPlayerWins.setText(player2.getUsername() + " Wins!");
        labelForPressAnyKey.setText("Press Any Key To Continue");
        labelForGameOver.getStyleClass().add("label-over");
        labelForPlayerWins.getStyleClass().add("label-over");
        labelForPressAnyKey.getStyleClass().add("label-over");
        if (GameScene.getPlayer1().isWin()) {
            player1.setBalance(player1.getBalance() + bet);
            BilliardBattleApplication.outToServer.println(ProtocolMessageCreator.create(
                    GAME_END, GameScene.getPlayer1().getUsername(),
                    GameScene.getPlayer2().getUsername(), bet)
            );
        } else {
            player1.setBalance(player1.getBalance() - bet);
        }
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            window.setScene(menu);
            startNewGame();
        });
    }

    private void changeTurn() {
        if (player1.isMyTurn()) {
            player1.setMyTurn(false);
            player2.setMyTurn(true);
        } else {
            player1.setMyTurn(true);
            player2.setMyTurn(false);
        }
    }

    private void checkForPocket(int ballNum) {
        double x = balls[ballNum].getPosition().getX(), y = balls[ballNum].getPosition().getY();
        double check = 625;

        if (calculateDistance(x, y, 130, 154) <= check) {
            dropIt(ballNum);
        } else if (calculateDistance(x, y, 550, 151) <= check) {
            dropIt(ballNum);
        } else if (calculateDistance(x, y, 970, 154) <= check) {
            dropIt(ballNum);
        } else if (calculateDistance(x, y, 970, 595) <= check) {
            dropIt(ballNum);
        } else if (calculateDistance(x, y, 550, 600) <= check) {
            dropIt(ballNum);
        } else if (calculateDistance(x, y, 130, 595) <= check) {
            dropIt(ballNum);
        }
        if ((y <= 148 || y >= 602) && !balls[ballNum].isDropped()) {
            dropIt(ballNum);
        }
        if ((x <= 113 || x >= 979) && !balls[ballNum].isDropped()) {
            dropIt(ballNum);
        }
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
    }

    private void dropIt(int ballNum) {
        thisTurnPottedBalls.add(ballNum);
        balls[ballNum].setDropped(true);
        balls[ballNum].setVelocity(0, 0);
        balls[ballNum].setPosition(new Vector(1045, stack_y));

        stack_y -= 25;
        if (ballNum == 0) {
            stack_y += 25;
            balls[0].getSphere().setVisible(false);
            balls[0].setPosition(new Vector(0, 0));
            balls[0].setDropped(false);
        }
    }

    public void startGame() {
        gamePause = false;
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(0.015),
                ae -> {
                    if (!gameOver) {
                        update();
                    } else {
                        gameOver = false;
                        gameOver();
                    }
                });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public void stopGame() {
        timeline.stop();
        gamePause = true;
    }

    public void startFromPause() {
        gamePause = false;
        timeline.play();
    }

    public void startNewGame() {
        stopGame();
        gamePause = false;
        reInitialize();
        timeline.play();
    }

    public static void setGameOver(boolean gameOver) {
        GameScene.gameOver = gameOver;
    }

    public static Ball getCueBall() {
        return balls[0];
    }

    public static void setBet(int bet) {
        GameScene.bet = bet;
    }

    public static Ball[] getBalls() {
        return balls;
    }

    public static void setBalls(Ball[] balls) {
        GameScene.balls = balls;
    }

    public static Player getPlayer1() {
        return GameScene.player1;
    }

    public static Player getPlayer2() {
        return GameScene.player2;
    }


    public static boolean isTurn() {
        return GameScene.isTurn;
    }

    public static boolean isGameOver() {
        return GameScene.gameOver;
    }

    public static boolean isGamePause() {
        return GameScene.gamePause;
    }
}
