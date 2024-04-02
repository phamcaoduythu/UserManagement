package com.example.usermanagement.utils;

import java.util.Random;
import java.util.regex.Pattern;

public class StringHandler {

    public static String randomStringGenerator(int length) {
        String capitalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String normalLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String values = capitalLetters + normalLetters +
                numbers;
        Random randomizer = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(values.charAt(randomizer.nextInt(values.length())));
        }
        return sb.toString();
    }

    public static boolean checkEmailRegrex(String email) {
        String emailPattern = "^[A-Za-z\\d+_.-]+@(?:(?:[A-Za-z\\d-]+\\.)?[A-Za-z]+\\.)?(gmail\\.com)$";
        Pattern emailCodePattern = Pattern.compile(emailPattern);
        return emailCodePattern.matcher(email).find();
    }

}
