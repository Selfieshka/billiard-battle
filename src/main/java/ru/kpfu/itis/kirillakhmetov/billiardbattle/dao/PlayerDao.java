package ru.kpfu.itis.kirillakhmetov.billiardbattle.dao;

import ru.kpfu.itis.kirillakhmetov.billiardbattle.entity.Player;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.util.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PlayerDao {
    //language=sql
    private static final String SQL_FIND_BY_NAME = """
            SELECT player_id, name, password, money
            FROM player
            WHERE name = ?
            """;

    //language=sql
    private static final String SQL_SAVE = """
            INSERT INTO player (name, password)
            VALUES (?, ?)
            """;

    public Optional<Player> findByName(String name) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_NAME)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(Player.builder()
                        .name(name)
                        .password(resultSet.getString("password"))
                        .balance(resultSet.getInt("money"))
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void save(Player player) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SAVE)) {
            statement.setString(1, player.getName());
            statement.setString(2, player.getPassword());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
