package com.smarthome.client2.util;


import java.util.Random;


public class AspectUtils {
    private static final int RC4_BASE = 256;

    public static String rc4(String input, String key) {
        int l = key.length();
        int[] is = new int[RC4_BASE];
        byte[] bs = new byte[RC4_BASE];
        for (int i = 0; i < RC4_BASE; ++i) {
            is[i] = i;
            bs[i] = (byte) key.charAt(i % l);
        }
        for (int i = 0, j = 0; i < RC4_BASE; ++i) {
            j = (j + is[i] + bs[i]) % RC4_BASE;
            int tmp = is[i];
            is[i] = is[j];
            is[j] = tmp;
        }
        char[] inputs = input.toCharArray();
        char[] output = new char[inputs.length];
        for (int i = 0, j = 0, k = 0; k < inputs.length; ++k) {
            i = (i + 1) % RC4_BASE;
            j = (j + is[i]) % RC4_BASE;
            int tmp = is[i];
            is[i] = is[j];
            is[j] = tmp;
            int m = (is[i] + (is[j] % RC4_BASE)) % RC4_BASE;
            output[k] = (char) (inputs[k] ^ ((char) is[m]));
        }
        return new String(output);
    }

    private static final char[] HEX_DIGITS = {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f'};

    public static String generatePSK(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; ++i) {
            sb.append(HEX_DIGITS[random.nextInt(HEX_DIGITS.length)]);
        }
        return sb.toString();
    }
}
