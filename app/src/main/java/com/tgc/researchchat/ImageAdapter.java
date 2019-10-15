package com.tgc.researchchat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter {

    ImageAdapter(Activity context, ArrayList<MyFiles> arr) {
        super(context, 0, arr);
    }


    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View listItemView = converView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.messagelist, parent, false);
        }
        MyFiles currentFile = (MyFiles) getItem(position);

        assert currentFile != null;
        ImageView sentImage = listItemView.findViewById(R.id.image_sent);
        ImageView receivedImage = listItemView.findViewById(R.id.image_received);


        System.out.println(currentFile.isSent());
        String filePath = currentFile.getFilePath();
        System.out.println("current file path =>" + filePath);


        if (currentFile.isSent()) {
            File imgFile = new File(filePath);
            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                sentImage.setImageBitmap(myBitmap);
                sentImage.setVisibility(View.VISIBLE);

            }
        } else {
            File imgFile = new File(filePath);
            System.out.println("filePath while received => " + filePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                receivedImage.setImageBitmap(myBitmap);
                receivedImage.setVisibility(View.VISIBLE);
            }
        }

        return listItemView;
    }
}
