package com.beckoningtech.fastandcustomizablesms;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Adapter for Messages in MessagesRecyclerView
 *
 * Created by wyjun on 11/21/2017.
 */

public class MessagesActivityMessagesRecyclerAdapter extends
        RecyclerView.Adapter<MessagesActivityMessagesRecyclerAdapter.ViewHolder> {

    Context mContext;
    private Resources resources;
    private ContactContainer contactContainer;
    private DisplayMetrics displayMetrics;
    private List<MessageContainer> messageContainers = new ArrayList<>();
    // If a message is deleted, we must decrement the key to all elements in this array.
    private SparseArray<List<ImageView>> imageViewSparseArray = new SparseArray<>();

    /**
     * ViewHolder for this adapter. Contains a TextView, an ImageView, a LinearLayout and a
     * RelativeLayout.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView messageTextView;
        ImageView contactImageView;
        LinearLayout wrapperLinearLayout;
        RelativeLayout wrapperRelativeLayout;
		LinearLayout wrapperVerticalLinearLayout;
        List<ImageView> imageViews;
        int origPosition;

        // Creates a ViewHolder and sets the instance's quickTexts to the global quickTexts.
        ViewHolder(View v) {
            super(v);
            messageTextView = (TextView) v.findViewById(R.id.messages_activity_message_text_view);
            contactImageView = (ImageView)
                    itemView.findViewById(R.id.messages_activity_contact_image_view);
            wrapperLinearLayout = (LinearLayout) v.findViewById(R.id.messages_activity_wrapper2);
            wrapperVerticalLinearLayout = (LinearLayout) v.findViewById(R.id.messages_activity_wrapper3);
            wrapperRelativeLayout = (RelativeLayout) v.findViewById(R.id.messages_activity_wrapper);
            imageViews = new LinkedList<>();
//            messageTextView.setOnClickListener(null);
        }
        public LinkedList<ImageView> getImageViews() {
            return (LinkedList<ImageView>)imageViews;
        }

        /**
         * To display time when a message is clicked. Not currently implemented.
         * @param v
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }

    }

    /**
     * Constructor for MessagesActivityMessagesRecyclerAdapter
     * @param messageContainers
     * @param context
     * @param contactContainer
     * @param displayMetrics
     */
    public MessagesActivityMessagesRecyclerAdapter(ArrayList<MessageContainer> messageContainers,
                                                   Context context,
                                                   ContactContainer contactContainer,
                                                   DisplayMetrics displayMetrics){
        this.messageContainers = messageContainers;
        this.mContext = context;
        this.contactContainer = contactContainer;
        this.displayMetrics = displayMetrics;
        this.resources = context.getResources();
    }

    @Override
    public MessagesActivityMessagesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                 int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.messages_activity_message_view, parent, false);
        return new ViewHolder(row);
    }

//    @Override
//    public void onViewRecycled(MessagesActivityMessagesRecyclerAdapter.ViewHolder holder) {
//        // Log.d("MAMRA", "test");
//        LinearLayout wrapperVerticalLinearLayout = holder.wrapperVerticalLinearLayout;
////        for(ImageView imageViewToRemove : imageViews){
////            // Log.d("MAMRA", "removing image!");
////            imageViewToRemove.setVisibility(View.GONE);
////            wrapperVerticalLinearLayout.removeView(imageViewToRemove);
//////                imageViews.remove(imageViewToRemove);
////        }
//    }

    /**
     * Organizes the view in such a way the message is correctly formatted.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MessagesActivityMessagesRecyclerAdapter.ViewHolder holder,
                                 int position) {
        // Log.d("MAMRA", "test");
        // Obtain references to the layouts and MessageContainers
        RelativeLayout wrapperRelativeLayout = holder.wrapperRelativeLayout;
        LinearLayout wrapperLinearLayout = holder.wrapperLinearLayout;
        LinearLayout wrapperVerticalLinearLayout = holder.wrapperVerticalLinearLayout;
        MessageContainer blob = messageContainers.get(position);
        ImageView imageView = holder.contactImageView;
        if(holder.getImageViews()!=null){
            // Log.d("MAMRA", "attempting to enter for loop!");
            for(ImageView imageViewToRemove : holder.getImageViews()){
                // Log.d("MAMRA", "removing image!");
                imageViewToRemove.setVisibility(View.GONE);
                wrapperVerticalLinearLayout.removeView(imageViewToRemove);
            }
            holder.getImageViews().clear();
        } else {
            // Log.d("MAMRA", "imageViews == null, position: " + position);
        }
        int nextPosition = position + 1;
        boolean nextLeft = false;
        if (nextPosition < messageContainers.size()) {
            nextLeft = (messageContainers.get(nextPosition)).left;
        }
        int paddingDp;
        int paddingDpStart = 15;
        int paddingDpTop = (position==0)? 20 : 0;
        float density = resources.getDisplayMetrics().density;
        if (nextLeft != blob.left) {
            int paddingPixels = 7;
            paddingDp = (int)(paddingPixels * density);
        } else {
            float paddingPixels = .2f;
            paddingDp = (int)(paddingPixels * density);
        }

        // Add content based on whether it's an MMS or SMS
        TextView messageTextView = (TextView)
                wrapperRelativeLayout.findViewById(R.id.messages_activity_message_text_view);
        // If there is no text as part of the message hide the text view
        if (blob.text.equals("")) {
            messageTextView.setVisibility(View.GONE);
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(blob.text);
            messageTextView.setTextColor(ColorsUtil.sTileFontColor);
        }
        int width = (int) (displayMetrics.widthPixels * .7);

        // Handle mms views;
        if(blob instanceof MMSContainer) {
            // Add all the relevant views in:
            int index = 0;
            if(((MMSContainer) blob).getContainsMedia()) {
                    for(Object curObject :((MMSContainer)blob).getMedia()) {
                    // Still images
                        if(curObject instanceof Bitmap) {
                            ImageView curView = new ImageView(mContext);
                            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                                    ((Bitmap)curObject).getWidth(), ((Bitmap)curObject).getHeight()
                            );
                            curView.setLayoutParams(layoutParams);
                            curView.setImageBitmap((Bitmap) curObject);
                            //((ImageView)curView).setFocusable(true);
                            wrapperVerticalLinearLayout.addView(curView,index++);
                            holder.getImageViews().add(curView);
                        }
                    }
            }
        }

        // Setup the current message's relative layout depending on left or right side
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(blob.left) {
            layoutParams.addRule(RelativeLayout.RIGHT_OF,
                    R.id.messages_activity_contact_image_view);
            // TODO: Actually figure out how to to center the ImageView with the last line of
            // the TextView. Currently using a hack that seems to be working.
            imageView.setPaddingRelative(paddingDpStart,0,0,paddingDp+10);
            if(!nextLeft) {
                ContactImageSetter.setContactImage(imageView, contactContainer, resources);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.INVISIBLE);
            }

        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            imageView.setVisibility(View.GONE);
        }
        wrapperLinearLayout.setLayoutParams(layoutParams);
        wrapperLinearLayout.setGravity(blob.left? Gravity.LEFT:Gravity.RIGHT);
        // Setup the chat bubbles
        messageTextView.setBackgroundResource(R.drawable.bubble_white);
        if(blob.left) {
            wrapperLinearLayout.setPaddingRelative(paddingDpStart,paddingDpTop,0,paddingDp);
            if(contactContainer!= null) {
                messageTextView.getBackground().setColorFilter(contactContainer.getColor(),
                        PorterDuff.Mode.MULTIPLY);
            } else {
                messageTextView.setBackgroundResource(R.drawable.bubble_green);
            }
        } else {
            wrapperLinearLayout.setPaddingRelative(0,paddingDpTop,paddingDpStart,paddingDp);
            messageTextView.getBackground().setColorFilter(ColorsUtil.darkGreyTextBubbleColor,
                    PorterDuff.Mode.SRC_ATOP);

        }
    }

    /**
     * Counts the number of messages in this current thread
     *
     * @return number of messages
     */
    @Override
    public int getItemCount() {
        return messageContainers.size();
    }

    /**
     * Adds an item to the adapter's dataset
     */
    public void add(MessageContainer object){
        messageContainers.add(object);
    }
}
