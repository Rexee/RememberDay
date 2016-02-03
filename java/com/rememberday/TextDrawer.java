package com.rememberday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.rememberday.DB.event;

import java.util.Calendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TextDrawer {

    public static final int TEXT_COLOR_CLOCK  = 0xFFFFFF;
    public static final int TEXT_COLOR_LABELS = 0xD0D0D0;

    public static final  int TEXT_SIZE_CLOCK       = 70;
    private static final int TEXT_SIZE_DAY_OF_WEEK = 18;

    public static final int mPrimaryColor   = TEXT_COLOR_CLOCK;
    public static final int mSecondaryColor = TEXT_COLOR_LABELS;

    public static void drawClock(Calendar calendar, textProcessor drawer) {

        String hourStr = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        hourStr = hourStr.substring(hourStr.length() - 2);
        String minStr = "0" + calendar.get(Calendar.MINUTE);
        minStr = minStr.substring(minStr.length() - 2);

        String timeText = hourStr + ":" + minStr;

        drawer.drawTextPrimary(timeText, TEXT_SIZE_CLOCK, R.id.imageView_Clock);

        drawDayOfWeek(calendar, drawer);
        drawDayOfYear(calendar, drawer);
    }

    private static void drawDayOfWeek(Calendar calendar, textProcessor drawer) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        String timeText = sdf.format(calendar.getTime());
        drawer.drawTextSecondary(timeText.substring(0, 1).toUpperCase() + timeText.substring(1), TEXT_SIZE_DAY_OF_WEEK, R.id.imageView_Day_Of_Week);
   }

    private static void drawDayOfYear(Calendar calendar, textProcessor drawer) {
      SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
        String timeText = "" + calendar.get(Calendar.DAY_OF_MONTH) + " " + sdf.format(calendar.getTime());
        drawer.drawTextSecondary(timeText, TEXT_SIZE_DAY_OF_WEEK, R.id.imageView_Day_Of_Year);
    }

    public static void drawEvent(Calendar calendar, textProcessor drawer, Context context) {

        DB eventsDB = new DB(context);
        eventsDB.open();

        event event = eventsDB.getMainEventOnDay(calendar);
        eventsDB.close();

        if (event.name.isEmpty()) {
            drawer.mViews.setViewVisibility(R.id.layout_Event, GONE);
            return;
        }

        drawer.mViews.setViewVisibility(R.id.layout_Event, VISIBLE);
        drawer.drawTextEvent(event, TEXT_SIZE_DAY_OF_WEEK, R.id.imageView_Event);

    }

    public static class textProcessor {
        private final int mSecondaryOffset = 5;
        private final int mPrimaryOffset   = 10;

        Paint          mDrawer;
        RemoteViews    mViews;
        DisplayMetrics mDisplayMetrics;

        Typeface mPrimaryFont;
        Typeface mSecondaryFont;

        public textProcessor(Context context, RemoteViews views) {

            mViews = views;
            mDisplayMetrics = context.getResources().getDisplayMetrics();

            mPrimaryFont = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
            mSecondaryFont = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");

            mDrawer = new Paint();
            mDrawer.setTypeface(mPrimaryFont);
            mDrawer.setAntiAlias(true);

        }

        void drawTextEvent(event textToDraw, int fontSizeSP, int imgRes) {

            int textColor = mSecondaryColor;

            if (textToDraw.color != 0) textColor = textToDraw.color;

            drawText(textToDraw.name, fontSizeSP, imgRes, false, textColor);
        }

        void drawTextSecondary(String textToDraw, int fontSizeSP, int imgRes) {
            drawText(textToDraw, fontSizeSP, imgRes, false, mSecondaryColor);
        }

        void drawTextPrimary(String textToDraw, int fontSizeSP, int imgRes) {
            drawText(textToDraw, fontSizeSP, imgRes, true, mPrimaryColor);
        }

        void drawText(String textToDraw, int fontSizeSP, int imgRes, boolean isPrimary, int color) {

            if (textToDraw.isEmpty()) return;
            mDrawer.setColor(0xFF000000 | color);

            int curOffset;
            if (isPrimary) {
                curOffset = mPrimaryOffset;
            } else {
                mDrawer.setTypeface(mSecondaryFont);
                curOffset = mSecondaryOffset;
            }

            int fontSizePX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, fontSizeSP, mDisplayMetrics);
            mDrawer.setTextSize(fontSizePX);

            int paddings = (fontSizePX / curOffset);
            int bitmapWidth = (int) (mDrawer.measureText(textToDraw));
            int bitmapHeight = fontSizePX;

            mDrawer.setTextSize(fontSizePX);

            Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawText(textToDraw, 0, fontSizePX - paddings, mDrawer);

            mViews.setImageViewBitmap(imgRes, bitmap);
        }

    }

}
