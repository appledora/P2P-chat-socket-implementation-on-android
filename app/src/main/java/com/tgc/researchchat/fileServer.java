package com.tgc.researchchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
                File testDirectory = new File(context.getObbDir(), "recordFolder");
                if (!testDirectory.exists())
                    testDirectory.mkdirs();
                File outputFile = new File(testDirectory, "recording1");
                try {
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(sockets[0].getInputStream());
                    byte[] byteArray = new byte[8192 * 16];
                    int count;
                    while ((count = bufferedInputStream.read(byteArray, 0, byteArray.length)) != -1) {
                        outputStream.write(byteArray, 0, count);
                    }

                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return text;
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: Result" + result);
//            try {
//                Log.i(TAG, "else cause");
//                File file = new File(context.getObbDir(), "testfile.txt");
//                Log.i(TAG, "FIle dir => " + file);
//                FileWriter writer = new FileWriter(file);
//                writer.append(result);
//                writer.flush();
//                writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }

}
