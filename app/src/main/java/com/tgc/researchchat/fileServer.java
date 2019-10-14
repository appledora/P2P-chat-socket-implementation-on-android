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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class fileServer extends Thread {

    Context context;
    private String TAG = "FILE SERVER";
    private ListView messageList;
    private ArrayList<Message> messageArray;
    private ChatAdapter mAdapter;
    private int port;
    String serverIpAddress;

    fileServer(Context context, ChatAdapter mAdapter, ListView messageList, ArrayList<Message> messageArray, int port,String serverIpAddress) {
        this.messageArray = messageArray;
        this.messageList = messageList;
        this.mAdapter = mAdapter;
        this.port = port;
        this.context = context;
        this.serverIpAddress = serverIpAddress;
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
         /*   File filepath = context.getObbDir();
            Log.i(TAG,"FilesDir =>" + filepath+ "\n");
            String fileName =  new SimpleDateFormat("yyyyMMdd").format(new Date()) +"-" + serverIpAddress + ".txt";
            File file = new File(filepath,fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file,true);
                String history = "Server received a file from => "+ serverIpAddress +"\n";
                fos.write(history.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }
    }

}
