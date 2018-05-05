package com.beckoningtech.fastandcustomizablesms;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Class to return a circular bitmap.
 *
 * Created by wyjun on 11/16/2017.
 */

public class BitmapModifier {

    /**
     * Method to return a circular version of a given bitmap.
     * @param bitmap  Bitmap to be turned into a circle.
     * @return a circular version of the bitmap added
     */
    public static Bitmap getCircularBitmap(Bitmap bitmap){
        if (bitmap == null){
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }
}
