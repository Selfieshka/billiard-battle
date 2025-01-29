module ru.kpfu.itis.kirillakhmetov.billiardbattle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;
    requires java.sql;
    requires org.postgresql.jdbc;

    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.client;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.server;

    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.client.controller to javafx.fxml;
    opens view to javafx.fxml;
    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.client.scene to javafx.fxml;
    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.server to javafx.fxml;
    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity;
}
