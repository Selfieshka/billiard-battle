package ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    private String name = "";
    private String pass = "";
    private String fbID = "";
    private int money;
    private boolean loggedIn;
    private boolean isPlaying;
}
