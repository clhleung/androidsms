package com.beckoningtech.fastandcustomizablesms;

import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for the recycler view in NewConversationActivity.
 *
 * Created by wyjun on 11/28/2017.
 */

public class NewConversationActivityContactAdapter extends
        RecyclerView.Adapter<NewConversationActivityContactAdapter.ViewHolder> {

    private ArrayList<ContactContainer> contactContainers;
    private Resources resources;
    private int fontColor;
    private NewConversationActivityAdapterInterface activityAdapterInterface;

    public NewConversationActivityContactAdapter(ArrayList<ContactContainer> contactContainers,
                                                 Resources resources, int fontColor,
                                                 NewConversationActivityAdapterInterface
                                                         activityAdapterInterface){
        this.contactContainers = contactContainers;
        ViewHolder.contactContainers = contactContainers;
        this.resources = resources;
        this.fontColor = fontColor;
        this.activityAdapterInterface = activityAdapterInterface;
        ViewHolder.activityAdapterInterface = activityAdapterInterface;
    }

    /**
     * Provide a reference to the views for each data item. In this case, each item is associated
     * with a text view and an image view.
     */
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView phoneNumberTextView;
        private TextView numberTypeTextView;
        private TextView contactNameTextView;
        private ImageView contactImageView;
        private RelativeLayout wrapperRelativeLayout;
        private LinearLayout textWrapperLinearLayout;

        private static NewConversationActivityAdapterInterface activityAdapterInterface;
        private static ArrayList<ContactContainer> contactContainers;

        // Creates a ViewHolder and sets the instance's quickTexts to the global quickTexts.
        private ViewHolder(View v) {
            super(v);
            phoneNumberTextView = (TextView)
                    v.findViewById(R.id.new_conversation_activity_phone_number);
            numberTypeTextView = (TextView)
                    v.findViewById(R.id.new_conversation_activity_number_type);
            contactNameTextView = (TextView)
                    v.findViewById(R.id.new_conversation_contact_name);
            contactImageView = (ImageView)
                    itemView.findViewById(R.id.new_conversation_activity_contact_image);
            wrapperRelativeLayout = (RelativeLayout)
                    v.findViewById(R.id.new_conversation_activity_contact_wrapper);
            wrapperRelativeLayout.setOnClickListener(this);
            textWrapperLinearLayout = (LinearLayout)
                    v.findViewById(R.id.new_conversation_activity_contact_text_holder);
        }

        @Override
        public void onClick(View v) {
            ContactContainer contactContainer = contactContainers.get(getAdapterPosition());
            activityAdapterInterface.addNumberToSearchBar(contactContainer);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.new_conversation_activity_contact_view, parent, false);
        return new NewConversationActivityContactAdapter.ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView phoneNumberTextView = holder.phoneNumberTextView;
        TextView numberTypeTextView = holder.numberTypeTextView;
        TextView contactNameTextView = holder.contactNameTextView;
        ImageView contactImageView = holder.contactImageView;
        RelativeLayout wrapperRelativeLayout = holder.wrapperRelativeLayout;
        LinearLayout textWrapperLinearLayout = holder.textWrapperLinearLayout;
        ContactContainer contactContainer = contactContainers.get(position);

        AllViewsColorChanger.changeAllTextViewAndEditTextHintAndFontColorInLayout(
                wrapperRelativeLayout, fontColor);

        phoneNumberTextView.setText(contactContainer.getContactNumber());
        phoneNumberTextView.setVisibility(View.VISIBLE);
        contactNameTextView.setText(contactContainer.getContactName());
        contactNameTextView.setVisibility(View.VISIBLE);
        ContactImageSetter.setContactImage(contactImageView, contactContainer, resources);
        numberTypeTextView.setText(contactContainer.getContactNumberType());

    }

    @Override
    public int getItemCount() {
        return contactContainers.size();
    }
}
