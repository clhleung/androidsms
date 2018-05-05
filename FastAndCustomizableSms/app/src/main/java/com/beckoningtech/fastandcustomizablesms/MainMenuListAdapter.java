package com.beckoningtech.fastandcustomizablesms;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * List view for MainMenuMessageInfo in MainMenuActivity.
 *
 * Created by wyjun on 9/11/2017.
 */

// This code will need to be refactored to support group messages as I have used
// ContactContainers to sort views

public class MainMenuListAdapter extends ArrayAdapter<MainMenuMessageInfo> {
    private ArrayList<MainMenuMessageInfo> mDataset;
    private Context mContext;
    private Resources resources;

    private static class ViewHolder {
        TextView vMessage;
        TextView vNumber;
        TextView vDate;
        ImageView vImage;
        ImageView vPreview;
    }


    public MainMenuListAdapter(ArrayList<MainMenuMessageInfo> data, Context context) {
        super(context, R.layout.main_menu_activity_message_info_view, data);
        this.mDataset = data;
        this.mContext=context;
        this.resources = context.getResources();

    }
    public void updateDataset(ArrayList<MainMenuMessageInfo> data) {
        this.mDataset.clear();
        this.mDataset.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mDataset==null) {
            return 0;
        }
        return mDataset.size();
    }


    // Called by the ListView instance for every view
    @Override
    public MainMenuMessageInfo getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int lastPosition = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MainMenuMessageInfo mainMenuMessageInfo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;
        final int fPosition = position;
        final MainMenuMessageInfo mainMenuMessageInfoFinal = mainMenuMessageInfo;

        // Setup our viewHolder to point to the various aspects of the convertView
        // so we can adjust the values of the convertView from the viewHolder
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.main_menu_activity_message_info_view, parent, false);
            viewHolder.vMessage = (TextView) convertView.findViewById(R.id.main_menu_activity_message_info_contact_text);
            viewHolder.vNumber = (TextView) convertView.findViewById(R.id.main_menu_activity_message_info_contact_name);
            viewHolder.vImage = (ImageView) convertView.findViewById(R.id.main_menu_activity_message_info_contact_image);
            viewHolder.vDate = (TextView) convertView.findViewById(R.id.main_menu_activity_message_info_contact_date);
			viewHolder.vPreview = (ImageView) convertView.findViewById(R.id.main_menu_activity_message_info_mms_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // setup the viewholder with relevant information
        viewHolder.vNumber.setTextColor(ColorsUtil.sTileFontColor);
        viewHolder.vMessage.setTextColor(ColorsUtil.sTileFontColor);
        viewHolder.vDate.setTextColor(ColorsUtil.sTileFontColor);

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position;

        MMSContainer mmsContainer = mainMenuMessageInfo.getMMSContainer();

        // Add text and date
        if(mmsContainer!=null) {
            viewHolder.vMessage.setText(mmsContainer.text);

        } else {
            viewHolder.vMessage.setText(mainMenuMessageInfo.getLastMessage());
            viewHolder.vDate.setText(mainMenuMessageInfo.getRelativeTime());
        }

        // Add a preview image if needed for an mms
        Bitmap previewBitmap = null;
        if(mmsContainer!=null) {
            previewBitmap = mmsContainer.getPreview();
            if (previewBitmap!=null) {
                viewHolder.vPreview.setImageBitmap(previewBitmap);
            }
        }
        if(mmsContainer == null || previewBitmap==null) {
            viewHolder.vPreview.setVisibility(View.GONE);
        } else {
            viewHolder.vPreview.setVisibility(View.VISIBLE);
        }

        // Setup the contact image
        ContactContainer contactContainer = mainMenuMessageInfo.getContactContainer();
        ContactImageSetter.setContactImage(viewHolder.vImage, contactContainer, resources);
        //        LetterTileDrawable letterTileDrawable = new LetterTileDrawable(resources);
        //        letterTileDrawable.setIsCircular(true);
        //        letterTileDrawable.setContactType(letterTileDrawable.TYPE_DEFAULT);

        // Show the numbers
        if(mmsContainer!=null){
            viewHolder.vNumber.setText(mmsContainer.getSendersDisplayed());
        } else {
            if (contactContainer == null) {
                viewHolder.vNumber.setText(mainMenuMessageInfo.getNumber());
                //            viewHolder.vImage.setImageDrawable(letterTileDrawable);
            } else {
                viewHolder.vNumber.setText(contactContainer.getContactName());
                //            Bitmap contactBm = contactContainer.getContactImage();
                //            if(contactBm != null) {
                //                viewHolder.vImage.setImageBitmap(contactContainer.getCircularContactImage());
                //            } else {
                //                letterTileDrawable.setLetterAndColorFromContactDetails(
                //                        contactContainer.getContactName(), contactContainer.getContactLookupKey());
                //                viewHolder.vImage.setImageDrawable(letterTileDrawable);
                //            }
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }

}
