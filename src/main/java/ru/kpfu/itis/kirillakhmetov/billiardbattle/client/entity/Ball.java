package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

import static javafx.scene.paint.Color.WHITE;
import static ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.GameParameters.*;

@Data
public class Ball {
    private final Vector initialPosition;
    private Vector position;
    private Vector velocity;
    private final Sphere sphere;
    private final String image;
    private final int ballType;
    private final int ballNumber;
    private boolean isDropped;

    public Ball(double positionX, double positionY, String image, int ballType, int ballNumber) {
        this.position = new Vector(positionX, positionY);
        this.initialPosition = new Vector(positionX, positionY);
        this.velocity = new Vector(0, 0);
        this.image = image;
        this.ballType = ballType;
        this.ballNumber = ballNumber;
        this.sphere = new Sphere(BALL_RADIUS);
    }

    public Node DrawBall() {
        sphere.setRadius(BALL_RADIUS);
        sphere.setLayoutX(position.getX());
        sphere.setLayoutY(position.getY());
        sphere.setRotationAxis(Rotate.Y_AXIS);
        sphere.setRotate(270);

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(image)));
        PhongMaterial material = new PhongMaterial();
        material.setSpecularColor(WHITE);
        material.setDiffuseMap(img);
        material.setSpecularPower(30);
        sphere.setMaterial(material);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setSpread(30);
        dropShadow.setOffsetX(10);
        dropShadow.setOffsetY(10);
        sphere.setEffect(dropShadow);
        return sphere;
    }

    public void spin() {
        Rotate rx = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate ry = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        rx.setAngle(Math.toDegrees(velocity.getY() / 10));
        ry.setAngle(-Math.toDegrees(velocity.getX() / 10));
        sphere.getTransforms().addAll(rx, ry);
    }

    public boolean collides(Ball b) {
        return position.sub(b.position).getSize() <= (BALL_DIAMETER);
    }

    public void transferEnergy(Ball b) {
        Vector nv2 = position.sub(b.position);
        nv2.normalize();
        nv2.multiply(velocity.dot(nv2));
        Vector nv1 = velocity.sub(nv2);
        Vector nv2b = b.position.sub(position);
        nv2b.normalize();
        nv2b.multiply(b.velocity.dot(nv2b));
        Vector nv1b = b.velocity.sub(nv2b);
        b.velocity = nv2.add(nv1b);
        velocity = nv1.add(nv2b);
    }

    public void updateWallCollision() {
        double x = position.getX();
        double y = position.getY();

        if (x - BALL_RADIUS <= 147 && (y >= 196 && y <= 557)) {
            velocity.setX(Math.abs(velocity.getX()));
        } else if (x - BALL_RADIUS <= 147 && (y >= 557 && y <= 570)) {
            velocity.setY(velocity.getSize());
            velocity.setX(0);
        } else if (x - BALL_RADIUS <= 147 && (y >= 178 && y <= 196)) {
            velocity.setY(-velocity.getSize());
            velocity.setX(0);
        }

        if (x + BALL_RADIUS >= 952 && (y >= 193 && y <= 551)) {
            velocity.setX(-Math.abs(velocity.getX()));
        } else if (x + BALL_RADIUS >= 952 && (y >= 180 && y <= 192)) {
            velocity.setY(-velocity.getSize());
            velocity.setX(0);
        } else if (x + BALL_RADIUS >= 952 && (y >= 551 && y <= 570)) {
            velocity.setY(velocity.getSize());
            velocity.setX(0);
        }

        if (y + BALL_RADIUS >= 578 && (x >= 174 && x <= 515)) {
            velocity.setY(-Math.abs(velocity.getY()));
        } else if (y + BALL_RADIUS >= 578 && (x >= 153 && x <= 174)) {
            velocity.setX(-velocity.getSize());
            velocity.setY(0);
        } else if (y + BALL_RADIUS >= 578 && (x >= 515 && x <= 526)) {
            velocity.setX(Math.abs(velocity.getX()));
        }

        if (y - BALL_RADIUS <= 172 && (x >= 170 && x <= 515)) {
            velocity.setY(Math.abs(velocity.getY()));
        } else if (y - BALL_RADIUS <= 172 && (x >= 153 && x <= 170)) {
            velocity.setX(-velocity.getSize());
            velocity.setY(0);
        } else if (y - BALL_RADIUS <= 172 && (x >= 515 && x <= 529)) {
            velocity.setX(Math.abs(velocity.getX()));
        }

        if (y - BALL_RADIUS <= 172 && (x >= 580 && x <= 924)) {
            velocity.setY(Math.abs(velocity.getY()));
        } else if (y - BALL_RADIUS <= 172 && (x >= 568 && x <= 580)) {
            velocity.setX(-Math.abs(velocity.getX()));
        } else if (y - BALL_RADIUS <= 172 && (x >= 924 && x <= 945)) {
            velocity.setX(velocity.getSize());
            velocity.setY(0);
        }

        if (y + BALL_RADIUS >= 574 && (x >= 584 && x <= 928)) {
            velocity.setY(-Math.abs(velocity.getY()));
        } else if (y + BALL_RADIUS >= 574 && (x >= 568 && x <= 584)) {
            velocity.setX(-Math.abs(velocity.getX()));
        } else if (y + BALL_RADIUS >= 574 && (x >= 928 && x <= 944)) {
            velocity.setX(velocity.getSize());
            velocity.setY(0);
        }
    }

    public void applyTableFriction() {
        velocity.setX(velocity.getX() * ACCELERATION);
        velocity.setY(velocity.getY() * ACCELERATION);
    }

    public void setVelocity(double x, double y) {
        this.velocity.setX(x);
        this.velocity.setY(y);
    }
}
