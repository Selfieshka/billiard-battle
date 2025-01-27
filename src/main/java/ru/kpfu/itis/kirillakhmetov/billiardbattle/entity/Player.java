package ru.kpfu.itis.kirillakhmetov.billiardbattle.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class Player {
    private boolean isMyTurn;
    private String name, ID;
    private boolean win;
    private BallType ballType;
    private boolean allBallsPotted;
    private int balance;

    public Player(String name) {
        this.name = name;
    }
}
