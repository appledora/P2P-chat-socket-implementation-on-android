package com.tgc.researchchat;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class chatServer extends Thread {

    String TAG = "CHATSERVER";

    ListView messageList;
    ArrayList<Message> messageArray;
    ChatAdapter mAdapter;

    int port;

    public chatServer(ChatAdapter mAdapter, ListView messageList, ArrayList<Message> messageArray, int port) {
        this.messageArray = messageArray;
        this.messageList = messageList;
        this.mAdapter = mAdapter;
        this.port = port;
    }

    ServerSocket initSocket = null;

    public void run() {
        try {
            String text;
            initSocket = new ServerSocket(port);
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
            messageArray.add(new Message( result, 1));
            messageList.setAdapter(mAdapter);

        }
    }
}
