package com.bxcloud.com.bxcloud.utils;

import com.bxcloud.com.bxcloud.constants.Constants;

import java.util.UUID;

public class TokenUtils {

    public static String getMemberToken() {
        return Constants.TOKEN_MEMBER + "-" + UUID.randomUUID();
    }
}
