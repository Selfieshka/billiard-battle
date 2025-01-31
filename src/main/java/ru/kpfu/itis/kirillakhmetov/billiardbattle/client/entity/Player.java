package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Player {
    private String username;
    private String password;
    private int balance;
    private boolean win;
    private BallType ballType;
    private boolean isMyTurn;
    private boolean allBallsPotted;

    public Player() {
        this.username = "";
    }

    public Player(String name) {
        this.username = name;
    }
}
