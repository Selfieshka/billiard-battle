package ru.kpfu.itis.kirillakhmetov.billiardbattle.server.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.kpfu.itis.kirillakhmetov.billiardbattle.server.exception.CreateConnectionDBException;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionProvider {
    private static final String DRIVER_KEY = "db.driver";
    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.user";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static BlockingQueue<Connection> pool;

    static {
        try {
            createConnectionPool();
        } catch (CreateConnectionDBException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createConnectionPool() throws CreateConnectionDBException {
        String poolSize = ConfigReader.getValue(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = createConnection();
            var proxyConnection = (Connection) Proxy.newProxyInstance(
                    ConnectionProvider.class.getClassLoader(),
                    new Class[]{Connection.class},
                    ((proxy, method, args) ->
                            method.getName().equals("close") ?
                                    pool.add((Connection) proxy) :
                                    method.invoke(connection, args))
            );
            pool.add(proxyConnection);
        }
    }

    private static Connection createConnection() throws CreateConnectionDBException {
        try {
            Class.forName(ConfigReader.getValue(DRIVER_KEY));
            return DriverManager.getConnection(
                    ConfigReader.getValue(URL_KEY),
                    ConfigReader.getValue(USER_KEY),
                    ConfigReader.getValue(PASSWORD_KEY)
            );
        } catch (SQLException | ClassNotFoundException e) {
            throw new CreateConnectionDBException(e);
        }
    }
}
