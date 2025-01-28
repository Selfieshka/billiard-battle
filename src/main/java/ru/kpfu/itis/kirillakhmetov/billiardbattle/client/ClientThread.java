package ru.kpfu.itis.kirillakhmetov.billiardbattle.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.controller.Controller;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.controller.OnlinePlayersController;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.entity.Vector2;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.scene.GameScene2;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class ClientThread implements Runnable {
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private Controller controller;

    public ClientThread(PrintWriter outToServer, BufferedReader inFromServer, Controller controller) throws Exception {
        this.controller = controller;
        this.outToServer = outToServer;
        this.inFromServer = inFromServer;
    }

    public void run() {
        while (true) {
            try {
                String string = inFromServer.readLine();
                System.out.println(string);
                if (string != null) {
                    //System.out.println ("FROM SERVER: " + string);
                    ArrayList<String> str = new ArrayList<>();
                    for (String val : string.split("[#]")) {
                        if (!val.isEmpty()) {
                            str.add(val);
                        }
                    }
                    //this is cue ball velocity
                    if (str.get(0).compareTo("V") == 0) {
                        double doubles[] = new double[2];
                        doubles[0] = Double.parseDouble(str.get(1));
                        doubles[1] = Double.parseDouble(str.get(2));
                        //System.out.println (doubles[0]+" "+doubles[1]);
                        GameScene2.ball[0].setVelocity(doubles[0], doubles[1]);
                    }
                    //this is cue stick move
                    else if (str.get(0).compareTo("st") == 0) {
                        controller.stick.setVisible(true);
                        controller.stick.setRotate(Double.parseDouble(str.get(1)));
                        controller.stick.setLayoutX(Double.parseDouble(str.get(2)));
                        controller.stick.setLayoutY(Double.parseDouble(str.get(3)));
                    }

                    //this is stick disable
                    else if (str.get(0).compareTo("stf") == 0) {
                        controller.stick.setVisible(false);
                    }
                    //this is cue ball move
                    else if (str.get(0).compareTo("M") == 0) {

                        double doubles[] = new double[2];
                        doubles[0] = Double.parseDouble(str.get(1));
                        doubles[1] = Double.parseDouble(str.get(2));
                        GameScene2.ball[0].setPosition(new Vector2(doubles[0], doubles[1]));
                    }


                    //this is left game true
                    else if (str.get(0).compareTo("Lt") == 0) {
                        GameScene2.getPlayer1().setWin(true);
                        GameScene2.getPlayer2().setWin(false);
                        GameScene2.setGameOver(true);
                    }
                    //this is breaking assign
                    else if (str.get(0).compareTo("login2") == 0) {
                        System.out.println(str);
                        GameScene2.getPlayer2().setName(str.get(1));
                        GameScene2.getPlayer2().setID(str.get(2));
                        GameScene2.setImage2(str.get(2));
                        GameScene2.setBet(Integer.parseInt(str.get(3)));
                        if (str.get(4).compareTo("true") == 0) {
                            GameScene2.getPlayer2().setMyturn(false);
                            GameScene2.getPlayer1().setMyturn(true);
                        } else {
                            GameScene2.getPlayer1().setMyturn(false);
                            GameScene2.getPlayer2().setMyturn(true);
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MyApp.window.setScene(MyApp.game);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }

                            }
                        });
                    }
                    //this is login assign
                    else if (str.get(0).compareTo("login") == 0) {
                        if (str.get(1).compareTo("false") == 0) {
                            if (str.size() > 2) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error!");
                                        alert.setHeaderText("Can't log you in!");
                                        alert.setContentText("You are already logged in from another device!");
                                        alert.show();
                                    }
                                });
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error!");
                                        alert.setHeaderText("Can't log you in!");
                                        alert.setContentText("User name or password is not correct!");
                                        alert.show();
                                    }
                                });
                            }
                        } else {

                            try {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Welcome");
                                        alert.setHeaderText("Logged in as " + str.get(1));
                                        alert.show();
                                        MyApp.window.setScene(MyApp.menu);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            GameScene2.getPlayer1().setName(str.get(1));
                            GameScene2.getPlayer1().setID(str.get(2));
                            GameScene2.setImage1(str.get(2));
                            GameScene2.getPlayer1().setBalance(Integer.parseInt(str.get(3)));
                        }
                    }
                    //this is signup assign
                    else if (str.get(0).compareTo("signup") == 0) {
                        if (str.get(1).compareTo("true") == 0) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Welcome");
                                    alert.setHeaderText("Congratulations! Signed up successful!");
                                    alert.show();
                                    MyApp.window.setScene(MyApp.login);
                                }
                            });
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Error!");
                                    alert.setHeaderText("Username already in use");
                                    alert.show();
                                }
                            });
                        }
                    }
                    //this is profile assign
                    else if (str.get(0).compareTo("profile") == 0) {
                        //System.out.println (str.get (1)+" "+str.get (2)+" "+str.get (3));
//                        ProfileScene.id = str.get(1);
//                        ProfileScene.gamesplayed = str.get(2);
//                        ProfileScene.gameswon = str.get(3);
//                        ProfileScene.money = str.get(4);
//                        ProfileScene.name = GameScene2.getPlayer1().getName();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Parent root2 = FXMLLoader.load(getClass().getResource("ProfileScene.fxml"));
//                                    MyApp.profile = new Scene(root2);
//                                    MyApp.window.setScene(Main.profile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                    //this is leaderboard show
                    else if (str.get(0).compareTo("start") == 0) {
//                        LeaderBoard.users = FXCollections.observableArrayList();
                    } else if (str.get(0).compareTo("leaderboard") == 0) {
//                        LeaderBoard.users.add(new User(str.get(1), Integer.valueOf(str.get(2)), Integer.valueOf(str.get(3)), Integer.valueOf(str.get(4))));
                    } else if (str.get(0).compareTo("end") == 0) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Parent root3 = FXMLLoader.load(getClass().getResource("LeaderBoard.fxml"));
//                                    MyApp.leaderBoard = new Scene(root3);
//                                    MyApp.window.setScene(Main.leaderBoard);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                    //done loading leaderboard
                    //this is activeplayerlist show
                    else if (str.get(0).compareTo("startActive") == 0) {
                        OnlinePlayersController.strings = FXCollections.observableArrayList();
                    } else if (str.get(0).compareTo("active") == 0) {
                        OnlinePlayersController.strings.add(str.get(1));
                    } else if (str.get(0).compareTo("endActive") == 0) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ru/kpfu/itis/kirillakhmetov/billiardbattle/view/online-players.fxml")));
                                    MyApp.onlinePlayers = new Scene(root);
                                    MyApp.window.setScene(MyApp.onlinePlayers);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    //Done showing active player list
                    //this is not enough balance alert and game request alert
                    else if (str.get(0).compareTo("canPlay") == 0) {
                        if (str.get(1).compareTo("false") == 0) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Error!");
                                    alert.setHeaderText("Opponent doesn't have enough money!!");
                                    alert.setContentText("Opponents balance is " + str.get(2));
                                    alert.show();
                                }
                            });

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("New Game Request!");
                                    alert.setHeaderText(str.get(1) + " challenged you to a match of " + str.get(2) + " dollars!");
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get() == ButtonType.OK) {
                                        MyApp.outToServer.println("play#" + str.get(1) + "#" + str.get(2));
                                    } else {
                                        MyApp.outToServer.println("reject#" + str.get(1));
                                    }
                                }
                            });

                        }
                    }
                    //this is reject notice
                    else if (str.get(0).compareTo("reject") == 0) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Error!");
                                alert.setHeaderText("Opponent is afraid to play with you!!");
                                alert.show();
                            }
                        });
                    }


                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
