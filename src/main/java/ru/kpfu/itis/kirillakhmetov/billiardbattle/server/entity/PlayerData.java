package ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class PlayerData {
    private String username;
    private String password;
    private int money;
    private boolean loggedIn;
    private boolean isPlaying;

    public PlayerData() {
        this.username = "";
        this.password = "";
    }
}
