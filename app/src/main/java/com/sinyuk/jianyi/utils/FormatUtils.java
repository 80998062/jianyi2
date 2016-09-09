package com.sinyuk.jianyi.utils;

import android.text.TextUtils;

/**
 * Created by Sinyuk on 16/8/7.
 */
public class FormatUtils {
    public static String shortenNumber(CharSequence sequence) {
        if (!TextUtils.isEmpty(sequence)) {
            int length = sequence.length();
            String suffix = "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                if (sequence.charAt(i) < '0' || sequence.charAt(i) > '9') {
                    throw new IllegalArgumentException("Must only have numbers");
                }
            }
            int invalidLength = 0;

            if (length >= 9) {
                invalidLength = 8;
                suffix = "bn";
            } else if (length >= 7) {
                invalidLength = 6;
                suffix = "m";
            } else if (length > 4) {
                invalidLength = 3;
                suffix = "k";
            }
            for (int i = 0; i < (length - invalidLength); i++) {
                sb.append(sequence.charAt(i));
            }
            if (invalidLength > 0) {
                sb.append('.');
                sb.append(sequence.charAt(length - invalidLength));
            }

            sb.append(suffix);
            return sb.toString();
        }
        return null;
    }
}
