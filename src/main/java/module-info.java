module ru.kpfu.itis.kirillakhmetov.billiardbattle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;

    exports ru.kpfu.itis.kirillakhmetov.billiardbattle;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.controller;
    exports ru.kpfu.itis.kirillakhmetov.billiardbattle.entity;

    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.controller to javafx.fxml;
    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.view to javafx.fxml;
    opens ru.kpfu.itis.kirillakhmetov.billiardbattle.entity;

}