package com.tgc.researchchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class chatServer extends Thread {

    private String TAG = "CHATSERVER";

    private ListView messageList;
    private ArrayList<Message> messageArray;
    private ChatAdapter mAdapter;
    private Context context;
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
            messageArray.add(new Message(result, 1));
            messageList.setAdapter(mAdapter);
        }
    }




}
