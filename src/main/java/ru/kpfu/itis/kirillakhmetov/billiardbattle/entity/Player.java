package ru.kpfu.itis.kirillakhmetov.billiardbattle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Player {
    private boolean isMyturn;
    private String name, ID;
    private boolean win;
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
