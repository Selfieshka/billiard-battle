package ru.kpfu.itis.kirillakhmetov.billiardbattle.server.util;

import lombok.Getter;
import lombok.Setter;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Player;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolMessageCreator;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.entity.PlayerData;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.service.PlayerService;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol.ProtocolProperties.*;


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
                    System.out.println("FROM CLIENT: " + request);

                    List<String> requestParts = Arrays.stream(request.split(DELIMITER))
                            .filter(val -> !val.isEmpty())
                            .toList();

                    switch (requestParts.getFirst()) {
                        case AUTH_LOGIN:
                            signIn(requestParts.get(1), requestParts.get(2));
                            break;
                        case GAME_INIT:
                            if (turn) {
                                request = ProtocolMessageCreator.create(request, LOGIC_TRUE);
                                turn = false;
                            } else {
                                request = ProtocolMessageCreator.create(request, LOGIC_FALSE);
                                turn = true;
                            }
                            outToClient.println(request);
                            break;
                        case AUTH_REGISTER:
                            signUp(requestParts.get(1), requestParts.get(2), requestParts.get(3));
                            break;
                        case ACTIVE_PLAYER_LIST:
                            sendActivePlayers(requestParts.get(1));
                            break;
                        case REQUEST_CHALLENGE:
                            sendOtherPlayer(requestParts.get(1), Integer.parseInt(requestParts.get(2)));
                            break;
                        case CANCEL_INVITE:
                            for (PlayerThread playerThread : playerThreads) {
                                if (playerThread.getThisPlayer().getName().equals(requestParts.get(1))) {
                                    playerThread.getOutToMyClient().println(request);
                                }
                            }
                            break;
                        case GAME_START:
                            setupGameSession(requestParts.get(1), Integer.parseInt(requestParts.get(2)));
                            break;
                        case GAME_END:
                            updateMoneyAfterEndGame(requestParts.get(1), requestParts.get(2), Integer.parseInt(requestParts.get(3)));
                            break;
                        case LOGOUT:
                            thisPlayer.setLoggedIn(false);
                            break;
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
                outToMyClient.println(ProtocolMessageCreator.create(
                        GAME_INIT, opponent, playerThread.getThisPlayer().getFbID(), bet, LOGIC_TRUE));
                outToClient.println(ProtocolMessageCreator.create(
                        GAME_INIT, thisPlayer.getName(), thisPlayer.getFbID(), bet, LOGIC_FALSE));
                break;
            }
        }
    }

    private void sendOtherPlayer(String name, int money) {
        Optional<Player> playerFromDb = playerService.getByName(name);
        if (playerFromDb.isPresent()) {
            Player player = playerFromDb.get();
            if (player.getBalance() < money) {
                outToMyClient.println(ProtocolMessageCreator.create(REQUEST_CHALLENGE, LOGIC_FALSE, money));
            } else {
                for (PlayerThread playerThread : playerThreads) {
                    if (playerThread.getThisPlayer().getName().equals(name) && playerThread.getThisPlayer().isLoggedIn()) {
                        playerThread.getOutToMyClient().println(ProtocolMessageCreator.create(REQUEST_CHALLENGE, thisPlayer.getName(), money));
                        break;
                    }
                }
            }
        } else {
            outToMyClient.println(ProtocolMessageCreator.create(REQUEST_CHALLENGE, LOGIC_FALSE, money));
        }
    }

    private void sendActivePlayers(String username) {
        outToMyClient.println("startActive#");
        for (PlayerThread playerThread : playerThreads) {
            if (playerThread.getThisPlayer().isLoggedIn() && !playerThread.getThisPlayer().getName().equals(username) && !playerThread.getThisPlayer().isPlaying()) {
                outToMyClient.println(ProtocolMessageCreator.create(ACTIVE_PLAYER_LIST, playerThread.getThisPlayer().getName()));
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
                outToMyClient.println(ProtocolMessageCreator.create(AUTH_LOGIN, LOGIC_FALSE));
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
                outToMyClient.println(ProtocolMessageCreator.create(
                        AUTH_LOGIN, player.getUsername(), 0, player.getBalance()));
            }
        } else {
            outToMyClient.println(ProtocolMessageCreator.create(AUTH_LOGIN, LOGIC_FALSE));
        }
    }

    private void signUp(String username, String password, String fbID) {
        Optional<Player> playerFromDb = playerService.getByName(username);
        if (playerFromDb.isEmpty()) {
            playerService.signUp(username, password);
            outToMyClient.println(ProtocolMessageCreator.create(AUTH_REGISTER, LOGIC_TRUE));
        } else {
            outToMyClient.println(ProtocolMessageCreator.create(AUTH_REGISTER, LOGIC_TRUE));
        }
    }

    public void addThread(PlayerThread pt) {
        playerThreads.add(pt);
    }
}
