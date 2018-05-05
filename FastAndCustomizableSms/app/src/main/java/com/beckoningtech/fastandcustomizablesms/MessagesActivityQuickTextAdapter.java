package com.beckoningtech.fastandcustomizablesms;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.support.v4.app.FragmentManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for the horizontal GridView associated with quick texting on the message screen.
 * <p>
 * Created by wyjun on 9/11/2017.
 */

public class MessagesActivityQuickTextAdapter extends
        RecyclerView.Adapter<MessagesActivityQuickTextAdapter.ViewHolder> {
    private ArrayList<QuickTextItem> quickTexts;

    private String thread_id;
    private static Context mContext;
    private static Context context;

    private static AdapterCallback callback;
    private ThreadInfoContainer threadInfoContainer;


    /**
     * Provide a reference to the views for each data item. In this case, each item is associated
     * with a text view and an image view.
     */
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private TextView mTextView;
        private ImageView mImageView;

        // Could probably make the below static.
        private static ArrayList<QuickTextItem> quickTexts;
        private static ThreadInfoContainer threadInfoContainer;
        private static FragmentManager fragmentManager;
        private static QuickTextContainer quickTextContainer;

        // Creates a ViewHolder and sets the instance's quickTexts to the global quickTexts.
        private ViewHolder(View v) {
            super(v);

            context = v.getContext();
            mTextView = (TextView) v.findViewById(R.id.messages_activity_quicktext_view_text);
            mImageView = (ImageView)
                    itemView.findViewById(R.id.messages_activity_quicktext_view_image);
            mTextView.setOnClickListener(this);
            mImageView.setOnClickListener(this);
            mTextView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            Log.d("CLICKED:", "stub to see if shit was fucking clicked or not," + position);

            if (position < quickTexts.size()) {
                Log.d("CLICKED:", position + ";" +
                        quickTexts.get(position).getQuickTextTag());
                QuickTextItem quickTextItem = quickTexts.get(position);
                callback.sendSMSOrMMS(quickTextItem.getMessageBody(),
                        quickTextItem.isSendOnPress(), quickTextItem.isAppendLocation());
            } else {
                callback.startQuickTextThreadEditActivity(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Confirm");
            builder.setMessage("Remove quickText?");
            boolean yes;
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    quickTextContainer.remove(getAdapterPosition());
                    Log.d("ADAPTER:", "onLongClick");
                    callback.updateQuickTexts();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
    }


    /**
     * Constructor for MessagesActivityQuickTextAdapter
     *
     * @param myDataset
     * @param context
     */
    public MessagesActivityQuickTextAdapter(ArrayList<QuickTextItem> myDataset,
                                            AdapterCallback context,
                                            ThreadInfoContainer threadInfoContainer,
                                            FragmentManager fragmentManager,
                                            QuickTextContainer quickTextContainer) {
        quickTexts = myDataset;
        callback = context;
        this.threadInfoContainer = threadInfoContainer;
        ViewHolder.quickTexts = quickTexts;
        ViewHolder.quickTextContainer = quickTextContainer;
        ViewHolder.threadInfoContainer = threadInfoContainer;
        ViewHolder.fragmentManager = fragmentManager;

    }

    /**
     * Create new views (invoked by the layout manager)
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MessagesActivityQuickTextAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_activity_quicktext_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Replace the contents of a view (invoked by the layout manager). In this case, we decide
     * if we display an image or text.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MessagesActivityQuickTextAdapter.ViewHolder holder, int position) {
        if (position < quickTexts.size()) {
            // - get element from your dataset at this position
            QuickTextItem quickText = quickTexts.get(position);
            // - replace the contents of the view with that element
            if (quickText.isShowImage()) {
                holder.mImageView.setImageBitmap(quickText.getBitmap());
                holder.mTextView.setVisibility(View.GONE);
            } else {
                holder.mTextView.setText(quickText.getQuickTextTag());
                holder.mImageView.setVisibility(View.GONE);
            }
        } else {
            holder.mTextView.setText("Add Action");
            holder.mImageView.setVisibility(View.GONE);
        }
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     *
     * @return number of items
     */
    @Override
    public int getItemCount() {
        if (quickTexts == null) {
            return 0;
        }
        return quickTexts.size() + 1;
    }

    /**
     * Sets a callback.
     *
     * @param callback
     */
    public void setCallback(AdapterCallback callback) {
        MessagesActivityQuickTextAdapter.callback = callback;
    }


    /**
     * Adds a quick text item to the dataset.
     * @param quickTextItem QuickTextItem to be added.
     */
    public void add(QuickTextItem quickTextItem){
        quickTexts.add(quickTextItem);
    }

    /**
     * Removes the QuickTextItem at the specified position.
     * @param position Position of the QuickTextItem to be removed.
     */
    public void remove(int position){
        quickTexts.remove(position);
    }


    /**
     * Interface for sending quick texts.
     */
    public interface AdapterCallback {
        void sendSMSOrMMS(String message, boolean thread, boolean location);

        void startQuickTextThreadEditActivity(int quickTextNumber);

        void updateQuickTexts();

        String getThreadId();
        /**
         * Callback interface for functions needed for quick texting.
         */
    /*
    public interface AdapterCallback {
        void sendSMSOrMMS(String phoneNumber, String message, String thread);
        void startQuickTextThreadEditActivity(int quickTextNumber);
*/
    }

}
