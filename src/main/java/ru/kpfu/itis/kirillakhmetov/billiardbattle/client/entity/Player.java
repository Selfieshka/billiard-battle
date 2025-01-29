package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Player {
    private String id;
    private String username;
    private String password;
    private int balance;
    private boolean win;
    private int ballType;
    private boolean isMyTurn;
    private boolean allBallsPotted;

    public Player() {
        this.username = "";
        this.id = "";
    }

    public Player(String name) {
        this.id = "";
        this.username = name;
    }
}
