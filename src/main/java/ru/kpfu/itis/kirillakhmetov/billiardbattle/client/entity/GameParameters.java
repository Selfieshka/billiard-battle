package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class GameParameters {
    public static final double BALL_RADIUS = 12.5;
    public static final double BALL_DIAMETER = BALL_RADIUS * 2;
    public static final int BALL_START_X = 769;
    public static final int BALL_START_Y = 375;
    public static final double CUE_BALL_VELOCITY = 40d;
    public static final double ACCELERATION = 0.99;
}
