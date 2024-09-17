package com.atm.core.utils.strings_generators;

import org.springframework.stereotype.Component;

import java.util.Random;
@Component
public class SlugGenerator {

    public String slug(String input) {
        if (input.contains(".") || input.contains(" "))
            return input.replaceAll("[ .]", "-");
        else
            return input + "-" + randomString(3);
    }

    public String randomString(int length) {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(symbols.length());
            sb.append(symbols.charAt(index));
        }
        return sb.toString();
    }
}
