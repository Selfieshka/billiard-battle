//package ru.kpfu.itis.kirillakhmetov.billiardbattle;
//
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
//import javafx.scene.Cursor;
//import javafx.scene.Group;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.Label;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.input.MouseEvent;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//import lombok.Data;
//import ru.kpfu.itis.kirillakhmetov.billiardbattle.controller.BilliardTableController;
//import ru.kpfu.itis.kirillakhmetov.billiardbattle.entity.*;
//
//import java.io.BufferedReader;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Optional;
//
//@Data
//public class GameScene {
//    private Group group;
//    private Scene scene, menu;
//    private Parent root;
//    public static Ball[] ball;
//    private static Player2 player1, player2;
//    private static int turnNum = 1;
//    private static boolean isTurn;
//    private boolean isFoul;
//    private static boolean gameOver;
//    private double stack_y = 605;
//    private boolean turnChangeFlag;
//    private boolean foulCheckFlag; //for foul check
//    private boolean ballCollisionFoulCheckFlag; //for other type ball collision foul check
//    private boolean noBallHitFoulCheck = true; // for no ball hit foul check
//    private ArrayList<Integer> thisTurnPottedBalls;
//    private static Label label = new Label(); //Label for the turn change
//    private Label label1 = new Label(); //label for ball type stripes or solids
//    private Label label2 = new Label(); //label for ball type stripes or solids
//    private Label label3 = new Label(); //label for Best Of Lcuk
//    private Label label4 = new Label(); //label for Game over
//    private Label label5 = new Label(); //laber  for player wins
//    private Label label6 = new Label();  //label for press any key to continue
//    private Label label7 = new Label(); //label for foul
//    private ImageView[] BallSolid = new ImageView[7];
//    private ImageView[] BallStripes = new ImageView[7];
//    private ImageView[] BallKala = new ImageView[2];
//    private boolean potted[] = new boolean[16];
//    private static boolean TurnOffSounds;
//    private Timeline timeline = new Timeline();
//    private static boolean gamePause = false;
//    private Stage window;
//    private static ImageView imageView1;
//    private static ImageView imageView2;
//    private static Label player1label, player2Label;
//    private static PrintWriter outToServer;
//    private static BufferedReader inFromServer;
//    private Button leave;
//    private static int bet;
//    private BilliardTableController controller;
//
//    public GameScene(Group group, Scene scene, Scene menu, Parent root, Stage window, BilliardTableController controller) throws Exception {
//        this.outToServer = outToServer;
//        this.inFromServer = inFromServer;
//        this.window = window;
//        this.group = group;
//        this.menu = menu;
//        this.root = root;
//        this.controller = controller;
//        this.scene = scene;
//
//        player1 = new Player2();
//        player2 = new Player2();
//        ball = new Ball[16];
//
//        drawPlayersLabel();
//        group.getChildren().addAll(root);
//
//        scene.getStylesheets().add(String.valueOf(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/menu.css")));
//
//        initializeBalls();
//
//        thisTurnPottedBalls = new ArrayList<>();
//        player1.setMyTurn(true);
//        isTurn = true;
//
//        label.setLayoutX(462);
//        label.setLayoutY(81);
//        label.setText(player1.getName() + " Is Breaking");
//        label.getStyleClass().add("label-player");
//        label7.setLayoutX(531);
//        label7.setLayoutY(655);
//        label7.setText("FOUL!!");
//        label7.getStyleClass().add("label-player");
//        group.getChildren().add(label);
//
/// /        SoundEffects.init();
/// /        SoundEffects.volume = SoundEffects.Volume.LOW;
//        group.getChildren().addAll(label1, label2, label3);
//        group.getChildren().addAll(label4, label5, label6, label7);
//
//        for (int i = 0; i < 7; i++) {
//            BallSolid[i] = new ImageView(String.valueOf(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/smallball/Ball" + (i + 1) + ".png")));
//            BallStripes[i] = new ImageView(String.valueOf(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/smallball/Ball" + (i + 9) + ".png")));
//            BallSolid[i].setFitWidth(30);
//            BallSolid[i].setFitHeight(30);
//            BallStripes[i].setFitHeight(30);
//            BallStripes[i].setFitWidth(30);
//            group.getChildren().addAll(BallSolid[i], BallStripes[i]);
//            BallStripes[i].setVisible(false);
//            BallSolid[i].setVisible(false);
//        }
//        for (int i = 0; i < 2; i++) {
//            BallKala[i] = new ImageView(String.valueOf(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/smallball/Ball" + 8 + ".png")));
//            BallKala[i].setFitWidth(30);
//            BallKala[i].setFitHeight(30);
//            BallKala[i].setVisible(false);
//            group.getChildren().add(BallKala[i]);
//        }
//        BallKala[0].setLayoutX(171);
//        BallKala[0].setLayoutY(660);
//        BallKala[1].setLayoutX(678);
//        BallKala[1].setLayoutY(660);
//        leave = new Button("Leave");
//        leave.setLayoutX(2);
//        leave.setLayoutY(70);
//        leave.getStyleClass().add("button-game");
//        leave.setOnAction(event -> {
//            if (!gameOver) {
//                showLeaveAlert();
//            }
//        });
//        group.getChildren().add(leave);
//    }
//
//    public void drawPlayersLabel() {
//        player1label = new Label();
//        player2Label = new Label();
//        player1label.setLayoutX(240);
//        player1label.setLayoutY(10);
//        player2Label.setLayoutX(760);
//        player2Label.setLayoutY(10);
//        player1label.getStyleClass().add("label-player2");
//        player2Label.getStyleClass().add("label-player2");
//        group.getChildren().addAll(player1label, player2Label);
//    }
//
//    public void initializeBalls() {
//        int index = 1;
//        for (int level = 0; level < 5; level++) {
//            double x = GameParameters.BALL_START_X + level * GameParameters.BALL_RADIUS * 2;
//            for (int position = 0; position <= level; position++) {
//                double y = GameParameters.BALL_START_Y - GameParameters.BALL_RADIUS * level + GameParameters.BALL_RADIUS * 2 * position;
//                if (index == 8) {
//                    ball[index] = new Ball(x, y, "/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/ballimages/" + index + ".png", BallType.BLACK_BALL, (index + 1));
//                } else if (index % 2 != 0) {
//                    ball[index] = new Ball(x, y, "/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/ballimages/" + index + ".png", BallType.STRIPED_BALL, (index + 1));
//                } else {
//                    ball[index] = new Ball(x, y, "/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/ballimages/" + index + ".png", BallType.SOLID_BALL, (index + 1));
//                }
//                index++;
//            }
//        }
//        ball[0] = new Ball(346, 375, "/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/ballimages/0.png", BallType.CUE_BALL, 0);
//
//        for (int i = 0; i < 16; i++) {
//            group.getChildren().add(ball[i].drawBall());
//        }
//    }
//
//    public static void setVelocity(double x, double y) {
//        ball[0].setVelocity(x, y);
////        outToServer.println("V#" + x + "#" + y);
//    }
//
//    private void reInitialize() {
//        for (int i = 0; i < 16; i++) {
//            group.getChildren().remove(ball[i].getSphere());
//        }
//        initializeBalls();
//        turnNum = 1;
//        stack_y = 605;
//        turnChangeFlag = false;
//        foulCheckFlag = false;
//        ballCollisionFoulCheckFlag = false;
//        noBallHitFoulCheck = true;
//        thisTurnPottedBalls.clear();
//        for (int i = 0; i < 16; i++) {
//            potted[i] = false;
//        }
//        player1.setMyTurn(true);
//        player2.setMyTurn(false);
//        isFoul = false;
//        gameOver = false;
//        isTurn = true;
//        label1.setVisible(false);
//        label2.setVisible(false);
//        label3.setVisible(false);
//        label4.setVisible(false);
//        label5.setVisible(false);
//        label6.setVisible(false);
//        label7.setVisible(false);
//        for (int i = 0; i < 7; i++) {
//            BallStripes[i].setVisible(false);
//            BallSolid[i].setVisible(false);
//        }
//        for (int i = 0; i < 2; i++) {
//            BallKala[i].setVisible(false);
//        }
//        gamePause = false;
//        player1.setWin(false);
//        player2.setWin(false);
//        player1.setBallType(null);
//        player2.setBallType(null);
//        player2.setAllBallsPotted(false);
//        player1.setAllBallsPotted(false);
//    }
//
//    private void update() {
//        if (turnNum == 1) {
//            labelDekhaw();
//        }
//        boolean flag = false;
//        moveCueBall();
//        for (int i = 0; i < 16; i++) {
//            if (!ball[i].getVelocity().isNull()) {
//                flag = true;
//                turnChangeFlag = true;
//            }
//            updateSingleBalls(i);
//            checkForPocket(i);
//        }
//        if (flag) {
//            isTurn = false;
//        } else if (!flag && !turnChangeFlag) {
//            isTurn = true;
//            turnLabel();
//        } else if (!flag && turnChangeFlag == true) {
//            isFoul = false;
//            checkForCases();
//            checkAllPottedBalls();
//            if (isFoul && !gameOver) {
//                stopGame();
//                showAlert();
//                startFromPause();
//            }
//            turnChangeFlag = false;
//            foulCheckFlag = false;
//            ballCollisionFoulCheckFlag = false;
//            noBallHitFoulCheck = true;
//            turnNum++;
//            isTurn = true;
//
//            if (thisTurnPottedBalls.contains(0)) {
//                ball[0].setPosition(new Vector2(346, 375));
//                ball[0].getSphere().setVisible(true);
//            }
//            for (int i = 1; i <= 7; i++) {
//                if (potted[i]) {
//                    BallSolid[i - 1].setVisible(false);
//                }
//            }
//            for (int i = 9; i <= 15; i++) {
//                if (potted[i]) {
//                    BallStripes[i - 9].setVisible(false);
//                }
//            }
//            if (player1.isAllBallsPotted()) {
//                BallKala[0].setVisible(true);
//            }
//            if (player2.isAllBallsPotted()) {
//                BallKala[1].setVisible(true);
//            }
//            thisTurnPottedBalls.clear();
//        }
//    }
//
//    private void labelDekhaw() {
//        player1label.setText(player1.getName());
//        player2Label.setText(player2.getName());
//        if (player1.isMyTurn())
//            label.setText(player1.getName() + " Is Breaking");
//        else {
//            label.setText(player2.getName() + " Is Breaking");
//        }
//    }
//
//    private void turnLabel() {
//        if (player1.isMyTurn()) {
//            label.setText("Turn for " + player1.getName());
//        } else {
//            label.setText("Turn for " + player2.getName());
//        }
//    }
//
//    private void showLeaveAlert() {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Leave!!");
//        alert.setHeaderText("You will lose the game if you leave!!");
//        alert.setContentText("Sure you want to leave?");
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.get() == ButtonType.OK) {
//            player2.setWin(true);
//            player1.setWin(false);
//            gameOver = true;
//            window.setScene(menu);
//            reInitialize();
//            outToServer.println("Lt");
//        }
//    }
//
//    private void showAlert() {
//        Alert alert = new Alert(Alert.AlertType.WARNING);
//        alert.setTitle("FOUL!!");
//
//        if (thisTurnPottedBalls.contains(Integer.valueOf(0)))
//            alert.setHeaderText("You potted the Cue ball");
//        else if (ballCollisionFoulCheckFlag)
//            alert.setHeaderText("You must hit your assigned ball type");
//        else
//            alert.setHeaderText("You must hit a ball");
//
//        if (player1.isMyTurn())
//            alert.setContentText("Ball in hand " + player1.getName());
//        else alert.setContentText("Ball in hand " + player2.getName());
//        alert.show();
//    }
//
//    private void checkAllPottedBalls() {
//        if (player1.getBallType() == null)
//            return;
//        if (player1.isMyTurn()) {
//            int f = 0;
//            if (player1.getBallType().equals(BallType.SOLID_BALL)) {
//                for (int i = 1; i <= 7; i++) {
//                    if (potted[i] == false) {
//                        f = 1;
//                        break;
//                    }
//                }
//            } else {
//                for (int i = 9; i <= 15; i++) {
//                    if (potted[i] == false) {
//                        f = 1;
//                        break;
//                    }
//                }
//            }
//            if (f == 0)
//                player1.setAllBallsPotted(true);
//        } else {
//            int f = 0;
//            if (player2.getBallType().equals(BallType.SOLID_BALL)) {
//                for (int i = 1; i <= 7; i++) {
//                    if (potted[i] == false) {
//                        f = 1;
//                        break;
//                    }
//                }
//            } else {
//                for (int i = 9; i <= 15; i++) {
//                    if (potted[i] == false) {
//                        f = 1;
//                        break;
//                    }
//                }
//            }
//            if (f == 0)
//                player2.setAllBallsPotted(true);
//        }
//    }
//
//    private void updateSingleBalls(int ball_num) {
//        if (ball[ball_num].getVelocity().getSize() <= 8e-2) {
//            ball[ball_num].setVelocity(0, 0);
//        } else {
//            ball[ball_num].getPosition().setX(ball[ball_num].getPosition().getX() + ball[ball_num].getVelocity().getX());
//            ball[ball_num].getPosition().setY(ball[ball_num].getPosition().getY() + ball[ball_num].getVelocity().getY());
//
//            for (Ball b : ball) {
//                if (ball_num != b.getBallNumber() && ball[ball_num].collides(b)) {
//                    if (turnNum != 1 && !isTurnOffSounds()) {
////                        SoundEffects.COLLIDE.play();
//                    }
//                    if (ball_num == 0 && !ballCollisionFoulCheckFlag && player1.getBallType() == null) {
//                        ballCollisionFoulCheckFlag = true;
//                        if (b.getBallType().equals(BallType.BLACK_BALL)) {
//                            foulCheckFlag = true;
//                        }
//                    }
//                    if (ball_num == 0 && !ballCollisionFoulCheckFlag && player1.getBallType() != null) {
//                        ballCollisionFoulCheckFlag = true;
//                        if (player1.isMyTurn()) {
//                            if (!player1.getBallType().equals(b.getBallType())) {
//                                if (b.getBallNumber() == 8 && player1.isAllBallsPotted())
//                                    foulCheckFlag = false;
//                                else foulCheckFlag = true;
//                            }
//                        } else {
//                            if (!player2.getBallType().equals(b.getBallType())) {
//                                if (b.getBallNumber() == 8 && player2.isAllBallsPotted())
//                                    foulCheckFlag = false;
//                                else foulCheckFlag = true;
//                            }
//                        }
//                    }
//                    if (ball_num == 0) {
//                        noBallHitFoulCheck = false;
//                    }
//                    ball[ball_num].getPosition().setX(ball[ball_num].getPosition().getX() - ball[ball_num].getVelocity().getX());
//                    ball[ball_num].getPosition().setY(ball[ball_num].getPosition().getY() - ball[ball_num].getVelocity().getY());
//                    ball[ball_num].transferEnergy(b);
//                    break;
//                }
//            }
//            ball[ball_num].updateWallCollision();
//            ball[ball_num].applyTableFriction();
//            ball[ball_num].spin();
////            if (Double.isNaN(ball[ball_num].getPosition().getX())) {
////                ball[ball_num].getPosition().getX();
////                ball[ball_num].getPosition().getX();
////            }
////            System.out.println(ball[ball_num].getPosition().getX() + " " + ball[ball_num].getPosition().getY());
//        }
//        ball[ball_num].getSphere().setLayoutX(ball[ball_num].getPosition().getX());
//        ball[ball_num].getSphere().setLayoutY(ball[ball_num].getPosition().getY());
//    }
//
//    private void moveCueBall() {
//        ball[0].getSphere().addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
//            if (isTurn && isFoul && player1.isMyTurn()) {
//                controller.stick.setVisible(false);
//                controller.circle.setVisible(false);
//                controller.line.setVisible(false);
//                controller.dirLine1.setVisible(false);
//                ball[0].getSphere().setCursor(Cursor.CLOSED_HAND);
//                if ((event.getSceneX() <= 937 && event.getSceneX() >= 157) && (event.getSceneY() >= 180 && event.getSceneY() <= 568)) {
//                    ball[0].setPosition(new Vector2(event.getSceneX(), event.getSceneY()));
//                    outToServer.println("M#" + event.getSceneX() + "#" + event.getSceneY());
//                }
//
//            } else if (isTurn && turnNum == 1 && player1.isMyTurn()) {
//                controller.stick.setVisible(false);
//                controller.circle.setVisible(false);
//                controller.line.setVisible(false);
//                controller.dirLine1.setVisible(false);
//                ball[0].getSphere().setCursor(Cursor.CLOSED_HAND);
//                if ((event.getSceneX() <= 344 && event.getSceneX() >= 155) && (event.getSceneY() >= 170 && event.getSceneY() <= 570)) {
//                    ball[0].setPosition(new Vector2(event.getSceneX(), event.getSceneY()));
////                    if (Double.isNaN(event.getSceneX())) {
////                        System.out.println(event.getSceneX());
////                    }
//                    outToServer.println("M#" + event.getSceneX() + "#" + event.getSceneY());
//                }
//            }
//        });
//
//    }
//
//    private void checkForCases() {
//        int flag = 0;
//        if (turnNum == 1) {
//            if (thisTurnPottedBalls.size() == 0) {
//                flag = 1;
//            } else {
//                for (int i = 0; i < thisTurnPottedBalls.size(); i++) {
//                    if (thisTurnPottedBalls.get(i).intValue() == 8) {
//                        khelaSes();
//                    } else if (thisTurnPottedBalls.get(i).intValue() == 0) {
//                        isFoul = true;
//                        flag = 1;
//                    } else potted[thisTurnPottedBalls.get(i).intValue()] = true;
//                }
//            }
//
//        } else if (turnNum >= 2 && player1.getBallType() == null) {
//            if (thisTurnPottedBalls.size() == 0) {
//                flag = 1;
//            } else {
//                int firstPuttedBallNum = thisTurnPottedBalls.get(0).intValue();
//                if (firstPuttedBallNum >= 1 && firstPuttedBallNum < 8) {
//                    if (player1.isMyTurn()) {
//                        player1.setBallType(BallType.SOLID_BALL);
//                        player2.setBallType(BallType.STRIPED_BALL);
//                    } else {
//                        player1.setBallType(BallType.STRIPED_BALL);
//                        player2.setBallType(BallType.SOLID_BALL);
//                    }
//                    showLabel();
//                } else if (firstPuttedBallNum >= 9 && firstPuttedBallNum <= 15) {
//                    if (player1.isMyTurn()) {
//                        player1.setBallType(BallType.STRIPED_BALL);
//                        player2.setBallType(BallType.SOLID_BALL);
//                    } else {
//                        player1.setBallType(BallType.SOLID_BALL);
//                        player2.setBallType(BallType.STRIPED_BALL);
//                    }
//                    showLabel();
//                }
//                for (int i = 0; i < thisTurnPottedBalls.size(); i++) {
//                    if (thisTurnPottedBalls.get(i).intValue() == 8) {
//                        khelaSes();
//                    } else if (thisTurnPottedBalls.get(i).intValue() == 0) {
//                        isFoul = true;
//                        flag = 1;
//                    } else potted[thisTurnPottedBalls.get(i).intValue()] = true;
//                }
//            }
//
//        } else {
//            if (thisTurnPottedBalls.size() == 0) {
//                flag = 1;
//            } else if (thisTurnPottedBalls.size() == 1 && thisTurnPottedBalls.get(0).intValue() == 8) {
//                if (player1.isMyTurn()) {
//                    if (player1.getBallType().equals(BallType.SOLID_BALL)) {
//                        int f = 0;
//                        for (int i = 1; i <= 7; i++) {
//                            if (!potted[i]) {
//                                f = 1;
//                                khelaSes();
//                            }
//                        }
//                        if (f == 0) {
//                            win();
//                        }
//                    } else {
//                        int f = 0;
//                        for (int i = 9; i <= 15; i++) {
//                            if (!potted[i]) {
//                                f = 1;
//                                khelaSes();
//                            }
//                        }
//                        if (f == 0) {
//                            win();
//                        }
//                    }
//                } else {
//                    if (player2.getBallType().equals(BallType.SOLID_BALL)) {
//                        int f = 0;
//                        for (int i = 1; i <= 7; i++) {
//                            if (!potted[i]) {
//                                f = 1;
//                                khelaSes();
//                            }
//                        }
//                        if (f == 0) {
//                            win();
//                        }
//                    } else {
//                        int f = 0;
//                        for (int i = 9; i <= 15; i++) {
//                            if (!potted[i]) {
//                                f = 1;
//                                khelaSes();
//                            }
//                        }
//                        if (f == 0) {
//                            win();
//                        }
//                    }
//                }
//            } else {
//                int firstPuttedBallNum = thisTurnPottedBalls.get(0).intValue();
//                if (player1.isMyTurn()) {
//                    if (!player1.getBallType().equals(ball[firstPuttedBallNum].getBallType())) {
//                        flag = 1;
//                    }
//                    for (int i = 0; i < thisTurnPottedBalls.size(); i++) {
//                        if (thisTurnPottedBalls.get(i).intValue() == 8) {
//                            //Write code
//                            khelaSes();
//                        } else if (thisTurnPottedBalls.get(i).intValue() == 0) {
//                            isFoul = true;
//                            flag = 1;
//                        } else {
//                            if (thisTurnPottedBalls.get(i).intValue() != 0)
//                                potted[thisTurnPottedBalls.get(i).intValue()] = true;
//                        }
//                    }
//                } else {
//                    if (player2.getBallType() != ball[firstPuttedBallNum].getBallType()) {
//                        flag = 1;
//                    }
//                    for (int i = 0; i < thisTurnPottedBalls.size(); i++) {
//                        if (thisTurnPottedBalls.get(i).intValue() == 8) {
//                            //Write Code
//                            khelaSes();
//                        } else if (thisTurnPottedBalls.get(i).intValue() == 0) {
//                            isFoul = true;
//                            flag = 1;
//                        } else {
//                            if (thisTurnPottedBalls.get(i).intValue() != 0)
//                                potted[thisTurnPottedBalls.get(i).intValue()] = true;
//                        }
//                    }
//                }
//            }
//
//
//        }
//        if (foulCheckFlag || noBallHitFoulCheck) {
//            isFoul = true;
//        }
//        if (flag == 1 || foulCheckFlag || noBallHitFoulCheck)
//            alterTurn();
//        if (isFoul) {
//            label7.setVisible(true);
//        } else label7.setVisible(false);
//
//    }
//
//    private void showLabel() {
//        label1.setVisible(true);
//        label2.setVisible(true);
//        label3.setVisible(true);
//        label1.setLayoutX(186);
//        label2.setLayoutX(719);
//        label3.setLayoutX(488);
//        label1.setLayoutY(624);
//        label2.setLayoutY(624);
//        label3.setLayoutY(624);
//        if (player1.getBallType().equals(BallType.SOLID_BALL)) {
//            label1.setText(player1.getName() + " is Solids");
//            label2.setText(player2.getName() + " is Stripes");
//            int place1 = 360, place2 = 147;
//            for (int i = 0; i < 7; i++) {
//                BallSolid[i].setLayoutX(531 - place1);
//                BallSolid[i].setLayoutY(660);
//                BallStripes[i].setLayoutX(531 + place2);
//                BallStripes[i].setLayoutY(660);
//                place1 -= 45;
//                place2 += 45;
//                BallStripes[i].setVisible(true);
//                BallSolid[i].setVisible(true);
//            }
//        } else {
//            label1.setText(player1.getName() + " is Stripes");
//            label2.setText(player2.getName() + " is Solids");
//            int place1 = 360, place2 = 147;
//            for (int i = 0; i < 7; i++) {
//                BallStripes[i].setLayoutX(531 - place1);
//                BallSolid[i].setLayoutY(660);
//                BallSolid[i].setLayoutX(531 + place2);
//                BallStripes[i].setLayoutY(660);
//                place1 -= 45;
//                place2 += 45;
//                BallStripes[i].setVisible(true);
//                BallSolid[i].setVisible(true);
//            }
//        }
//        label3.setText("Best Of Luck");
//        label1.getStyleClass().add("label-player");
//        label2.getStyleClass().add("label-player");
//        label3.getStyleClass().add("label-player");
//
//    }
//
//    private void khelaSes() {
//        if (player1.isMyTurn()) {
//            player2.setWin(true);
//            player1.setWin(false);
//            gameOver = true;
//        } else {
//            player1.setWin(true);
//            player2.setWin(false);
//            gameOver = true;
//        }
//    }
//
//    private void win() {
//        if (player1.isMyTurn()) {
//            player2.setWin(false);
//            player1.setWin(true);
//            gameOver = true;
//        } else {
//            player1.setWin(false);
//            player2.setWin(true);
//            gameOver = true;
//        }
//    }
//
//    private void gameOverDilg() {
//        label4.setVisible(true);
//        label5.setVisible(true);
//        label6.setVisible(true);
//        label4.setLayoutX(425);
//        label4.setLayoutY(169);
//        label5.setLayoutX(415);
//        label5.setLayoutY(242);
//        label6.setLayoutX(240);
//        label6.setLayoutY(500);
//        label4.setText("Game Over");
//        if (player1.isWin()) label5.setText(player1.getName() + " Wins!");
//        else label5.setText(player2.getName() + " Wins!");
//        label6.setText("Press Any Key To Continue");
//        label4.getStyleClass().add("label-over");
//        label5.getStyleClass().add("label-over");
//        label6.getStyleClass().add("label-over");
//        if (GameScene.getPlayer1().isWin()) {
//            player1.setBalance(player1.getBalance() + bet);
//            outToServer.println("W#" + GameScene.getPlayer1().getName() + "#" + GameScene.getPlayer2().getName() + "#" + bet);
//        } else {
//            player1.setBalance(player1.getBalance() - bet);
//        }
//        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
//            window.setScene(menu);
//            startNewGame();
//        });
//    }
//
//    private void alterTurn() {
//        if (player1.isMyTurn()) {
//            player1.setMyTurn(false);
//            player2.setMyTurn(true);
//        } else {
//            player2.setMyTurn(false);
//            player1.setMyTurn(true);
//        }
////        if (!TurnOffSounds)
////            SoundEffects.TURNCHANGE.play();
//    }
//
//
//    private void checkForPocket(int ballNum) {
//        double x = ball[ballNum].getPosition().getX(), y = ball[ballNum].getPosition().getY();
//        double check = 625;
//
//        if (sqdistance(x, y, 130, 154) <= check) {
//            dropit(ballNum);
//        } else if (sqdistance(x, y, 550, 151) <= check) {
//            dropit(ballNum);
//        } else if (sqdistance(x, y, 970, 154) <= check) {
//            dropit(ballNum);
//        } else if (sqdistance(x, y, 970, 595) <= check) {
//            dropit(ballNum);
//        } else if (sqdistance(x, y, 550, 600) <= check) {
//            dropit(ballNum);
//        } else if (sqdistance(x, y, 130, 595) <= check) {
//            dropit(ballNum);
//        }
//        if ((y <= 148 || y >= 602) && !ball[ballNum].isDropped()) {
//            dropit(ballNum);
//        }
//        if ((x <= 113 || x >= 979) && !ball[ballNum].isDropped()) {
//            dropit(ballNum);
//        }
//
//    }
//
//    private double sqdistance(double x1, double y1, double x2, double y2) {
//        return (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
//    }
//
//    private void dropit(int ballNum) {
//        thisTurnPottedBalls.add(Integer.valueOf(ballNum));
//        ball[ballNum].setDropped(true);
//        ball[ballNum].setVelocity(0, 0);
//        ball[ballNum].setPosition(new Vector2(1045, stack_y));
//
//        stack_y -= 25;
//        if (ballNum == 0) {
//            stack_y += 25;
//            ball[0].getSphere().setVisible(false);
//            ball[0].setPosition(new Vector2(0, 0));
//            ball[0].setDropped(false);
//        }
//    }
//
//
//    public void startGame() {
//        gamePause = false;
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        KeyFrame keyFrame = new KeyFrame(
//                Duration.seconds(0.015),
//                ae -> {
//                    if (!gameOver) {
//                        update();
//                    } else {
//                        gameOver = false;
//                        gameOverDilg();
//                    }
//                });
//        timeline.getKeyFrames().add(keyFrame);
//        timeline.play();
//    }
//
//    public void stopGame() {
//        timeline.stop();
//        gamePause = true;
//    }
//
//    public void startFromPause() {
//        gamePause = false;
//        timeline.play();
//    }
//
//    public void startNewGame() {
//        stopGame();
//        gamePause = false;
//        reInitialize();
//        timeline.play();
//    }
//
//    public static void setTurnNum(int turnNum) {
//        GameScene.turnNum = turnNum;
//    }
//
//    public static boolean isTurn() {
//        return isTurn;
//    }
//
//    public static void setIsTurn(boolean isTurn) {
//        GameScene.isTurn = isTurn;
//    }
//
//    public boolean isFoul() {
//        return isFoul;
//    }
//
//    public void setFoul(boolean foul) {
//        isFoul = foul;
//    }
//
//    public static boolean isGameOver() {
//        return gameOver;
//    }
//
//    public static void setGameOver(boolean gameOver) {
//        GameScene.gameOver = gameOver;
//    }
//
//    public static boolean isGamePause() {
//        return gamePause;
//    }
//
//    public static void setGamePause(boolean gamePause) {
//        GameScene.gamePause = gamePause;
//    }
//
//    public static Ball getCueBall() {
//        return ball[0];
//    }
//
//    public static int getTurnNum() {
//        return turnNum;
//    }
//
//    public static boolean isTurnOffSounds() {
//        return TurnOffSounds;
//    }
//
//    public static void setTurnOffSounds(boolean turnOffSounds) {
//        TurnOffSounds = turnOffSounds;
//    }
//
//    public static Player2 getPlayer1() {
//        return player1;
//    }
//
//    public static void setPlayer1(Player2 player1) {
//        GameScene.player1 = player1;
//    }
//
//    public static Player2 getPlayer2() {
//        return player2;
//    }
//
//    public static void setPlayer2(Player2 player2) {
//        GameScene.player2 = player2;
//    }
//
//    public static void setName1(String s) {
//        player1.setName(s);
//        player1label.setText(s);
//    }
//
//    public static void setName2(String s) {
//        player2.setName(s);
//        player2Label.setText(s);
//    }
//
//    public static void setTurn(boolean t) {
//        if (t) {
//            player1.setMyTurn(true);
//            player2.setMyTurn(false);
//            label.setText(player1.getName() + " is Breaking");
//        } else {
//            player1.setMyTurn(false);
//            player2.setMyTurn(true);
//            label.setText(player2.getName() + " is Breaking");
//        }
//
//    }
//
//
//    public static Label getPlayer1label() {
//        return player1label;
//    }
//
//    public static void setPlayer1label(Label player1label) {
//        GameScene.player1label = player1label;
//    }
//
//    public static Label getPlayer2Label() {
//        return player2Label;
//    }
//
//    public static void setPlayer2Label(Label player2Label) {
//        GameScene.player2Label = player2Label;
//    }
//
//    public static int getBet() {
//        return bet;
//    }
//
//    public static void setBet(int bet) {
//        GameScene.bet = bet;
//    }
//}
