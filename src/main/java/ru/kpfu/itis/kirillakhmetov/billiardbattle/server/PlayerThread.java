package ru.kpfu.itis.kirillakhmetov.billiardbattle.server;

import lombok.Getter;
import lombok.Setter;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Player;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.service.PlayerService;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlayerThread implements Runnable {
    @Getter
    @Setter
    private PrintWriter outToClient;
    @Getter
    @Setter
    private PrintWriter outToMyClient;
    @Getter
    @Setter
    private PlayerData thisPlayer;
    private final BufferedReader inFromClient;
    private static boolean turn;
    private static final ArrayList<PlayerThread> playerThreads = new ArrayList<>();
    private final PlayerService playerService;

    public PlayerThread(BufferedReader inFromClient, PrintWriter outToMyClient) {
        this.inFromClient = inFromClient;
        this.outToMyClient = outToMyClient;
        playerService = new PlayerService();
        thisPlayer = new PlayerData();
    }

    public void run() {
        while (true) {
            try {
                String sentence = inFromClient.readLine();
                System.out.println("сообщение" + sentence);
                if (sentence != null) {
                    List<String> str = Arrays.stream(sentence.split("#"))
                            .filter(val -> !val.isEmpty())
                            .toList();

                    if (sentence.charAt(0) == 'W') {
                        System.out.println(str.get(1) + " wins.. " + str.get(2) + " loses..");
                        updateMoneyAfterEndGame(str.get(1), str.get(2), Integer.parseInt(str.get(3)));
                    }
                    // Отправка первому игроку информации о втором игроке и старт игры
                    else if (str.get(0).compareTo("login2") == 0) {
                        if (turn) {
                            sentence += "#true";
                            turn = false;
                        } else {
                            sentence += "#false";
                            turn = true;
                        }
                        outToClient.println(sentence);
                    }
                    // Вход
                    else if (str.get(0).compareTo("login") == 0) {
                        System.out.println(str);
                        signIn(str.get(1), str.get(2));
                    }
                    // Регистрация
                    else if (str.get(0).compareTo("signup") == 0) {
                        signUp(str.get(1), str.get(2), str.get(3));
                    }
//                    //next case profile info pathaw
//                    else if (str.get(0).compareTo("profile") == 0) {
//                        showProfile(str.get(1));
//                    }
//                    //next case leaderboard pathaw
//                    else if (str.get(0).compareTo("leaderboard") == 0) {
//                        sendLeaderBoard();
//                    }
                    // Отправка активных игроков
                    else if (str.get(0).compareTo("active") == 0) {
                        sendActivePlayers(str.get(1));
                    }
                    // Проверяем, может ли выбранный игрок сыграть на n-ое количество денег
                    else if (str.get(0).compareTo("canPlay") == 0) {
                        sendOtherPlayer(str.get(1), Integer.parseInt(str.get(2)));
                    }
                    // Отмена приглашения в игру
                    else if (str.get(0).compareTo("reject") == 0) {
                        for (PlayerThread playerThread : playerThreads) {
                            if (playerThread.getThisPlayer().getName().compareTo(str.get(1)) == 0) {
                                playerThread.getOutToMyClient().println(sentence);
                            }
                        }
                    }
                    // Устанавливаем соединения между двумя игроками
                    else if (str.get(0).compareTo("play") == 0) {
                        setupGameSession(str.get(1), Integer.parseInt(str.get(2)));
                    }
                    // Выход из аккаунта
                    else if (str.get(0).compareTo("logout") == 0) {
                        thisPlayer.setLoggedIn(false);
                    }
                    // Некорректный запрос отправляем обратно
                    else {
                        outToClient.println(sentence);
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setupGameSession(String opponent, int bet) {
        for (PlayerThread playerThread : playerThreads) {
            if (playerThread.getThisPlayer().getName().compareTo(opponent) == 0 && playerThread.getThisPlayer().isLoggedIn()) {
                outToClient = playerThread.getOutToMyClient();
                playerThread.setOutToClient(outToMyClient);
                thisPlayer.setPlaying(true);
                playerThread.getThisPlayer().setPlaying(true);
                outToMyClient.println("login2#" + opponent + "#" + playerThread.getThisPlayer().getFbID() + "#" + bet + "#true");
                outToClient.println("login2#" + thisPlayer.getName() + "#" + thisPlayer.getFbID() + "#" + bet + "#false");
                break;
            }
        }
    }

    private void sendOtherPlayer(String name, int money) {
        Optional<Player> playerFromDb = playerService.getByName(name);
        if (playerFromDb.isPresent()) {
            Player player = playerFromDb.get();
            if (player.getBalance() < money) {
                outToMyClient.println("canPlay#false#" + money);
            } else {
                for (PlayerThread playerThread : playerThreads) {
                    if (playerThread.getThisPlayer().getName().compareTo(name) == 0 && playerThread.getThisPlayer().isLoggedIn()) {
                        System.out.println("canPlay#" + thisPlayer.getName() + "#" + money);
                        playerThread.getOutToMyClient().println("canPlay#" + thisPlayer.getName() + "#" + money);
                        break;
                    }
                }
            }
        } else {
            outToMyClient.println("canPlay#false#" + money);
        }
    }

    private void sendActivePlayers(String username) {
        outToMyClient.println("startActive#");
        for (PlayerThread playerThread : playerThreads) {
            if (playerThread.getThisPlayer().isLoggedIn() && playerThread.getThisPlayer().getName().compareTo(username) != 0 && !playerThread.getThisPlayer().isPlaying()) {
                outToMyClient.println("active#" + playerThread.getThisPlayer().getName());
            }
        }
        outToMyClient.println("#endActive");
    }

    private void updateMoneyAfterEndGame(String player1, String player2, int bet) {
        playerService.updateMoney(player1, player2, bet);
//        String cmd = "SELECT * FROM player where Name='" + player1 + "'";
//        rs = st.executeQuery(cmd);
//
//        if (rs.next()) {
//            int gamePlayed = rs.getInt("GamesPlayed");
//            gamePlayed += 1;
//            int gameWon = rs.getInt("GamesWon");
//            gameWon += 1;
//            int m = rs.getInt("Money");
//            m += bet;
//            String cmd2 = "UPDATE player SET GamesPlayed='" + gamePlayed + "' WHERE Name = '" + player1 + "'";
//            st.executeUpdate(cmd2);
//            String cmd3 = "UPDATE player SET GamesWon='" + gameWon + "' WHERE Name = '" + player1 + "'";
//            st.executeUpdate(cmd3);
//            String cmd7 = "UPDATE player SET Money='" + m + "' WHERE Name = '" + player1 + "'";
//            st.executeUpdate(cmd7);
//        }
//
//        String cmd4 = "SELECT * FROM player where Name='" + player2 + "'";
//        rs = st.executeQuery(cmd4);
//        if (rs.next()) {
//            int gamePlayed = rs.getInt("GamesPlayed");
//            gamePlayed += 1;
//            int m = rs.getInt("Money");
//            m -= bet;
//            String cmd5 = "UPDATE player SET GamesPlayed='" + gamePlayed + "' WHERE Name = '" + player2 + "'";
//            st.executeUpdate(cmd5);
//            String cmd8 = "UPDATE player SET Money='" + m + "' WHERE Name = '" + player2 + "'";
//            st.executeUpdate(cmd8);
//        }
        thisPlayer.setPlaying(false);
        for (PlayerThread playerThread : playerThreads) {
            if (playerThread.getThisPlayer().getName().compareTo(player2) == 0) {
                playerThread.getThisPlayer().setPlaying(false);
            }
        }
    }

//    private void sendLeaderBoard() throws Exception {
//        outToMyClient.println("start#");
//        String cmd = "SELECT * FROM player";
//        rs = st.executeQuery(cmd);
//        while (rs.next()) {
//            String name = rs.getString("Name");
//            int gamePlayed = rs.getInt("GamesPlayed");
//            int gameWon = rs.getInt("GamesWon");
//            int money = rs.getInt("Money");
//            outToMyClient.println("leaderboard#" + name + "#" + gamePlayed + "#" + gameWon + "#" + money);
//
//        }
//        outToMyClient.println("#end");
//    }

//    private void showProfile(String username) throws Exception {
//        String cmd = "SELECT * FROM player";
//        rs = st.executeQuery(cmd);
//        while (rs.next()) {
//            String name = rs.getString("Name");
//            String id = rs.getString("FacebookID");
//            int gamePlayed = rs.getInt("GamesPlayed");
//            int gameWon = rs.getInt("GamesWon");
//            int money = rs.getInt("Money");
//            if (name.compareTo(username) == 0) {
//                outToMyClient.println("profile#" + id + "#" + gamePlayed + "#" + gameWon + "#" + money);
//                break;
//            }
//        }
//    }

    private void signIn(String username, String password) {
        for (PlayerThread playerThread : playerThreads) {
            if (playerThread.getThisPlayer().getName().compareTo(username) == 0 && playerThread.getThisPlayer().isLoggedIn()) {
                outToMyClient.println("login#false#onno");
                return;
            }
        }

        if (playerService.signIn(username, password)) {
            Optional<Player> playerFromDb = playerService.getByName(username);
            if (playerFromDb.isPresent()) {
                Player player = playerFromDb.get();
                thisPlayer = PlayerData.builder()
                        .name(player.getName())
                        .pass(player.getPassword())
                        .fbID("0")
                        .money(player.getBalance())
                        .loggedIn(true)
                        .build();
                outToMyClient.println("login#" + player.getName() + "#" + 0 + "#" + player.getBalance());
            }
        } else {
            outToMyClient.println("login#false");
        }

//        String cmd = "SELECT * FROM player";
//        boolean x = false;
//        rs = st.executeQuery(cmd);
//        while (rs.next()) {
//            String name = rs.getString("name");
//            String pass = rs.getString("password");
////            String id = rs.getString("FacebookID");
//            int money = rs.getInt("money");
//            if (name.compareTo(username) == 0 && pass.compareTo(password) == 0) {
//                outToMyClient.println("login#" + username + "#" + 0 + "#" + money);
//                thisPlayer = new PlayerData(name, pass, "0", money, true);
//                x = true;
//                break;
//            }
//        }
//        if (!x) {
//            outToMyClient.println("login#false");
//        }
    }

    private void signUp(String username, String password, String fbID) {
        Optional<Player> playerFromDb = playerService.getByName(username);
        if (playerFromDb.isEmpty()) {
            playerService.signUp(username, password);
            outToMyClient.println("signup#true");
        } else {
            outToMyClient.println("signup#false");
        }
//        boolean x = true;
//        String cmd = "SELECT * FROM player";
//        rs = st.executeQuery(cmd);
//        while (rs.next()) {
//            String name = rs.getString("Name");
//            if (name.compareTo(username) == 0) {
//                outToMyClient.println("signup#false");
//                x = false;
//                break;
//            }
//        }
//        if (x) {
//            outToMyClient.println("signup#true");
//            String cmd1 = "INSERT INTO player (Name,Password,FacebookID,GamesPlayed,GamesWon,Money) VALUES ('" + username + "','" + password + "','" + fbID + "', '" + 0 + "','" + 0 + "','" + 100 + "')";
//            st.executeUpdate(cmd1);
//        }
    }

    public void addThread(PlayerThread pt) {
        playerThreads.add(pt);
    }
}
