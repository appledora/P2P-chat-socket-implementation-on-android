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

public class fileServer extends Thread {

    Context context;
    private String TAG = "FILE SERVER";
    private ListView messageList;
    private ArrayList<Message> messageArray;
    private ChatAdapter mAdapter;
    private int port;

    fileServer(Context context, ChatAdapter mAdapter, ListView messageList, ArrayList<Message> messageArray, int port) {
        this.messageArray = messageArray;
        this.messageList = messageList;
        this.mAdapter = mAdapter;
        this.port = port;
        this.context = context;
    }

    public void run() {
        try {
            ServerSocket fileSocket = new ServerSocket(port + 1);
            Log.d(TAG, "run: " + fileSocket.getLocalPort());
            fileSocket.setReuseAddress(true);
            System.out.println(TAG + "started");
            while (true) {
                Socket connectFileSocket = fileSocket.accept();
                Log.d(TAG, "run: File Opened");
                fileFromClient handleFile = new fileFromClient();
                handleFile.execute(connectFileSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class fileFromClient extends AsyncTask<Socket, Void, String> {
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
