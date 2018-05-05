//package com.beckoningtech.fastandcustomizablesms;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
///**
// * Created by wyjun on 9/11/2017.
// */
//
//public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder>  {
//    private ContactContainer[] mDataset;
//
//    // Provide a reference to the views for each data item
//    // Complex data main_menu_action_bar_items may need more than one view per item, and
//    // you provide access to all the views for a data item in a view holder
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        // each data item is just a string in this case
//        public TextView mTextView;
//        public Button messageButton;
//        public ViewHolder(View v) {
//            super(v);
//            mTextView = (TextView) v.findViewById(R.id.contact_name);
//            messageButton = (Button) itemView.findViewById(R.id.message_button);
//        }
//    }
//
//    // Provide a suitable constructor (depends on the kind of dataset)
//    public MainMenuAdapter(ContactContainer[] myDataset) {
//        mDataset = myDataset;
//    }
//
//    // Create new views (invoked by the layout manager)
//    @Override
//    public MainMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
//                                                   int viewType) {
//        // create a new view
//        View v =  LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.contact_view, parent, false);
//        // set the view's size, margins, paddings and layout parameters
//        //...
//
//        ViewHolder vh = new ViewHolder(v);
//        return vh;
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(MainMenuAdapter.ViewHolder holder, int position) {
//        // - get element from your dataset at this position
//        ContactContainer contact = mDataset[position];
//        // - replace the contents of the view with that element
//        holder.mTextView.setText(contact.getContactName());
//        holder.messageButton.setText("TestButton");
//
//
//
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        if (mDataset==null) {
//            return 0;
//        }
//        return mDataset.length;
//    }
//
//}
