package ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerProperties {
    public static final String HOST = "localhost";
    public static final int PORT = 5123;
}
