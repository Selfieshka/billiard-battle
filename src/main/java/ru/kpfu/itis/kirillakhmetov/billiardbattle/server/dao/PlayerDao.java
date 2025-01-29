package ru.kpfu.itis.kirillakhmetov.billiardbattle.server.dao;

import ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity.Player;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.util.ConnectionProvider;

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

    //language=sql
    private static final String SQL_UPDATE_MONEY = """
            UPDATE player
            SET money = money + ?
            WHERE name = ?;
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

    public void updateMoneyByName(String username1, String username2, int money) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statementForPlusMoney = connection.prepareStatement(SQL_UPDATE_MONEY);
             PreparedStatement statementForSubtractingMoney = connection.prepareStatement(SQL_UPDATE_MONEY)) {
            try {
                connection.setAutoCommit(false);

                statementForPlusMoney.setInt(1, money);
                statementForPlusMoney.setString(2, username1);

                statementForSubtractingMoney.setInt(1, money * -1);
                statementForSubtractingMoney.setString(2, username2);

                statementForPlusMoney.execute();
                statementForSubtractingMoney.execute();

                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
