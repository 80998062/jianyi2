package com.sinyuk.jianyi.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.widgets.TextDrawable;

/**
 * Created by Sinyuk on 16/9/12.
 */
public class AvatarHelper {
    public static TextDrawable createTextDrawable(String text, int color) {
        if (TextUtils.isEmpty(text)) {
            text = " ";
        }
        return TextDrawable.builder().buildRound(text, color);
    }

    public static TextDrawable createTextDrawable(String text, Context context) {
        if (TextUtils.isEmpty(text)) {
            text = " ";
        }
        return TextDrawable.builder().buildRound(text, ContextCompat.getColor(context, R.color.colorAccent));
    }
}
