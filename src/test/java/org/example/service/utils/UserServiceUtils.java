package org.example.service.utils;

import org.example.domain.model.Role;
import org.example.domain.model.User;

import java.util.ArrayList;

public class UserServiceUtils {

    public static final Long USER_1_ID = 1L;

    public static final Long USER_2_ID = 2L;

    public static final Long USER_3_ID = 3L;

    public static final User USER_1 = getUser(USER_1_ID);

    public static final User USER_2 = getUser(USER_2_ID);

    public static final User USER_3 = getUser(USER_3_ID);

    public static User getUser(Long id) {
        return User.builder().username("user" + id).password("1234").email("user" + id + "@mail.com")
                .role(Role.ROLE_USER).chats(new ArrayList<>()).messageDeliveries(new ArrayList<>()).build();
    }
}
