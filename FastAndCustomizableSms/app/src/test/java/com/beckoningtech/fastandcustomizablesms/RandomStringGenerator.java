package com.beckoningtech.fastandcustomizablesms;

import java.util.Random;

/**
 * Created by root on 11/22/17.
 */

public class RandomStringGenerator {

    static protected String getSaltedString(int string_length) {
        String salted_characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        while (salt.length() < string_length) {
            int index = (int) (random.nextFloat() * salted_characters.length());
            salt.append(salted_characters.charAt(index));
        }
        return salt.toString();
    }

    static protected String getRandomPhoneNumber() {
        String salted_characters = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        while (salt.length() < 10) {
            int index = (int) (random.nextFloat() * salted_characters.length());
            salt.append(salted_characters.charAt(index));
        }
        return "+1" + salt.toString();
    }

    static protected String getRandomNumber(int length) {
        String salted_characters = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        while (salt.length() < length) {
            int index = (int) (random.nextFloat() * salted_characters.length());
            salt.append(salted_characters.charAt(index));
        }
        return salt.toString();
    }

}
