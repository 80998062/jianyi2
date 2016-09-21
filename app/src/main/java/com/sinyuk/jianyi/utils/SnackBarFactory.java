package com.sinyuk.jianyi.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.sinyuk.jianyi.R;

/**
 * Created by Sinyuk on 15.12.23.
 */
public class SnackBarFactory {


    public static Snackbar requestLogin(final Activity context, View view) {
        Snackbar snackbar = Snackbar.make(view, context.getString(R.string.plz_login),Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        snackbar.setActionTextColor(ContextCompat.getColor(context,R.color.colorAccent));
        snackbar.setAction("现在登录", v -> {});
        return snackbar;
    }


    public static Snackbar loginFailed(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        final TextView hintText = (TextView) layout.findViewById(R.id.snackbar_text);

        if (hintText != null) {
            hintText.setTextColor(ContextCompat.getColor(context,R.color.themeError));
        }

        layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
        snackbar.setActionTextColor(ContextCompat.getColor(context,R.color.text_secondary_dark));
        snackbar.setAction("帮助", v -> {});
        return snackbar;
    }

    public static Snackbar networkError(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        final TextView hintText = (TextView) layout.findViewById(R.id.snackbar_text);

        if (hintText != null) {
            hintText.setTextColor(ContextCompat.getColor(context,R.color.themeError));
        }

        layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
        snackbar.setActionTextColor(ContextCompat.getColor(context,R.color.text_secondary_dark));


        snackbar.setAction("检查网络连接", v -> {});
        return snackbar;
    }


    public static Snackbar errorNoAction(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        final TextView hintText = (TextView) layout.findViewById(R.id.snackbar_text);

        if (hintText != null) {
            hintText.setTextColor(ContextCompat.getColor(context,R.color.themeError));
        }

        layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white));

        return snackbar;
    }

    public static Snackbar succeedNoAction(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        final TextView hintText = (TextView) layout.findViewById(R.id.snackbar_text);

        if (hintText != null) {
            hintText.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
        }

        layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white));

        return snackbar;
    }


    public static Snackbar errorWithHelp(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
        snackbar.setActionTextColor(ContextCompat.getColor(context,R.color.colorAccent));
        snackbar.setAction("获取帮助", v -> {
        });
        return snackbar;
    }

}
