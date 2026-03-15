package com.outfy.outfy_backend.common.util;

import java.util.UUID;

public class IdGenerator {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
    
    public static String generateShort() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}

