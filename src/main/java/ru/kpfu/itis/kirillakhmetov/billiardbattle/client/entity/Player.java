package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Player {
    private boolean isMyturn;
    private String name, ID;
    private boolean win;
    private String password;
    private int ballType;//1 will mean solids 2 will mean stripes 0 will mean not set
    private boolean allBallsPotted;
    private int balance;

    public Player() {
        isMyturn = false;
        win = false;
        ballType = 0;
        name = "";
        allBallsPotted = false;
        ID = "";
        balance = 0;
    }

    public Player(String name) {
        isMyturn = false;
        win = false;
        ballType = 0;
        this.name = name;
        allBallsPotted = false;
        ID = "";
        balance = 0;
    }
}
