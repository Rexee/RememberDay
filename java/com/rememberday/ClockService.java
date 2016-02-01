package com.rememberday;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class ClockService extends Service {

    public static final String ACTION_DATE_FORMAT_CHANGED = "rememberday.DATE_FORMAT_CHANGED";

    public ClockService() {
    }

    private DateFormatObserver mDateFormatObserver;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramAnonymousContext, Intent intent) {
            if (intent == null) {
                return;
            }
            String str = intent.getAction();
            L.L("BroadcastReceiver.onReceive action = " + str);

            if (Intent.ACTION_TIME_TICK.equals(str) ||
                    Intent.ACTION_TIME_CHANGED.equals(str) ||
                    Intent.ACTION_CONFIGURATION_CHANGED.equals(str) ||
                    Intent.ACTION_TIMEZONE_CHANGED.equals(str) ||
                    Intent.ACTION_DATE_CHANGED.equals(str) ||
                    Intent.ACTION_SCREEN_ON.equals(str) ||
                    ACTION_DATE_FORMAT_CHANGED.equals(str))

                MainWidget.updateClock(paramAnonymousContext, AppWidgetManager.getInstance(paramAnonymousContext));
        }
    };

    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(ACTION_DATE_FORMAT_CHANGED);

        registerReceiver(this.mReceiver, intentFilter);

        this.mDateFormatObserver = new DateFormatObserver();
        Uri uri = android.provider.Settings.System.getUriFor(android.provider.Settings.System.DATE_FORMAT);
        getContentResolver().registerContentObserver(uri, true, this.mDateFormatObserver);

        Context localContext = getApplicationContext();
        MainWidget.updateClock(localContext, AppWidgetManager.getInstance(localContext));
    }

    public void onDestroy() {
        unregisterReceiver(this.mReceiver);
        getContentResolver().unregisterContentObserver(this.mDateFormatObserver);
        if (MainWidget.isWidgetRunning()) {
            L.L("onDestroy : The widget is alive. service will be reStarted!!!");
            startService(new Intent(this, ClockService.class));
            return;
        }
        L.L("onDestroy : End");
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        return START_STICKY;
    }

    private class DateFormatObserver extends ContentObserver {
        public DateFormatObserver() {
            super(new Handler());
        }

        public void onChange(boolean paramBoolean) {
            super.onChange(paramBoolean);
            ClockService.this.sendBroadcast(new Intent(ACTION_DATE_FORMAT_CHANGED));
        }
    }
}
