package com.example.technology_forum.model;

import java.util.Random;

public class VerifyCode {
    Random random = new Random();
    private StringBuilder code = new StringBuilder();
    public String getCode() {
        String[] rance = new String[] {
                "0","1", "2", "3", "4", "5", "6", "7", "8", "9",
                "a","b", "d", "c", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z"};
        for (int i = 0; i < 6 ; i++) {
            int r = random.nextInt(36);
            code.append(rance[r]);
        }
        return code.toString();
    }
}
