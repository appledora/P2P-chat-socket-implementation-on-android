package com.tgc.researchchat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter {

    ChatAdapter(Activity context, ArrayList<Message> arr) {
        super(context, 0, arr);
    }



    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View listItemView = converView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.messagelist, parent, false);
        }
       Message currentMessage = (Message) getItem(position);

        assert currentMessage != null;

        TextView sent = listItemView.findViewById(R.id.list_sent);
        TextView received = listItemView.findViewById(R.id.list_received);
        ImageView sentImage = listItemView.findViewById(R.id.image_sent);
        ImageView receivedImage = listItemView.findViewById(R.id.image_received);
        sent.setText("");
        sent.setVisibility(View.GONE);
        received.setText("");
        received.setVisibility(View.GONE);

       System.out.println(currentMessage.getMessage());
        System.out.println(currentMessage.isSent());

        String message = currentMessage.getMessage();

       if (currentMessage.isSent()) {
            sent.setText(message);
            sent.setVisibility(View.VISIBLE);
        } else {
            received.setText(message);
            received.setVisibility(View.VISIBLE);
        }

        return listItemView;
    }
}
