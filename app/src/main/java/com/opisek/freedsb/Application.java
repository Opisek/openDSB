package com.opisek.freedsb;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.opisek.freedsb.account.Authenticator;
import com.opisek.freedsb.database.AppDatabase;
import com.opisek.freedsb.utils.AccountUtils;

import java.util.Date;

import androidx.appcompat.app.AppCompatDelegate;

import javax.annotation.Nullable;

public class Application extends android.app.Application {

    private static final String PREFERENCE_ACTIVE_LOGIN = "active_login";
    private static final String PREFERENCE_LAST_UPDATE = "last_update";

    public static final String PREFERENCE_APPEARANCE_THEME = "appearance_theme";

    public static final String PREFERENCE_CLASS_FILTERING = "class_filtering";
    public static final String PREFERENCE_CLASS_TO_FILTER = "class";

    private static Application instance;

    public boolean mIsInForeground = false;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(getApplicationContext());
    }

// --Commented out by Inspection START (02.05.19 17:02):
//    public void setNightMode(int mode) {
//        if (mode <= AppCompatDelegate.MODE_NIGHT_YES && mode >= ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ? AppCompatDelegate.MODE_NIGHT_AUTO : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM))
//        mNightMode = mode;
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
//        editor.putString(PREFERENCE_APPEARANCE_THEME, String.valueOf(mNightMode));
//        editor.apply();
//    }
// --Commented out by Inspection STOP (02.05.19 17:02)

    public int getNightMode() {
        /*String theme = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PREFERENCE_APPEARANCE_THEME, Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? "-1" : "0");
        /*if (theme != null) {
            return Integer.parseInt(String.valueOf(mNightMode));
        }
        return AppCompatDelegate.MODE_NIGHT_AUTO;*/
        return AppCompatDelegate.MODE_NIGHT_YES;
    }

    public boolean getClassFiltering() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PREFERENCE_CLASS_FILTERING, false);
    }

    public String getClassToFilter() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PREFERENCE_CLASS_TO_FILTER, "");
    }

    public void setClassFiltering(boolean filter, String toFilter) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putBoolean(PREFERENCE_CLASS_FILTERING, filter);
        editor.putString(PREFERENCE_CLASS_TO_FILTER, toFilter);
    }

    public void setActiveAccount(@Nullable Account account) {
        String name = null;
        if (account != null) {
            name = account.name;
        }
        setActiveAccountId(name);
    }

    public void setActiveAccountId(@Nullable String id) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        if (id == null || id.isEmpty()) {
            editor.remove(PREFERENCE_ACTIVE_LOGIN);
        } else {
            editor.putString(PREFERENCE_ACTIVE_LOGIN, id);
        }
        editor.apply();
    }

    public Account getActiveAccount() {
        String name = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PREFERENCE_ACTIVE_LOGIN, "");
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        Account account = AccountUtils.getAccountByName(accountManager, name);
        if (account != null) {
            return account;
        }
        Account[] accounts = accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return accounts[0];
        }
        return null;
    }

    @Deprecated
    public void setLastUpdate(Date date) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putLong(PREFERENCE_LAST_UPDATE, date.getTime());
        editor.apply();
    }

    @Deprecated
    public Date getLastUpdate() {
        long date = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong(PREFERENCE_LAST_UPDATE, 0);
        if (date == 0) {
            return null;
        }
        return new Date(date);
    }
}
