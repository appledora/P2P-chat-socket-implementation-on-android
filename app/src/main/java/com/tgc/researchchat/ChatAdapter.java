package com.tgc.researchchat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter {

    public ChatAdapter(Activity context, ArrayList<Message> arr) {
        super(context, 0, arr);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View listItemView = converView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.messagelist, parent, false);
        }
        Message currentMessage = (Message) getItem(position);
        String message = currentMessage.getMessage();
        if (currentMessage.isSent() && currentMessage.getMessage()!="") {
            TextView sent = listItemView.findViewById(R.id.list_sent);
            sent.setText(message);
            sent.setVisibility(View.VISIBLE);
        } else if(!currentMessage.isSent() && currentMessage.getMessage()!=""){
            TextView received = listItemView.findViewById(R.id.list_received);
            received.setText(message);
            received.setVisibility(View.VISIBLE);
        }
        return listItemView;
    }
}
