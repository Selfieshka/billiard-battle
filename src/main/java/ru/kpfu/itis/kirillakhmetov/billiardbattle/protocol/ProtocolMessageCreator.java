package ru.kpfu.itis.kirillakhmetov.billiardbattle.protocol;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProtocolMessageCreator {
    public static String create(Object... params) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            builder.append(params[i]);
            if (i < params.length - 1) {
                builder.append(ProtocolProperties.DELIMITER);
            }
        }
        return builder.toString();
    }
}
