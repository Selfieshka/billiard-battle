//package ru.kpfu.itis.kirillakhmetov.billiardbattle.controller;
//
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Label;
//import javafx.scene.control.Slider;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.Line;
/// /import ru.kpfu.itis.kirillakhmetov.billiardbattle.GameScene;
//import ru.kpfu.itis.kirillakhmetov.billiardbattle.entity.Ball;
//import ru.kpfu.itis.kirillakhmetov.billiardbattle.entity.GameParameters;
//import ru.kpfu.itis.kirillakhmetov.billiardbattle.entity.Vector;
//
//import java.net.URL;
//import java.util.Objects;
//import java.util.ResourceBundle;
//
//
//public class BilliardTableController implements Initializable {
//    @FXML
//    public Line line;
//    @FXML
//    public Circle circle;
//    @FXML
//    public ImageView border1, border2, border3, border4;
//    @FXML
//    public Slider velocitySlider;
//    @FXML
//    public Label velocityLabel;
//    @FXML
//    public ImageView stick;
//    @FXML
//    public Line dirLine1;
//
//    double ang;
//
//    private double xp = -1, yp = -1;
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        Image stickImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/cuesticks/cue-stick.png")));
//        Image border1Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/ballimages/img1.jpg")));
//        Image border2Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/ballimages/img2.jpg")));
//        stick.setImage(stickImg);
//        border1.setImage(border1Img);
//        border2.setImage(border1Img);
//        border3.setImage(border2Img);
//        border4.setImage(border2Img);
//    }
//
//    public void moveLine(MouseEvent event) {
//        if (GameScene.isTurn() && !GameScene.isGameOver()
//                && !GameScene.isGamePause() && GameScene.getPlayer1().isMyTurn()) {
//            double x1 = GameScene.getCueBall().getPosition().getX();
//            double y1 = GameScene.getCueBall().getPosition().getY();
//            double x2 = event.getSceneX();
//            double y2 = event.getSceneY();
//
//            line.setVisible(true);
//            line.setStroke(Color.WHITE);
//            line.setStartX(x1);
//            line.setStartY(y1);
//            line.setEndX(x2);
//            line.setEndY(y2);
//
//            circle.setVisible(true);
//            circle.setStroke(Color.WHITE);
//            circle.setCenterX(x2);
//            circle.setCenterY(y2);
//            circle.setRadius(GameParameters.BALL_RADIUS);
//
//            for (int i = 0; i < 16; i++) {
//                if (collides(circle, GameScene.ball[i])) {
//                    dirLine1.setVisible(true);
//                    break;
//                }
//            }
//
//            for (int i = 1; i < 16; i++) {
////                System.out.println("-------");
//                if (collides(circle, GameScene.ball[i])) {
//                    double cueBallVelocity = 40;
//                    double angle = Math.atan((y2 - y1) / (x2 - x1));
//                    if (x2 < x1) cueBallVelocity = -cueBallVelocity;
//                    Vector position = new Vector(circle.getCenterX(), circle.getCenterY());
//                    Vector velocity = new Vector(cueBallVelocity * Math.cos(angle), cueBallVelocity * Math.sin(angle));
//                    Vector nv2 = position.sub(GameScene.ball[i].getPosition());
//                    nv2.normalize();
//                    nv2.multiply(velocity.dot(nv2));
//                    Vector nv2b = GameScene.ball[i].getPosition().sub(position);
//                    nv2b.normalize();
//                    nv2b.multiply(GameScene.ball[i].getVelocity().dot(nv2b));
//                    Vector nv1b = GameScene.ball[i].getVelocity().sub(nv2b);
//                    double p = GameScene.ball[i].getSphere().getLayoutX(), q = GameScene.ball[i].getSphere().getLayoutY();
//                    Vector v = nv2.add(nv1b);
//                    dirLine1.setStartX(p);
//                    dirLine1.setStartY(q);
//                    dirLine1.setEndX(p + v.getX());
//                    dirLine1.setEndY(q + v.getY());
//                }
//            }
//
//            stick.setVisible(true);
//            stick.setLayoutX(x1 - (346 + 36));
//            stick.setLayoutY(y1 - 14);
//            ang = Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));
//            if (x2 <= x1) {
//                ang = 180 - ang;
//                ang = -ang;
//            }
//            stick.setRotate(ang);
//            double mid_x = stick.getLayoutX() + stick.getFitWidth() / 2;
//            double mid_y = stick.getLayoutY() + stick.getFitHeight() / 2;
//            double dist = (x1 - mid_x);
//            double now_y = Math.sin(Math.toRadians(-ang)) * dist + mid_y;
//            double now_x = mid_x + (dist - dist * Math.cos(Math.toRadians(ang)));
//            double pos_x = now_x - stick.getFitWidth() / 2;
//            double pos_y = now_y - stick.getFitHeight() / 2 + 4;
//            stick.setLayoutX(pos_x);
//            stick.setLayoutY(pos_y);
//        }
//    }
//
//    private boolean collides(Circle circle, Ball b) {
//        double x = circle.getCenterX() - b.getPosition().getX();
//        double y = circle.getCenterY() - b.getPosition().getY();
//        double dist = Math.sqrt(x * x + y * y);
//        return dist - GameParameters.BALL_RADIUS * 2 <= 0 && dist - (GameParameters.BALL_RADIUS) >= -3;
//    }
//
//    public void released(MouseEvent event) {
//        if (GameScene.isTurn() && !GameScene.isGameOver() && !GameScene.isGamePause() && GameScene.getPlayer1().isMyTurn()) {
//            double x = event.getSceneX();
//            double y = event.getSceneY();
//            xp = x;
//            yp = y;
////            MyApp.outToServer.println("st#" + (int) stick.getRotate() + "#" + (int) stick.getLayoutX() + "#" + (int) stick.getLayoutY());
//        }
//
//    }
//
//    public void showVelocity() {
//        if (!GameScene.isGameOver() && !GameScene.isGamePause() && GameScene.getPlayer1().isMyTurn()) {
//            velocityLabel.setText(String.valueOf(Math.floor(velocitySlider.getValue() / 30 * 100)));
//            stick.setLayoutX(GameScene.getCueBall().getPosition().getX() - (346 + 36) - (Math.floor(velocitySlider.getValue() / 30 * 100)));
//            stick.setLayoutY(GameScene.getCueBall().getPosition().getY() - (375 - 367));
//            ang = Math.toDegrees(Math.atan((yp - GameScene.getCueBall().getPosition().getY()) / (xp - GameScene.getCueBall().getPosition().getX())));
//            if (GameScene.getCueBall().getPosition().getX() >= xp) {
//                ang = 180 - ang;
//                ang = -ang;
//            }
//            double mid_x = stick.getLayoutX() + stick.getFitWidth() / 2;
//            double mid_y = stick.getLayoutY() + stick.getFitHeight() / 2;
//            double dist = (GameScene.getCueBall().getPosition().getX() - mid_x);
//            double now_y = Math.sin(Math.toRadians(-ang)) * dist + mid_y;
//            double now_x = mid_x + (dist - dist * Math.cos(Math.toRadians(ang)));
//            stick.setLayoutX(now_x - stick.getFitWidth() / 2);
//            stick.setLayoutY(now_y - stick.getFitHeight() / 2);
//        }
//
//    }
//
//    public void mereDaw() {
//        double cueBallVelocity = 0;
//        if (GameScene.isTurn() && !GameScene.isGameOver() && xp != -1 && yp != -1 && !GameScene.isGamePause() && GameScene.getPlayer1().isMyTurn()) {
//
//            if (GameScene.getTurnNum() == 1 && !GameScene.isTurnOffSounds()) {
////                SoundEffects.START.play();
//            }
//            cueBallVelocity = velocitySlider.getValue();
//            if (cueBallVelocity != 0) {
//                line.setVisible(false);
//                circle.setVisible(false);
//                velocitySlider.setValue(0);
//                velocityLabel.setText("0");
//                double angle = Math.atan((yp - GameScene.getCueBall().getPosition().getY()) / (xp - GameScene.getCueBall().getPosition().getX()));
//                if (xp < GameScene.getCueBall().getPosition().getX()) cueBallVelocity = -cueBallVelocity;
//                GameScene.setVelocity(cueBallVelocity * Math.cos(angle), cueBallVelocity * Math.sin(angle));
//
//                xp = -1;
//                yp = -1;
//                stick.setVisible(false);
////                MyApp.outToServer.println("stf");
//                dirLine1.setVisible(false);
//            }
//
//        } else {
//            velocitySlider.setValue(0);
//            velocityLabel.setText("0");
//        }
//    }
//}
