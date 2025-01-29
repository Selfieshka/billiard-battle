package ru.kpfu.itis.kirillakhmetov.billiardbattle.server.service;

import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Player;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.dao.PlayerDao;

import java.util.Optional;

public class PlayerService {
    public final PlayerDao playerDao = new PlayerDao();

    public boolean signIn(String name, String password) {
        Optional<Player> playerFromDb = playerDao.findByName(name);
        if (playerFromDb.isPresent()) {
            Player player = playerFromDb.get();
            return player.getPassword().equals(password);
        }
        return false;
    }

    public Optional<Player> getByName(String username) {
        return playerDao.findByName(username);
    }

    public void signUp(String username, String password) {
        playerDao.save(Player.builder()
                .name(username)
                .password(password)
                .build());
    }

    public void updateMoney(String username1, String username2, int money) {
        playerDao.updateMoneyByName(username1, username2, money);
    }
}
