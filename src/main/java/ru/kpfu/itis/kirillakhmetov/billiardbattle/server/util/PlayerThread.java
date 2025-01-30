package ru.kpfu.itis.kirillakhmetov.billiardbattle.server.util;

import lombok.Getter;
import lombok.Setter;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Player;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity.PlayerData;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.service.PlayerService;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


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
    private static final List<PlayerThread> playerThreads = new CopyOnWriteArrayList<>();
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
                String request = inFromClient.readLine();
                if (request != null) {
                    System.out.println("FROM CLIENT:" + request);

                    List<String> requestParts = Arrays.stream(request.split("#"))
                            .filter(val -> !val.isEmpty())
                            .toList();

                    switch (requestParts.getFirst()) {
                        // Вход
                        case "login":
                            signIn(requestParts.get(1), requestParts.get(2));
                            break;
                        // Отправка первому игроку информации о втором игроке и старт игры
                        case "login2":
                            if (turn) {
                                request += "#true";
                                turn = false;
                            } else {
                                request += "#false";
                                turn = true;
                            }
                            outToClient.println(request);
                            break;
                        // Регистрация
                        case "signup":
                            signUp(requestParts.get(1), requestParts.get(2), requestParts.get(3));
                            break;
                        // Отправка активных игроков
                        case "active":
                            sendActivePlayers(requestParts.get(1));
                            break;
                        // Проверяем, может ли выбранный игрок сыграть на n-ое количество денег
                        case "canPlay":
                            sendOtherPlayer(requestParts.get(1), Integer.parseInt(requestParts.get(2)));
                            break;
                        // Отмена приглашения в игру
                        case "reject":
                            for (PlayerThread playerThread : playerThreads) {
                                if (playerThread.getThisPlayer().getName().equals(requestParts.get(1))) {
                                    playerThread.getOutToMyClient().println(request);
                                }
                            }
                            break;
                        // Устанавливаем соединения между двумя игроками
                        case "play":
                            setupGameSession(requestParts.get(1), Integer.parseInt(requestParts.get(2)));
                            break;
                        // Окончание игры
                        case "W":
                            updateMoneyAfterEndGame(requestParts.get(1), requestParts.get(2), Integer.parseInt(requestParts.get(3)));
                            break;
                        // Выход из аккаунта
                        case "logout":
                            thisPlayer.setLoggedIn(false);
                            break;
                        // Некорректный запрос отправляем обратно
                        default:
                            outToClient.println(request);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setupGameSession(String opponent, int bet) {
        for (PlayerThread playerThread : playerThreads) {
            if (playerThread.getThisPlayer().getName().equals(opponent) && playerThread.getThisPlayer().isLoggedIn()) {
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
                    if (playerThread.getThisPlayer().getName().equals(name) && playerThread.getThisPlayer().isLoggedIn()) {
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
            if (playerThread.getThisPlayer().isLoggedIn() && !playerThread.getThisPlayer().getName().equals(username) && !playerThread.getThisPlayer().isPlaying()) {
                outToMyClient.println("active#" + playerThread.getThisPlayer().getName());
            }
        }
        outToMyClient.println("#endActive");
    }

    private void updateMoneyAfterEndGame(String player1, String player2, int bet) {
        playerService.updateMoney(player1, player2, bet);
        thisPlayer.setPlaying(false);
        for (PlayerThread playerThread : playerThreads) {
            if (playerThread.getThisPlayer().getName().equals(player2)) {
                playerThread.getThisPlayer().setPlaying(false);
            }
        }
    }

    private void signIn(String username, String password) {
        for (PlayerThread playerThread : playerThreads) {
            if (playerThread.getThisPlayer().getName().equals(username) && playerThread.getThisPlayer().isLoggedIn()) {
                outToMyClient.println("login#false#onno");
                return;
            }
        }

        if (playerService.signIn(username, password)) {
            Optional<Player> playerFromDb = playerService.getByName(username);
            if (playerFromDb.isPresent()) {
                Player player = playerFromDb.get();
                thisPlayer = PlayerData.builder()
                        .name(player.getUsername())
                        .pass(player.getPassword())
                        .fbID("0")
                        .money(player.getBalance())
                        .loggedIn(true)
                        .build();
                outToMyClient.println("login#" + player.getUsername() + "#" + 0 + "#" + player.getBalance());
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
