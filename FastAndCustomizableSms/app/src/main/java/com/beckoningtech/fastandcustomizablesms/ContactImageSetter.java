package com.beckoningtech.fastandcustomizablesms;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Class to set an ImageView with the correct Contact photo.
 *
 * Created by wyjun on 11/15/2017.
 */

public class ContactImageSetter {

    /**
     * Sets the ImageView to the ContactContainer's image if available. If not, it sets it to a
     * circle with the contact's first name if available. If this is also not avaiable, it sets
     * the image to a default image.
     * @param imageView          ImageView we want to set.
     * @param contactContainer   ContactContainer to pull the image data out of.
     * @param resources          Resources to pull drawables.
     */
    public static void setContactImage(ImageView imageView, ContactContainer contactContainer,
                                       Resources resources){
        LetterTileDrawable letterTileDrawable = new LetterTileDrawable(resources);
        if (contactContainer!=null){
            if(contactContainer.isContact()) {
                Bitmap bitmap = contactContainer.getCircularContactImage();
                if (bitmap == null) {
                    letterTileDrawable.setLetterAndColorFromContactDetails(
                            contactContainer.getContactName(), contactContainer.getContactLookupKey());
                    letterTileDrawable.setColor(contactContainer.getColor());
                    letterTileDrawable.setContactType(letterTileDrawable.TYPE_DEFAULT);
                    letterTileDrawable.setIsCircular(true);
                    imageView.setImageDrawable(letterTileDrawable);
                } else {
                    imageView.setImageBitmap(bitmap);
                }
            } else {
                letterTileDrawable.setContactType(letterTileDrawable.TYPE_DEFAULT);
                letterTileDrawable.setIsCircular(true);
                letterTileDrawable.setColor(contactContainer.getColor());
                imageView.setImageDrawable(letterTileDrawable);

            }
        } else {
            letterTileDrawable.setContactType(letterTileDrawable.TYPE_DEFAULT);
            letterTileDrawable.setIsCircular(true);
            imageView.setImageDrawable(letterTileDrawable);
        }

    }
}
