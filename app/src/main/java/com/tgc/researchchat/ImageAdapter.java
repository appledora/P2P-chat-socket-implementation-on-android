package com.tgc.researchchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context context;
    private ArrayList<MyFiles> arrayList;

    ImageAdapter(Context context, ArrayList<MyFiles> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemViewType(int position) {
        MyFiles myFiles = arrayList.get(position);
        System.out.println("filePath in Adapter => " + myFiles.getFilePath());
        if (myFiles.isSent())
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.sent_image, parent, false);
            return new SentImageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(context).inflate(R.layout.received_image, parent, false);
            return new ReceivedImageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyFiles myFiles = arrayList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentImageHolder) holder).bind(myFiles);

                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedImageHolder) holder).bind(myFiles);

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        ImageView receivedImage;

        ReceivedImageHolder(View itemView) {
            super(itemView);
            receivedImage = itemView.findViewById(R.id.received_image_body);
            timeText = itemView.findViewById(R.id.image_time);
        }

        void bind(MyFiles myFiles) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String currentDateTimeString = sdf.format(myFiles.getTime());
            String directory = myFiles.getFilePath();
            System.out.println("Received Image holder: file directory => " + directory);
            File imgFile = new File(directory);
            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                receivedImage.setImageBitmap(myBitmap);
                receivedImage.setVisibility(View.VISIBLE);

            }
            timeText.setText(currentDateTimeString);
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        ImageView sentImage;

        SentImageHolder(View itemView) {
            super(itemView);
            sentImage = itemView.findViewById(R.id.sent_image_body);
            timeText = itemView.findViewById(R.id.image_time);
        }

        void bind(MyFiles myFiles) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String currentDateTimeString = sdf.format(myFiles.getTime());
            String directory = myFiles.getFilePath();
            System.out.println("Sent Image holder: file directory => " + directory);
            File imgFile = new File(directory);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                sentImage.setImageBitmap(myBitmap);
                sentImage.setVisibility(View.VISIBLE);
            }
            timeText.setText(currentDateTimeString);
        }
    }
}
