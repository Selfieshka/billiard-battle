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

import java.util.ArrayList;
import java.util.Optional;


public class GameScene {
    private final Stage window;
    private final Group group;
    private final Scene scene;
    private final Scene menu;
    private final GameController gameController;
    public static Ball[] ball;
    private static Player player1;
    private static Player player2;
    private static int turnNum = 1;
    private static boolean isTurn;
    private boolean isFoul;
    private static boolean gameOver;
    private double stack_y = 605;
    private int flagForTurnChange;
    private int flagForFoulCheck;
    private int flagForOtherTypeBallCollisionFoulCheck;
    private int flagForNoBallHitFoulCheck = 1;
    private final ArrayList<Integer> thisTurnPottedBalls;

    private static final Label labelForTurnChange = new Label();
    private final Label labelForBallTypePlayer1 = new Label();
    private final Label labelForBallTypePlayer2 = new Label();
    private final Label labelForBestOfLuck = new Label();
    private final Label labelForGameOver = new Label();
    private final Label labelForPlayerWins = new Label();
    private final Label labelForPressAnyKey = new Label();
    private final Label labelForFoul = new Label();

    private final ImageView[] BallSolid = new ImageView[7];
    private final ImageView[] BallStripes = new ImageView[7];
    private final ImageView[] BallKala = new ImageView[2];

    private final boolean[] potted = new boolean[16];
    private static boolean TurnOffSounds;
    private final Timeline timeline = new Timeline();
    private static boolean gamePause;
    private static Label player1label;
    private static Label player2Label;
    private static int bet;

    public GameScene(Group group, Scene scene, Scene menu, Parent root, Stage window, GameController gameController) {
        ball = new Ball[16];
        this.window = window;
        this.group = group;
        this.menu = menu;
        this.gameController = gameController;
        this.scene = scene;
        player1label = new Label();
        player2Label = new Label();
        player1label.setLayoutX(240);
        player1label.setLayoutY(10);
        player2Label.setLayoutX(760);
        player2Label.setLayoutY(10);
        player1label.getStyleClass().add("label-player2");
        player2Label.getStyleClass().add("label-player2");
        group.getChildren().addAll(player1label, player2Label);
        player1 = new Player("");
        player2 = new Player("");
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
        thisTurnPottedBalls = new ArrayList<>();
        player1.setMyTurn(true);
        isTurn = true;
        labelForTurnChange.setLayoutX(462);
        labelForTurnChange.setLayoutY(81);
        labelForTurnChange.setText(player1.getUsername() + " Is Breaking");
        labelForTurnChange.getStyleClass().add("label-player");
        labelForFoul.setLayoutX(531);
        labelForFoul.setLayoutY(655);
        labelForFoul.setText("FOUL!!");
        labelForFoul.getStyleClass().add("label-player");
        group.getChildren().add(labelForTurnChange);
        group.getChildren().addAll(labelForBallTypePlayer1, labelForBallTypePlayer2, labelForBestOfLuck);
        group.getChildren().addAll(labelForGameOver, labelForPlayerWins, labelForPressAnyKey, labelForFoul);
        labelForBallTypePlayer1.setVisible(false);
        labelForBallTypePlayer2.setVisible(false);
        labelForBestOfLuck.setVisible(false);
        labelForGameOver.setVisible(false);
        labelForPlayerWins.setVisible(false);
        labelForPressAnyKey.setVisible(false);
        labelForFoul.setVisible(false);
        for (int i = 0; i < 7; i++) {
            BallSolid[i] = new ImageView(String.valueOf(getClass().getResource("/img/smallball/Ball" + (i + 1) + ".png")));
            BallStripes[i] = new ImageView(String.valueOf(getClass().getResource("/img/smallball/Ball" + (i + 9) + ".png")));
            BallSolid[i].setFitWidth(30);
            BallSolid[i].setFitHeight(30);
            BallStripes[i].setFitHeight(30);
            BallStripes[i].setFitWidth(30);
            group.getChildren().addAll(BallSolid[i], BallStripes[i]);
            BallStripes[i].setVisible(false);
            BallSolid[i].setVisible(false);
        }
        for (int i = 0; i < 2; i++) {
            BallKala[i] = new ImageView(String.valueOf(getClass().getResource("/img/smallball/Ball" + 8 + ".png")));
            BallKala[i].setFitWidth(30);
            BallKala[i].setFitHeight(30);
            BallKala[i].setVisible(false);
            group.getChildren().add(BallKala[i]);
        }
        BallKala[0].setLayoutX(171);
        BallKala[0].setLayoutY(660);
        BallKala[1].setLayoutX(678);
        BallKala[1].setLayoutY(660);
        Button leave = new Button("Leave");
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
        ball[4] = new Ball(865, 325, "/img/ballimages/4.png", 1, 4);
        ball[12] = new Ball(865, 350, "/img/ballimages/12.png", 2, 12);
        ball[3] = new Ball(865, 375, "/img/ballimages/3.png", 1, 3);
        ball[9] = new Ball(865, 400, "/img/ballimages/9.png", 2, 9);
        ball[7] = new Ball(865, 425, "/img/ballimages/7.png", 1, 7);

        ball[1] = new Ball(841, 338, "/img/ballimages/1.png", 1, 1);
        ball[15] = new Ball(841, 363, "/img/ballimages/15.png", 2, 15);
        ball[2] = new Ball(841, 388, "/img/ballimages/2.png", 1, 2);
        ball[5] = new Ball(841, 413, "/img/ballimages/5.png", 1, 5);

        ball[14] = new Ball(817, 350, "/img/ballimages/14.png", 2, 14);
        ball[8] = new Ball(817, 375, "/img/ballimages/8.png", 3, 8);
        ball[10] = new Ball(817, 400, "/img/ballimages/10.png", 2, 10);

        ball[11] = new Ball(793, 363, "/img/ballimages/11.png", 2, 11);
        ball[6] = new Ball(793, 388, "/img/ballimages/6.png", 1, 6);

        ball[13] = new Ball(769, 375, "/img/ballimages/13.png", 2, 13);

        ball[0] = new Ball(346, 375, "/img/ballimages/0.png", 0, 0);

        for (int i = 0; i < 16; i++) {
            group.getChildren().add(ball[i].DrawBall());
        }
    }

    public static void setVelocity(double x, double y) {
        ball[0].setVelocity(x, y);
        BilliardBattleApplication.outToServer.println("V#" + x + "#" + y);
    }

    private void reInitialize() {
        for (int i = 0; i < 16; i++) {
            group.getChildren().remove(ball[i].getSphere());
        }
        initializeBalls();
        turnNum = 1;
        stack_y = 605;
        flagForTurnChange = 0;
        flagForFoulCheck = 0;
        flagForOtherTypeBallCollisionFoulCheck = 0;
        flagForNoBallHitFoulCheck = 1;
        thisTurnPottedBalls.clear();
        for (int i = 0; i < 16; i++) {
            potted[i] = false;
        }
        player1.setMyTurn(true);
        player2.setMyTurn(false);
        isFoul = false;
        gameOver = false;
        isTurn = true;
        labelForBallTypePlayer1.setVisible(false);
        labelForBallTypePlayer2.setVisible(false);
        labelForBestOfLuck.setVisible(false);
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
        gamePause = false;
        player1.setWin(false);
        player2.setWin(false);
        player1.setBallType(0);
        player2.setBallType(0);
        player2.setAllBallsPotted(false);
        player1.setAllBallsPotted(false);
    }

    private void update() {
        if (turnNum == 1) {
            labelDekhaw();
        }
        int flag = 0;
        moveCueBall();
        for (int i = 0; i < 16; i++) {
            if (!ball[i].getVelocity().isNull()) {
                flag = 1;
                flagForTurnChange = 1;
            }
            updateSingleBalls(i);
            checkForPocket(i);
        }
        if (flag == 1) {
            isTurn = false;
        } else if (flag == 0 && flagForTurnChange == 0) {
            isTurn = true;
            turnLabel();
        } else if (flag == 0 && flagForTurnChange == 1) {
            isFoul = false;
            checkForCases();
            checkAllPottedBalls();
            if (isFoul && !gameOver) {
                stopGame();
                showAlert();
                startFromPause();
            }
            flagForTurnChange = 0;
            flagForFoulCheck = 0;
            flagForOtherTypeBallCollisionFoulCheck = 0;
            flagForNoBallHitFoulCheck = 1;
            turnNum++;
            isTurn = true;

            if (thisTurnPottedBalls.contains(Integer.valueOf(0))) {
                ball[0].setPosition(new Vector(346, 375));
                ball[0].getSphere().setVisible(true);
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

    private void labelDekhaw() {
        player1label.setText(player1.getUsername());
        player2Label.setText(player2.getUsername());
        if (player1.isMyTurn())
            labelForTurnChange.setText(player1.getUsername() + " Is Breaking");
        else {
            labelForTurnChange.setText(player2.getUsername() + " Is Breaking");
        }
    }

    private void turnLabel() {
        if (player1.isMyTurn()) {
            labelForTurnChange.setText("Turn for " + player1.getUsername());
        } else {
            labelForTurnChange.setText("Turn for " + player2.getUsername());
        }
    }

    private void showLeaveAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave!!");
        alert.setHeaderText("You will lose the game if you leave!!");
        alert.setContentText("Sure you want to leave?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            player2.setWin(true);
            player1.setWin(false);
            gameOver = true;
            window.setScene(menu);
            reInitialize();
            BilliardBattleApplication.outToServer.println("Lt");
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("FOUL!!");

        if (thisTurnPottedBalls.contains(Integer.valueOf(0)))
            alert.setHeaderText("You potted the Cue ball");
        else if (flagForOtherTypeBallCollisionFoulCheck == 1)
            alert.setHeaderText("You must hit your assigned ball type");
        else
            alert.setHeaderText("You must hit a ball");

        if (player1.isMyTurn())
            alert.setContentText("Ball in hand " + player1.getUsername());
        else alert.setContentText("Ball in hand " + player2.getUsername());
        alert.show();
    }

    private void checkAllPottedBalls() {
        if (player1.getBallType() == 0)
            return;
        if (player1.isMyTurn()) {
            int f = 0;
            if (player1.getBallType() == 1) {
                for (int i = 1; i <= 7; i++) {
                    if (potted[i] == false) {
                        f = 1;
                        break;
                    }
                }
            } else {
                for (int i = 9; i <= 15; i++) {
                    if (potted[i] == false) {
                        f = 1;
                        break;
                    }
                }
            }
            if (f == 0)
                player1.setAllBallsPotted(true);
        } else {
            int f = 0;
            if (player2.getBallType() == 1) {
                for (int i = 1; i <= 7; i++) {
                    if (potted[i] == false) {
                        f = 1;
                        break;
                    }
                }
            } else {
                for (int i = 9; i <= 15; i++) {
                    if (potted[i] == false) {
                        f = 1;
                        break;
                    }
                }
            }
            if (f == 0)
                player2.setAllBallsPotted(true);
        }
    }

    private void updateSingleBalls(int ball_num) {
        if (ball[ball_num].getVelocity().getSize() <= 8e-2) {
            ball[ball_num].setVelocity(0, 0);
        } else {
            ball[ball_num].getPosition().setX(ball[ball_num].getPosition().getX() + ball[ball_num].getVelocity().getX());
            ball[ball_num].getPosition().setY(ball[ball_num].getPosition().getY() + ball[ball_num].getVelocity().getY());
            for (Ball b : ball) {
                if (ball_num != b.getBallNumber() && ball[ball_num].collides(b)) {
                    if (turnNum != 1 && !isTurnOffSounds()) {
//                        SoundEffects.COLLIDE.play();
                    }
                    if (ball_num == 0 && flagForOtherTypeBallCollisionFoulCheck == 0 && player1.getBallType() == 0) {
                        flagForOtherTypeBallCollisionFoulCheck = 1;
                        if (b.getBallType() == 3) {
                            flagForFoulCheck = 1;
                        }
                    }
                    if (ball_num == 0 && flagForOtherTypeBallCollisionFoulCheck == 0 && player1.getBallType() != 0) {
                        flagForOtherTypeBallCollisionFoulCheck = 1;
                        if (player1.isMyTurn()) {
                            if (player1.getBallType() != b.getBallType()) {
                                if (b.getBallNumber() == 8 && player1.isAllBallsPotted())
                                    flagForFoulCheck = 0;
                                else flagForFoulCheck = 1;
                            }
                        } else {
                            if (player2.getBallType() != b.getBallType()) {
                                if (b.getBallNumber() == 8 && player2.isAllBallsPotted())
                                    flagForFoulCheck = 0;
                                else flagForFoulCheck = 1;
                            }
                        }
                    }
                    if (ball_num == 0) {
                        flagForNoBallHitFoulCheck = 0;
                    }
                    ball[ball_num].getPosition().setX(ball[ball_num].getPosition().getX() - ball[ball_num].getVelocity().getX());
                    ball[ball_num].getPosition().setY(ball[ball_num].getPosition().getY() - ball[ball_num].getVelocity().getY());
                    ball[ball_num].transferEnergy(b);
                    break;
                }
            }
            ball[ball_num].updateWallCollision();
            ball[ball_num].applyTableFriction();
            ball[ball_num].spin();
        }
        ball[ball_num].getSphere().setLayoutX(ball[ball_num].getPosition().getX());
        ball[ball_num].getSphere().setLayoutY(ball[ball_num].getPosition().getY());
    }

    private void moveCueBall() {
        ball[0].getSphere().addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isTurn && isFoul && player1.isMyTurn()) {
                gameController.stick.setVisible(false);
                gameController.circle.setVisible(false);
                gameController.line.setVisible(false);
                gameController.predictedLine.setVisible(false);
                ball[0].getSphere().setCursor(Cursor.CLOSED_HAND);
                if ((event.getSceneX() <= 937 && event.getSceneX() >= 157) && (event.getSceneY() >= 180 && event.getSceneY() <= 568)) {
                    ball[0].setPosition(new Vector(event.getSceneX(), event.getSceneY()));
                    BilliardBattleApplication.outToServer.println("M#" + event.getSceneX() + "#" + event.getSceneY());
                }

            } else if (isTurn && turnNum == 1 && player1.isMyTurn()) {
                gameController.stick.setVisible(false);
                gameController.circle.setVisible(false);
                gameController.line.setVisible(false);
                gameController.predictedLine.setVisible(false);
                ball[0].getSphere().setCursor(Cursor.CLOSED_HAND);
                if ((event.getSceneX() <= 344 && event.getSceneX() >= 155) && (event.getSceneY() >= 170 && event.getSceneY() <= 570)) {
                    ball[0].setPosition(new Vector(event.getSceneX(), event.getSceneY()));
                    BilliardBattleApplication.outToServer.println("M#" + event.getSceneX() + "#" + event.getSceneY());
                }
            }
        });

    }

    private void checkForCases() {
        int flag = 0;
        if (turnNum == 1) {
            if (thisTurnPottedBalls.size() == 0) {
                flag = 1;
            } else {
                for (int i = 0; i < thisTurnPottedBalls.size(); i++) {
                    if (thisTurnPottedBalls.get(i).intValue() == 8) {
                        khelaSes();
                    } else if (thisTurnPottedBalls.get(i).intValue() == 0) {
                        isFoul = true;
                        flag = 1;
                    } else potted[thisTurnPottedBalls.get(i).intValue()] = true;
                }
            }

        } else if (turnNum >= 2 && player1.getBallType() == 0) {
            if (thisTurnPottedBalls.size() == 0) {
                flag = 1;
            } else {
                int firstPuttedBallNum = thisTurnPottedBalls.get(0).intValue();
                if (firstPuttedBallNum >= 1 && firstPuttedBallNum < 8) {
                    if (player1.isMyTurn()) {
                        player1.setBallType(1);
                        player2.setBallType(2);
                    } else {
                        player1.setBallType(2);
                        player2.setBallType(1);
                    }
                    showLabel();
                } else if (firstPuttedBallNum >= 9 && firstPuttedBallNum <= 15) {
                    if (player1.isMyTurn()) {
                        player1.setBallType(2);
                        player2.setBallType(1);
                    } else {
                        player1.setBallType(1);
                        player2.setBallType(2);
                    }
                    showLabel();
                }
                for (int i = 0; i < thisTurnPottedBalls.size(); i++) {
                    if (thisTurnPottedBalls.get(i).intValue() == 8) {
                        khelaSes();
                    } else if (thisTurnPottedBalls.get(i).intValue() == 0) {
                        isFoul = true;
                        flag = 1;
                    } else potted[thisTurnPottedBalls.get(i).intValue()] = true;
                }
            }

        } else {
            if (thisTurnPottedBalls.size() == 0) {
                flag = 1;
            } else if (thisTurnPottedBalls.size() == 1 && thisTurnPottedBalls.get(0).intValue() == 8) {
                if (player1.isMyTurn()) {
                    if (player1.getBallType() == 1) {
                        int f = 0;
                        for (int i = 1; i <= 7; i++) {
                            if (!potted[i]) {
                                f = 1;
                                khelaSes();
                            }
                        }
                        if (f == 0) {
                            win();
                        }
                    } else {
                        int f = 0;
                        for (int i = 9; i <= 15; i++) {
                            if (!potted[i]) {
                                f = 1;
                                khelaSes();
                            }
                        }
                        if (f == 0) {
                            win();
                        }
                    }
                } else {
                    if (player2.getBallType() == 1) {
                        int f = 0;
                        for (int i = 1; i <= 7; i++) {
                            if (!potted[i]) {
                                f = 1;
                                khelaSes();
                            }
                        }
                        if (f == 0) {
                            win();
                        }
                    } else {
                        int f = 0;
                        for (int i = 9; i <= 15; i++) {
                            if (!potted[i]) {
                                f = 1;
                                khelaSes();
                            }
                        }
                        if (f == 0) {
                            win();
                        }
                    }
                }
            } else {
                int firstPuttedBallNum = thisTurnPottedBalls.get(0).intValue();
                if (player1.isMyTurn()) {
                    if (player1.getBallType() != ball[firstPuttedBallNum].getBallType()) {
                        flag = 1;
                    }
                    for (int i = 0; i < thisTurnPottedBalls.size(); i++) {
                        if (thisTurnPottedBalls.get(i).intValue() == 8) {
                            //Write code
                            khelaSes();
                        } else if (thisTurnPottedBalls.get(i).intValue() == 0) {
                            isFoul = true;
                            flag = 1;
                        } else {
                            if (thisTurnPottedBalls.get(i).intValue() != 0)
                                potted[thisTurnPottedBalls.get(i).intValue()] = true;
                        }
                    }
                } else {
                    if (player2.getBallType() != ball[firstPuttedBallNum].getBallType()) {
                        flag = 1;
                    }
                    for (int i = 0; i < thisTurnPottedBalls.size(); i++) {
                        if (thisTurnPottedBalls.get(i).intValue() == 8) {
                            //Write Code
                            khelaSes();
                        } else if (thisTurnPottedBalls.get(i).intValue() == 0) {
                            isFoul = true;
                            flag = 1;
                        } else {
                            if (thisTurnPottedBalls.get(i).intValue() != 0)
                                potted[thisTurnPottedBalls.get(i).intValue()] = true;
                        }
                    }
                }
            }


        }
        if (flagForFoulCheck == 1 || flagForNoBallHitFoulCheck == 1) {
            isFoul = true;
        }
        if (flag == 1 || flagForFoulCheck == 1 || flagForNoBallHitFoulCheck == 1)
            alterTurn();
        if (isFoul) {
            labelForFoul.setVisible(true);
        } else labelForFoul.setVisible(false);

    }

    private void showLabel() {
        labelForBallTypePlayer1.setVisible(true);
        labelForBallTypePlayer2.setVisible(true);
        labelForBestOfLuck.setVisible(true);
        labelForBallTypePlayer1.setLayoutX(186);
        labelForBallTypePlayer2.setLayoutX(719);
        labelForBestOfLuck.setLayoutX(488);
        labelForBallTypePlayer1.setLayoutY(624);
        labelForBallTypePlayer2.setLayoutY(624);
        labelForBestOfLuck.setLayoutY(624);
        if (player1.getBallType() == 1) {
            labelForBallTypePlayer1.setText(player1.getUsername() + " is Solids");
            labelForBallTypePlayer2.setText(player2.getUsername() + " is Stripes");
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
            int place1 = 360, place2 = 147;
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
        labelForBestOfLuck.setText("Best Of Luck");
        labelForBallTypePlayer1.getStyleClass().add("label-player");
        labelForBallTypePlayer2.getStyleClass().add("label-player");
        labelForBestOfLuck.getStyleClass().add("label-player");

    }

    private void khelaSes() {
        if (player1.isMyTurn()) {
            player2.setWin(true);
            player1.setWin(false);
            gameOver = true;
        } else {
            player1.setWin(true);
            player2.setWin(false);
            gameOver = true;
        }
    }

    private void win() {
        if (player1.isMyTurn()) {
            player2.setWin(false);
            player1.setWin(true);
            gameOver = true;
        } else {
            player1.setWin(false);
            player2.setWin(true);
            gameOver = true;
        }
    }

    private void gameOverDilg() {
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
            BilliardBattleApplication.outToServer.println("W#" + GameScene.getPlayer1().getUsername() + "#" + GameScene.getPlayer2().getUsername() + "#" + bet);
        } else {
            player1.setBalance(player1.getBalance() - bet);
        }
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            window.setScene(menu);
            startNewGame();
        });
    }

    private void alterTurn() {
        if (player1.isMyTurn()) {
            player1.setMyTurn(false);
            player2.setMyTurn(true);
        } else {
            player2.setMyTurn(false);
            player1.setMyTurn(true);
        }
//        if (!TurnOffSounds)
//            SoundEffects.TURNCHANGE.play();
    }


    private void checkForPocket(int ballNum) {
        double x = ball[ballNum].getPosition().getX(), y = ball[ballNum].getPosition().getY();
        double check = 625;

        if (sqdistance(x, y, 130, 154) <= check) {
            dropit(ballNum);
        } else if (sqdistance(x, y, 550, 151) <= check) {
            dropit(ballNum);
        } else if (sqdistance(x, y, 970, 154) <= check) {
            dropit(ballNum);
        } else if (sqdistance(x, y, 970, 595) <= check) {
            dropit(ballNum);
        } else if (sqdistance(x, y, 550, 600) <= check) {
            dropit(ballNum);
        } else if (sqdistance(x, y, 130, 595) <= check) {
            dropit(ballNum);
        }
        if ((y <= 148 || y >= 602) && !ball[ballNum].isDropped()) {
            dropit(ballNum);
        }
        if ((x <= 113 || x >= 979) && !ball[ballNum].isDropped()) {
            dropit(ballNum);
        }

    }

    private double sqdistance(double x1, double y1, double x2, double y2) {
        return (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
    }

    private void dropit(int ballNum) {
        thisTurnPottedBalls.add(Integer.valueOf(ballNum));
        ball[ballNum].setDropped(true);
        ball[ballNum].setVelocity(0, 0);
        ball[ballNum].setPosition(new Vector(1045, stack_y));

        stack_y -= 25;
        if (ballNum == 0) {
            stack_y += 25;
            ball[0].getSphere().setVisible(false);
            ball[0].setPosition(new Vector(0, 0));
            ball[0].setDropped(false);
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
                        gameOverDilg();
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

    public static void setTurnNum(int turnNum) {
        GameScene.turnNum = turnNum;
    }

    public static boolean isIsTurn() {
        return isTurn;
    }

    public static void setIsTurn(boolean isTurn) {
        GameScene.isTurn = isTurn;
    }

    public boolean isFoul() {
        return isFoul;
    }

    public void setFoul(boolean foul) {
        isFoul = foul;
    }

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void setGameOver(boolean gameOver) {
        GameScene.gameOver = gameOver;
    }

    public static boolean isGamePause() {
        return gamePause;
    }

    public static void setGamePause(boolean gamePause) {
        GameScene.gamePause = gamePause;
    }

    public static Ball getCueBall() {
        return ball[0];
    }

    public static int getTurnNum() {
        return turnNum;
    }

    public static boolean isTurnOffSounds() {
        return TurnOffSounds;
    }

    public static void setTurnOffSounds(boolean turnOffSounds) {
        TurnOffSounds = turnOffSounds;
    }

    public static Player getPlayer1() {
        return player1;
    }

    public static void setPlayer1(Player player1) {
        GameScene.player1 = player1;
    }

    public static Player getPlayer2() {
        return player2;
    }

    public static void setPlayer2(Player player2) {
        GameScene.player2 = player2;
    }

    public static void setName1(String s) {
        player1.setUsername(s);
        player1label.setText(s);
    }

    public static void setImage1(String s) {
//        Image image = new Image("https://graph.facebook.com/" + s + "/picture?type=large&width=122&height=122");
//        if (image.isError()) {
//            imageView1.setImage(new Image("sample/Default Profile Pictures/4860042536_a85b1c2745.jpg"));
//        } else imageView1.setImage(image);
    }

    public static void setName2(String s) {
        player2.setUsername(s);
        player2Label.setText(s);
    }

    public static void setImage2(String s) {
//        Image image = new Image("https://graph.facebook.com/" + s + "/picture?type=large&width=122&height=122");
//        if (image.isError()) {
//            imageView2.setImage(new Image("sample/Default Profile Pictures/4860042536_a85b1c2745.jpg"));
//        } else imageView2.setImage(image);
    }

    public static void setTurn(boolean t) {
        if (t) {
            player1.setMyTurn(true);
            player2.setMyTurn(false);
            labelForTurnChange.setText(player1.getUsername() + " is Breaking");
        } else {
            player1.setMyTurn(false);
            player2.setMyTurn(true);
            labelForTurnChange.setText(player2.getUsername() + " is Breaking");
        }

    }


    public static Label getPlayer1label() {
        return player1label;
    }

    public static void setPlayer1label(Label player1label) {
        GameScene.player1label = player1label;
    }

    public static Label getPlayer2Label() {
        return player2Label;
    }

    public static void setPlayer2Label(Label player2Label) {
        GameScene.player2Label = player2Label;
    }

    public static int getBet() {
        return bet;
    }

    public static void setBet(int bet) {
        GameScene.bet = bet;
    }
}
