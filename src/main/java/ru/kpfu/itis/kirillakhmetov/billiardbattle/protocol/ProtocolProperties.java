package ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс содержит ключевые слова протокола, которые используются для взаимодействия между клиентом и сервером.
 * <p>Список ключевых слов:</p>
 * <ul>
 *     <li>{@link #DELIMITER} - Разделитель ключевых слов в протоколе.</li>
 *     <li>{@link #AUTH_LOGIN} - Команда для входа в систему.</li>
 *     <li>{@link #AUTH_REGISTER} - Команда для регистрации нового пользователя в системе.</li>
 *     <li>{@link #GAME_INIT} - Инициализация игры. Используется для начала игрового процесса.</li>
 *     <li>{@link #GAME_START} - Начало игровой сессии между игроками.</li>
 *     <li>{@link #ACTIVE_PLAYER_LIST} - Команда для передачи списка активных игроков.</li>
 *     <li>{@link #REQUEST_CHALLENGE} - Проверка, может ли выбранный игрок участвовать в игре.</li>
 *     <li>{@link #CANCEL_INVITE} - Отмена приглашения в игру.</li>
 *     <li>{@link #GAME_END} - Завершение игровой сессии.</li>
 *     <li>{@link #LOGOUT} - Выход пользователя из системы.</li>
 *     <li>{@link #SHOT_VELOCITY} - Передача данных о скорости движения шара при ударе.</li>
 *     <li>{@link #TECH_LOSE} - Фиксация технического поражения игрока.</li>
 *     <li>{@link #CUE_ROTATE} - Указание о повороте кия.</li>
 *     <li>{@link #PLAYER_HIT} - Команда, указывающая, что игрок совершил удар.</li>
 *     <li>{@link #BALL_MOVE} - Передача информации о движении шара на столе.</li>
 *     <li>{@link #LOGIC_TRUE} - Флаг положительного ответа (true).</li>
 *     <li>{@link #LOGIC_FALSE} - Флаг отрицательного ответа (false).</li>
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProtocolProperties {
    public static final String DELIMITER = "#";
    public static final String AUTH_LOGIN = "AUTH_LOGIN";
    public static final String AUTH_REGISTER = "AUTH_REGISTER";
    public static final String GAME_INIT = "GAME_INIT";
    public static final String GAME_START = "GAME_START";
    public static final String ACTIVE_PLAYER_LIST = "ACTIVE_PLAYER_LIST";
    public static final String REQUEST_CHALLENGE = "REQUEST_CHALLENGE";
    public static final String CANCEL_INVITE = "CANCEL_INVITE";
    public static final String GAME_END = "GAME_END";
    public static final String LOGOUT = "LOGOUT";
    public static final String SHOT_VELOCITY = "SHOT_VELOCITY";
    public static final String TECH_LOSE = "TECH_LOSE";
    public static final String CUE_ROTATE = "CUE_ROTATE";
    public static final String PLAYER_HIT = "PLAYER_HIT";
    public static final String BALL_MOVE = "BALL_MOVE";
    public static final String LOGIC_TRUE = "TRUE";
    public static final String LOGIC_FALSE = "FALSE";
}
