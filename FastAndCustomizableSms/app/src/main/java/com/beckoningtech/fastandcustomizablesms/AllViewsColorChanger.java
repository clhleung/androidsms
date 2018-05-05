package com.beckoningtech.fastandcustomizablesms;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Class to change all views in a ViewGroup to a specific color.
 *
 * Created by wyjun on 11/27/2017.
 */

public class AllViewsColorChanger {

    /**
     * Sets the font color of all text and hints in EditViews and TextViews in the layout
     * specified to the color specified.
     * @param parentLayout  Layout containing the views of which to change color
     * @param color         Font Color to change to
     */
    public static void changeAllTextViewAndEditTextHintAndFontColorInLayout(ViewGroup parentLayout,
                                                                            int color){
        changeAllEditTextColorInLayout(parentLayout, color);
        changeAllEditTextHintsColorInLayout(parentLayout, color);
        changeAllTextViewColorInLayout(parentLayout, color);
        changeAllTextViewHintsColorInLayout(parentLayout, color);
    }

    /**
     * Sets the font color of TextViews in the layout specified to the color specified.
     * @param parentLayout   Layout containing the views of which to change color
     * @param color          Font Color to change to
     */
    public static void changeAllTextViewColorInLayout(ViewGroup parentLayout, int color){
        for (int count=0; count < parentLayout.getChildCount(); count++){
            View view = parentLayout.getChildAt(count);
            if(view instanceof TextView){
                ((TextView)view).setTextColor(color);
            } else if(view instanceof ViewGroup){
                changeAllTextViewColorInLayout((ViewGroup)view, color);
            }
        }
    }

    /**
     * Sets the font color of all EditTexts in the layout specified to the color specified.
     * @param parentLayout Layout containing the views of which to change color
     * @param color Font Color to change to
     */
    public static void changeAllEditTextColorInLayout(ViewGroup parentLayout, int color){
        for (int count=0; count < parentLayout.getChildCount(); count++){
            View view = parentLayout.getChildAt(count);
            if(view instanceof TextView){
                ((TextView)view).setTextColor(color);
            } else if(view instanceof ViewGroup){
                changeAllEditTextColorInLayout((ViewGroup)view, color);
            }
        }
    }

    /**
     * Sets the font color of the hints in all TextViews in the layout specified to the color
     * specified.
     * @param parentLayout Layout containing the views of which to change color
     * @param color Font Color to change to
     */
    public static void changeAllTextViewHintsColorInLayout(ViewGroup parentLayout, int color){
        for (int count=0; count < parentLayout.getChildCount(); count++){
            View view = parentLayout.getChildAt(count);
            if(view instanceof TextView){
                ((TextView)view).setHintTextColor(color);
            } else if(view instanceof ViewGroup){
                changeAllTextViewHintsColorInLayout((ViewGroup)view, color);
            }
        }
    }

    /**
     * Sets the font color of the hint in all EditTexts in the layout specified to the color
     * specified.
     * @param parentLayout Layout containing the views of which to change color
     * @param color Font Color to change to
     */
    public static void changeAllEditTextHintsColorInLayout(ViewGroup parentLayout, int color){
        for (int count=0; count < parentLayout.getChildCount(); count++){
            View view = parentLayout.getChildAt(count);
            if(view instanceof TextView){
                ((TextView)view).setHintTextColor(color);
            } else if(view instanceof ViewGroup){
                changeAllEditTextHintsColorInLayout((ViewGroup)view, color);
            }
        }
    }
}
