package com.switchfully.eurder.services;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public static boolean isTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().contains("org.junit.")) {
                return true;
            }
        }
        return false;
    }
}
