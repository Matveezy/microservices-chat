package com.lab2.message.util;

import com.lab2.message.exception.RequestAttributeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import static com.lab2.message.util.RequestAttributeNames.USER_ID_ATTRIBUTE_NAME;

@Component
public class RequestAttributeUtils {

    public static Long extractUserId(HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getHeader(USER_ID_ATTRIBUTE_NAME);
        if (userId == null) throw new RequestAttributeException("userId attribute doesn't exist in request!");
        return Long.valueOf(userId);
    }
}
