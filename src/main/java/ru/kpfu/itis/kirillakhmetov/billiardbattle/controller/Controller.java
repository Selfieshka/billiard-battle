package ru.kpfu.itis.kirillakhmetov.billiardbattle.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.entity.GameParameters;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.entity.SingleBall;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.entity.Vector2;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.scene.GameScene2;

import java.util.Objects;


public class Controller {
    @FXML
    public Line line;
    @FXML
    public Circle circle;
    @FXML
    public ImageView border1, border2, border3, border4;
    @FXML
    Slider velocitySlider;
    @FXML
    Label velocityLabel;
    @FXML
    public ImageView stick;
    @FXML
    public Line dirLine1;

    double ang;

    private double xp = -1, yp = -1;

    @FXML
    public void initialize() {
        Image stickImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/cuesticks/cue-stick.png")));
        Image border1Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/ballimages/img1.jpg")));
        Image border2Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/itis/kirillakhmetov/billiardbattle/img/ballimages/img2.jpg")));
        stick.setImage(stickImg);
        border1.setImage(border1Img);
        border2.setImage(border1Img);
        border3.setImage(border2Img);
        border4.setImage(border2Img);
    }

    public void Moveline(MouseEvent event) {
        if (GameScene2.isIsTurn() && !GameScene2.isGameOver() && !GameScene2.isGamePause() && GameScene2.getPlayer1().isMyturn()) {
            line.setVisible(true);
            circle.setVisible(true);
            line.setStroke(Color.WHITE);
            circle.setStroke(Color.WHITE);
            double x2 = event.getSceneX(), y2 = event.getSceneY();
            double x1 = GameScene2.getCueBall().getPosition().getX(), y1 = GameScene2.getCueBall().getPosition().getY();
            line.setStartX(x1);
            line.setStartY(y1);
            line.setEndX(x2);
            line.setEndY(y2);
            circle.setCenterX(x2);
            circle.setCenterY(y2);
            circle.setRadius(GameParameters.BALL_RADIUS);
            int flag = 0;
            for (int i = 0; i < 16; i++) {
                if (collides(circle, GameScene2.ball[i])) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 1) {
                dirLine1.setVisible(true);
            } else dirLine1.setVisible(false);
            for (int i = 1; i < 16; i++) {
                if (collides(circle, GameScene2.ball[i])) {
                    double cueBallVelocity = 40;
                    double angle = Math.atan((y2 - y1) / (x2 - x1));
                    if (x2 < x1) cueBallVelocity = -cueBallVelocity;
                    Vector2 position = new Vector2(circle.getCenterX(), circle.getCenterY());
                    Vector2 velocity = new Vector2(cueBallVelocity * Math.cos(angle), cueBallVelocity * Math.sin(angle));
                    Vector2 nv2 = position.sub(GameScene2.ball[i].getPosition());
                    nv2.normalize();
                    nv2.multiply(velocity.dot(nv2));
                    Vector2 nv2b = GameScene2.ball[i].getPosition().sub(position);
                    nv2b.normalize();
                    nv2b.multiply(GameScene2.ball[i].getVelocity().dot(nv2b));
                    Vector2 nv1b = GameScene2.ball[i].getVelocity().sub(nv2b);
                    double p = GameScene2.ball[i].getSphere().getLayoutX(), q = GameScene2.ball[i].getSphere().getLayoutY();
                    Vector2 v = nv2.add(nv1b);
                    dirLine1.setStartX(p);
                    dirLine1.setStartY(q);
                    dirLine1.setEndX(p + v.getX());
                    dirLine1.setEndY(q + v.getY());
                }
            }

            stick.setVisible(true);
            stick.setLayoutX(x1 - (346 + 36));
            stick.setLayoutY(y1 - 14);
            ang = Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));
            if (x2 <= x1) {
                ang = 180 - ang;
                ang = -ang;
            }
            stick.setRotate(ang);
            double mid_x = stick.getLayoutX() + stick.getFitWidth() / 2;
            double mid_y = stick.getLayoutY() + stick.getFitHeight() / 2;
            double dist = (x1 - mid_x);
            double now_y = Math.sin(Math.toRadians(-ang)) * dist + mid_y;
            double now_x = mid_x + (dist - dist * Math.cos(Math.toRadians(ang)));
            double pos_x = now_x - stick.getFitWidth() / 2;
            double pos_y = now_y - stick.getFitHeight() / 2 + 4;
            stick.setLayoutX(pos_x);
            stick.setLayoutY(pos_y);


        }
    }

    private boolean collides(Circle circle, SingleBall b) {
        double x = circle.getCenterX() - b.getPosition().getX();
        double y = circle.getCenterY() - b.getPosition().getY();
        double dist = Math.sqrt(x * x + y * y);
        if (dist - GameParameters.BALL_RADIUS * 2 <= 0 && dist - GameParameters.BALL_RADIUS >= -3) {

            return true;
        } else return false;
    }

    public void released(MouseEvent event) {
        if (GameScene2.isIsTurn() && !GameScene2.isGameOver() && !GameScene2.isGamePause() && GameScene2.getPlayer1().isMyturn()) {
            double x = event.getSceneX();
            double y = event.getSceneY();
            xp = x;
            yp = y;
//            Main.outToServer.println("st#" + (int) stick.getRotate() + "#" + (int) stick.getLayoutX() + "#" + (int) stick.getLayoutY());
        }

    }

    public void showVelocity() {
        if (!GameScene2.isGameOver() && !GameScene2.isGamePause() && GameScene2.getPlayer1().isMyturn()) {
            velocityLabel.setText(String.valueOf(Math.floor(velocitySlider.getValue() / 30 * 100)));
            stick.setLayoutX(GameScene2.getCueBall().getPosition().getX() - (346 + 36) - (Math.floor(velocitySlider.getValue() / 30 * 100)));
            stick.setLayoutY(GameScene2.getCueBall().getPosition().getY() - (375 - 367));
            ang = Math.toDegrees(Math.atan((yp - GameScene2.getCueBall().getPosition().getY()) / (xp - GameScene2.getCueBall().getPosition().getX())));
            if (GameScene2.getCueBall().getPosition().getX() >= xp) {
                ang = 180 - ang;
                ang = -ang;
            }
            double mid_x = stick.getLayoutX() + stick.getFitWidth() / 2;
            double mid_y = stick.getLayoutY() + stick.getFitHeight() / 2;
            double dist = (GameScene2.getCueBall().getPosition().getX() - mid_x);
            double now_y = Math.sin(Math.toRadians(-ang)) * dist + mid_y;
            double now_x = mid_x + (dist - dist * Math.cos(Math.toRadians(ang)));
            stick.setLayoutX(now_x - stick.getFitWidth() / 2);
            stick.setLayoutY(now_y - stick.getFitHeight() / 2);
        }

    }

    public void mereDaw() {
        double cueBallVelocity = 0;
        if (GameScene2.isIsTurn() && !GameScene2.isGameOver() && xp != -1 && yp != -1 && !GameScene2.isGamePause() && GameScene2.getPlayer1().isMyturn()) {

            if (GameScene2.getTurnNum() == 1 && !GameScene2.isTurnOffSounds()) {
//                SoundEffects.START.play();
            }
            cueBallVelocity = velocitySlider.getValue();
            if (cueBallVelocity != 0) {
                line.setVisible(false);
                circle.setVisible(false);
                velocitySlider.setValue(0);
                velocityLabel.setText("0");
                double angle = Math.atan((yp - GameScene2.getCueBall().getPosition().getY()) / (xp - GameScene2.getCueBall().getPosition().getX()));
                if (xp < GameScene2.getCueBall().getPosition().getX()) cueBallVelocity = -cueBallVelocity;
                GameScene2.setVelocity(cueBallVelocity * Math.cos(angle), cueBallVelocity * Math.sin(angle));

                xp = -1;
                yp = -1;
                stick.setVisible(false);
//                Main.outToServer.println("stf");
                dirLine1.setVisible(false);
            }

        } else {
            velocitySlider.setValue(0);
            velocityLabel.setText("0");
        }
    }


}
