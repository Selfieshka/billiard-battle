module ru.kpfu.itis.kirillakhmetov.billiardbattle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;
    requires java.sql;

    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.scene;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.controller;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.entity;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.client;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.server;

    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.controller to javafx.fxml;
    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.view to javafx.fxml;
    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.scene to javafx.fxml;
    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.server to javafx.fxml;
    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.entity;
}