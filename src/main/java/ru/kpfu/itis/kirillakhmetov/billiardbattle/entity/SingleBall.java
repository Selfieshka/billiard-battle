package ru.kpfu.itis.kirillakhmetov.billiardbattle.entity;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import lombok.ToString;

import static javafx.scene.paint.Color.WHITE;

@ToString
public class SingleBall {
    private double acceleration, diameter;
    private Vector2 position, velocity, initialPosition;
    private Sphere sphere = new Sphere(GameParameters.BALL_RADIUS);
    private String image;
    private int ballType; //PASS 0 FOR CUE BALL ,1 FOR SOLID BALL, 2 FOR STRIPED BALL ,3 for Black Ball
    private int ballNumber;
    private boolean isDropped;

    public SingleBall(double positionX, double positionY, String image, int ballType, int ballNumber) {
        position = new Vector2(positionX, positionY);
        velocity = new Vector2(0, 0);
        this.image = image;
        this.ballType = ballType;
        this.ballNumber = ballNumber;
        isDropped = false;
        acceleration = .99;
        diameter = 25.0;
        initialPosition = new Vector2(positionX, positionY);
    }

    public Node DrawBall() {
        sphere.setRadius(GameParameters.BALL_RADIUS);
        sphere.setLayoutX(position.getX());
        sphere.setLayoutY(position.getY());
        sphere.setRotationAxis(Rotate.Y_AXIS);
        sphere.setRotate(270);
        Image img = new Image(getClass().getResourceAsStream(image));
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

    public boolean collides(SingleBall b) {
        double a = position.sub(b.position).getSize();
        return position.sub(b.position).getSize() <= (diameter / 2 + b.diameter / 2);
    }

    public void transferEnergy(SingleBall b) {
        Vector2 nv2 = position.sub(b.position);
        nv2.normalize();
        nv2.multiply(velocity.dot(nv2));
        Vector2 nv1 = velocity.sub(nv2);
        Vector2 nv2b = b.position.sub(position);
        nv2b.normalize();
        nv2b.multiply(b.velocity.dot(nv2b));
        Vector2 nv1b = b.velocity.sub(nv2b);
        b.velocity = nv2.add(nv1b);
        velocity = nv1.add(nv2b);
    }


    public void updateWallCollision() {
        double x = position.getX(), y = position.getY(), r = diameter / 2.0;
        //This is another
        if (x - r <= 147 && (y >= 196 && y <= 557)) {
            velocity.setX(Math.abs(velocity.getX()));
        } else if (x - r <= 147 && (y >= 557 && y <= 570)) {
            //velocity.setY (Math.abs (velocity.getY ()));
            velocity.setY(velocity.getSize());
            velocity.setX(0);
        } else if (x - r <= 147 && (y >= 178 && y <= 196)) {
            //velocity.setY (-Math.abs (velocity.getY ()));
            velocity.setY(-velocity.getSize());
            velocity.setX(0);
        }
        //This is another
        if (x + r >= 952 && (y >= 193 && y <= 551)) {
            velocity.setX(-Math.abs(velocity.getX()));
        } else if (x + r >= 952 && (y >= 180 && y <= 192)) {
            //velocity.setY (-Math.abs (velocity.getY ()));
            velocity.setY(-velocity.getSize());
            velocity.setX(0);

        } else if (x + r >= 952 && (y >= 551 && y <= 570)) {
            //velocity.setY (Math.abs (velocity.getY ()));
            velocity.setY(velocity.getSize());
            velocity.setX(0);
        }
        //This is another
        if (y + r >= 578 && (x >= 174 && x <= 515)) {
            velocity.setY(-Math.abs(velocity.getY()));
        } else if (y + r >= 578 && (x >= 153 && x <= 174)) {
            //velocity.setX (-Math.abs (velocity.getX ()));
            velocity.setX(-velocity.getSize());
            velocity.setY(0);
        } else if (y + r >= 578 && (x >= 515 && x <= 526)) {
            velocity.setX(Math.abs(velocity.getX()));
        }
        //This is another
        if (y - r <= 172 && (x >= 170 && x <= 515)) {
            velocity.setY(Math.abs(velocity.getY()));
        } else if (y - r <= 172 && (x >= 153 && x <= 170)) {
            //velocity.setX (-Math.abs (velocity.getX ()));
            velocity.setX(-velocity.getSize());
            velocity.setY(0);

        } else if (y - r <= 172 && (x >= 515 && x <= 529)) {
            velocity.setX(Math.abs(velocity.getX()));

        }

        //This is one
        if (y - r <= 172 && (x >= 580 && x <= 924)) {
            velocity.setY(Math.abs(velocity.getY()));
        } else if (y - r <= 172 && (x >= 568 && x <= 580)) {
            velocity.setX(-Math.abs(velocity.getX()));

        } else if (y - r <= 172 && (x >= 924 && x <= 945)) {
            //velocity.setX (Math.abs (velocity.getX ()));
            velocity.setX(velocity.getSize());
            velocity.setY(0);

        }
        //This is another
        if (y + r >= 574 && (x >= 584 && x <= 928)) {
            velocity.setY(-Math.abs(velocity.getY()));
        } else if (y + r >= 574 && (x >= 568 && x <= 584)) {
            velocity.setX(-Math.abs(velocity.getX()));
        } else if (y + r >= 574 && (x >= 928 && x <= 944)) {
            //velocity.setX (Math.abs (velocity.getX ()));
            velocity.setX(velocity.getSize());
            velocity.setY(0);
        }
    }


    public void applyTableFriction() {
        velocity.setX(velocity.getX() * acceleration);
        velocity.setY(velocity.getY() * acceleration);
    }


    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(double x, double y) {
        this.velocity.setX(x);
        this.velocity.setY(y);
    }

    public Sphere getSphere() {
        return sphere;
    }

    public int getBallType() {
        return ballType;
    }

    public int getBallNumber() {
        return ballNumber;
    }

    public boolean getisDropped() {
        return isDropped;
    }

    public void setDropped(boolean dropped) {
        isDropped = dropped;
    }

    public Vector2 getInitialPosition() {
        return initialPosition;
    }
}
