package com.beckoningtech.fastandcustomizablesms;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.EdgeEffect;

import java.lang.reflect.Field;

/**
 * Class to change the overscroll color of a scrollable view.
 * Created by wyjun on 11/17/2017.
 */

public class ScrollableOverscrollColorChange {
    /**
     * Sets the overscroll color.
     * @param scrollableView
     * @param color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setOverscrollEdgeColor(View scrollableView, int color) {
        final String[] edgeGlows = {"mLeftGlow", "mTopGlow", "mRightGlow", "mBottomGlow"};
        for (String edgeGlow : edgeGlows) {
            Class<?> aClass = scrollableView.getClass();
            while (aClass != null) {
                try {
                    final Field edgeGlowField = aClass.getDeclaredField(edgeGlow);
                    edgeGlowField.setAccessible(true);
                    final EdgeEffect edgeEffect = (EdgeEffect) edgeGlowField.get(scrollableView);
                    edgeEffect.setColor(color);
                    break;
                } catch (Exception e) {
                    aClass = aClass.getSuperclass();
                }
            }
        }
    }


}
