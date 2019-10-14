package com.tgc.researchchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class chatServer extends Thread {

    private String TAG = "CHATSERVER";

    private ListView messageList;
    private ArrayList<Message> messageArray;
    private ChatAdapter mAdapter;
    Context context;
    private int port;
    String serverIpAddress;
    Activity activity;

    chatServer(Activity activity, Context context, ChatAdapter mAdapter, ListView messageList, ArrayList<Message> messageArray, int port, String serverIpAddress) {
        this.messageArray = messageArray;
        this.messageList = messageList;
        this.mAdapter = mAdapter;
        this.port = port;
        this.context = context;
        this.serverIpAddress = serverIpAddress;
        this.activity = activity;
    }

    public void run() {
        try {
            ServerSocket initSocket = new ServerSocket(port);
            initSocket.setReuseAddress(true);
            System.out.println(TAG + "started");
            while (true) {
                Socket connectSocket = initSocket.accept();
                ReadFromClient handle = new ReadFromClient();
                handle.execute(connectSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("StaticFieldLeak")
    public class ReadFromClient extends AsyncTask<Socket, Void, String> {
        String text;

        @Override
        protected String doInBackground(Socket... sockets) {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(sockets[0].getInputStream()));
                text = input.readLine();
                Log.i(TAG, "Received => " + text);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return text;
        }
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: Result" + result);
            if (result.charAt(0) == '1' && result.charAt(1) == ':') {
                StringBuilder stringBuilder = new StringBuilder(result);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                result = stringBuilder.toString();
                File path = context.getObbDir();
                Log.i(TAG,"FilesDir =>" + path+ "\n");
                String fileName =  new SimpleDateFormat("yyyyMMdd").format(new Date()) +"-" + serverIpAddress + ".txt";
                File file = new File(path,fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file,true);
                    String history = "server: " +result+"\n";
                    fos.write(history.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                messageArray.add(new Message(result, 1));
                messageList.setAdapter(mAdapter);
            } else {
                StringBuilder stringBuilder = new StringBuilder(result);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                result = stringBuilder.toString();
                ListView message_List;
                message_List = activity.findViewById(R.id.message_list);
                LayerDrawable layerDrawable = (LayerDrawable) message_List.getBackground();
                GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.shapeColor);
                gradientDrawable.setColor(Color.parseColor("#" + result));
            }
        }
    }
}
