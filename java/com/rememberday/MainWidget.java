package com.rememberday;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.widget.RemoteViews;

import java.util.Calendar;


public class MainWidget extends AppWidgetProvider {

    private static boolean mIsWidgetRunning         = false;
    public static  String  POPUP_LIST_EVENTS_OF_DAY = "POPUP_LIST_EVENTS";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        updateClock(context, appWidgetManager);

        if (!isServiceRunning(context)) {
            context.startService(new Intent(context, ClockService.class));
        }

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        // Enter relevant functionality for when the first widget is created

        mIsWidgetRunning = true;
        context.startService(new Intent(context, ClockService.class));

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        // Enter relevant functionality for when the last widget is disabled

        mIsWidgetRunning = false;
        context.stopService(new Intent(context, ClockService.class));

    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);

        if (ids == null || ids.length == 0) return;

        if (!isServiceRunning(context)) {
            context.startService(new Intent(context, ClockService.class));
        }
        if (intent.getAction().equals("com.sec.android.intent.action.HOME_RESUME")) {
            updateClock(context, appWidgetManager);
        }

        if (intent.getAction().equals(POPUP_LIST_EVENTS_OF_DAY)) {
            Intent popUpIntent = new Intent(context, EventsOfDayActivity.class);
            Rect rr = intent.getSourceBounds();
            popUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            popUpIntent.putExtra("x",rr.left);
            popUpIntent.putExtra("y",rr.top);
            context.startActivity(popUpIntent);
        }

    }

    public static void updateClock(Context context, AppWidgetManager appWidgetManager) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MainWidget.class.getName());
        updateViews(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisAppWidget));
    }

    private static void updateViews(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews localRemoteViews = new RemoteViews(context.getPackageName(), R.layout.clock_widget);

        Intent intent = new Intent(context, MainWidget.class);
        intent.setAction(POPUP_LIST_EVENTS_OF_DAY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        localRemoteViews.setOnClickPendingIntent(R.id.layout_Event, pendingIntent);

        Calendar calendar = Calendar.getInstance();
        TextDrawer.textProcessor drawer = new TextDrawer.textProcessor(context, localRemoteViews);

        TextDrawer.drawClock(calendar, drawer);
        TextDrawer.drawEvent(calendar, drawer, context);

        updateWidget(appWidgetManager, localRemoteViews, appWidgetIds);

    }

    static void updateWidget(AppWidgetManager appWidgetManager, RemoteViews remoteViews, int[] appWidgetIds) {

        if ((appWidgetManager != null) && (appWidgetIds != null)) {
            final int numIds = appWidgetIds.length;
            for (int i = 0; i < numIds; i++) {
                appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
            }
        }

    }

    public static boolean isWidgetRunning() {
        return mIsWidgetRunning;
    }

    private boolean isServiceRunning(Context context) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ClockService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }
}