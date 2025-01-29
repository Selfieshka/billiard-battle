package ru.kpfu.itis.kirillakhmetov.billiardbattle.server;

import lombok.Getter;
import lombok.Setter;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Player;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity.PlayerData;
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
                    else if (str.get(0).equals("login2")) {
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
                    else if (str.get(0).equals("login")) {
                        System.out.println(str);
                        signIn(str.get(1), str.get(2));
                    }
                    // Регистрация
                    else if (str.get(0).equals("signup")) {
                        signUp(str.get(1), str.get(2), str.get(3));
                    }
                    // Отправка активных игроков
                    else if (str.get(0).equals("active")) {
                        sendActivePlayers(str.get(1));
                    }
                    // Проверяем, может ли выбранный игрок сыграть на n-ое количество денег
                    else if (str.get(0).equals("canPlay")) {
                        sendOtherPlayer(str.get(1), Integer.parseInt(str.get(2)));
                    }
                    // Отмена приглашения в игру
                    else if (str.get(0).equals("reject")) {
                        for (PlayerThread playerThread : playerThreads) {
                            if (playerThread.getThisPlayer().getName().compareTo(str.get(1)) == 0) {
                                playerThread.getOutToMyClient().println(sentence);
                            }
                        }
                    }
                    // Устанавливаем соединения между двумя игроками
                    else if (str.get(0).equals("play")) {
                        setupGameSession(str.get(1), Integer.parseInt(str.get(2)));
                    }
                    // Выход из аккаунта
                    else if (str.get(0).equals("logout")) {
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
        thisPlayer.setPlaying(false);
        for (PlayerThread playerThread : playerThreads) {
            if (playerThread.getThisPlayer().getName().compareTo(player2) == 0) {
                playerThread.getThisPlayer().setPlaying(false);
            }
        }
    }

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
    }

    private void signUp(String username, String password, String fbID) {
        Optional<Player> playerFromDb = playerService.getByName(username);
        if (playerFromDb.isEmpty()) {
            playerService.signUp(username, password);
            outToMyClient.println("signup#true");
        } else {
            outToMyClient.println("signup#false");
        }
    }

    public void addThread(PlayerThread pt) {
        playerThreads.add(pt);
    }
}
