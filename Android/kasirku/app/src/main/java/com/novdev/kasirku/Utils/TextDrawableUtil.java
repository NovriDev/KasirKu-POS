package com.novdev.kasirku.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class TextDrawableUtil {
    public static Bitmap createCircleAvatar(Context context, String text, int color, int sizePx) {
        Bitmap bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paintCircle = new Paint();
        paintCircle.setColor(color);
        paintCircle.setAntiAlias(true);
        canvas.drawCircle(sizePx / 2, sizePx / 2, sizePx / 2, paintCircle);

        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(sizePx / 2);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);

        Rect bounds = new Rect();
        paintText.getTextBounds(text, 0, 1, bounds);

        canvas.drawText(
                text.substring(0, 1).toUpperCase(),
                sizePx / 2,
                sizePx / 2 + bounds.height() / 2,
                paintText
        );

        return bitmap;
    }
}

