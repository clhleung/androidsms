package com.beckoningtech.fastandcustomizablesms;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

/**
 * Class to get default colors and store modified colors.
 *
 * Created by wyjun on 12/1/2017.
 */

public class ColorsUtil {
    /**
     * Black font color.
     */
    public static int blackFontColor;
    /**
     * White font color.
     */
    public static int sTileFontColor;
    /**
     * White color.
     */
    public static int opaqueWhiteColor;
    /**
     * Dark grey color.
     */
    public static int darkGreyTextBubbleColor;
    /** Color Information */
    static TypedArray sColors = null;
    static TypedArray darkColors;
    static int sDefaultColor;
    static int fontColor;
    static int backgroundColor;
    static int primaryColor;
    static int darkPrimaryColor;

    /**
     * @return backgroundColor
     */
    public static int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the background color to a given color.
     * @param backgroundColor Color to set the background to.
     */
    public static void setBackgroundColor(int backgroundColor) {
        ColorsUtil.backgroundColor = backgroundColor;
    }

    /**
     * Get the dark primary color.
     * @return darkPrimaryColor
     */
    public static int getDarkPrimaryColor() {
        return darkPrimaryColor;
    }

    /**
     * Sets the dark primary color.
     * @param darkPrimaryColor Color to set the dark primary color to.
     */
    public static void setDarkPrimaryColor(int darkPrimaryColor) {
        ColorsUtil.darkPrimaryColor = darkPrimaryColor;
    }

    /**
     * Gets the primary color.
     * @return primaryColor
     */
    public static int getPrimaryColor() {
        return primaryColor;
    }

    /**
     * Sets the primary color
     * @param primaryColor color to set the primary color to.
     */
    public static void setPrimaryColor(int primaryColor) {
        ColorsUtil.primaryColor = primaryColor;
    }

    /**
     * Gets the font color
     * @return fontColor
     */
    public static int getFontColor() {
        return fontColor;
    }

    /**
     * Sets the font color
     * @param fontColor Color to set the font to
     */
    public static void setFontColor(int fontColor) {
        ColorsUtil.fontColor = fontColor;
    }

    /**
     * Returns a deterministic color based on the provided contact identifier string.
     * @param identifier  String that we has to get a color.
     * @return color
     */
    public static int pickColor(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return sDefaultColor;
        }
        // String.hashCode() implementation is not supposed to change across java versions, so
        // this should guarantee the same email address always maps to the same color.
        // The email should already have been normalized by the ContactRequest.
        final int color = Math.abs(identifier.hashCode()) % sColors.length();
        return sColors.getColor(color, sDefaultColor);
    }

    /**
     * Returns a deterministic color based on the provided contact identifier string.
     * @param identifier  String that we hash to get a color.
     * @return darkColor
     */
    public static int pickDarkColor(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return sDefaultColor;
        }
        // String.hashCode() implementation is not supposed to change across java versions, so
        // this should guarantee the same email address always maps to the same color.
        // The email should already have been normalized by the ContactRequest.
        final int color = Math.abs(identifier.hashCode()) % darkColors.length();
        return darkColors.getColor(color, sDefaultColor);
    }

    /**
     * Private function to setup the default colors.
     */
    public static void setupColors(Context context){
        if (ColorsUtil.sColors == null) {
            Resources resources = context.getResources();
            ColorsUtil.sColors = resources.obtainTypedArray(R.array.letter_tile_colors);
            ColorsUtil.darkColors = resources.obtainTypedArray(R.array.letter_tile_colors_dark);
            ColorsUtil.backgroundColor = ContextCompat.getColor(
                    context, R.color.grey_background_color);
            ColorsUtil.darkGreyTextBubbleColor = ContextCompat.getColor(
                    context, R.color.dark_grey_text_bubble_color);
            ColorsUtil.blackFontColor = ContextCompat.getColor(
                    context, R.color.letter_black_font_color);
            ColorsUtil.sDefaultColor = ContextCompat.getColor(
                    context, R.color.letter_tile_default_color);
            ColorsUtil.sTileFontColor = ContextCompat.getColor(
                    context, R.color.letter_tile_font_color);
            ColorsUtil.opaqueWhiteColor = ContextCompat.getColor(
                    context, R.color.opaque_white_color);
            ColorsUtil.primaryColor = ContextCompat.getColor(
                    context, R.color.colorPrimary);
            ColorsUtil.darkPrimaryColor = ContextCompat.getColor(
                    context, R.color.colorPrimaryDark);
        }
        ColorsUtil.fontColor = ColorsUtil.sTileFontColor;
    }

}
