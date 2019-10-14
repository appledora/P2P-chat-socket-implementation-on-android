package com.tgc.researchchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class chatServer extends Thread {

    private String TAG = "CHATSERVER";

    private ListView messageList;
    private ArrayList<Message> messageArray;
    private ChatAdapter mAdapter;
    Context context;
    private int port;

    chatServer(Context context, ChatAdapter mAdapter, ListView messageList, ArrayList<Message> messageArray, int port) {
        this.messageArray = messageArray;
        this.messageList = messageList;
        this.mAdapter = mAdapter;
        this.port = port;
        this.context = context;
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
                messageArray.add(new Message(result, 1));
                messageList.setAdapter(mAdapter);
            } else {
                try {
                    Log.i(TAG, "else cause");
                    File file = new File(context.getObbDir(), "testfile.txt");
                    Log.i(TAG, "FIle dir => " + file);
                    FileWriter writer = new FileWriter(file);
                    writer.append(result);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
