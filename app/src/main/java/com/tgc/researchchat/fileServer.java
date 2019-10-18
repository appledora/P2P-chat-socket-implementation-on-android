package com.tgc.researchchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

public class fileServer extends Thread {

    private Context context;
    private String serverIpAddress;
    private String TAG = "FILE SERVER";
    private RecyclerView messageList;
    private ArrayList<Message> messageArray;
    private ChatAdapterRecycler mAdapter;
    private int port;

    fileServer(Context context, ChatAdapterRecycler mAdapter, RecyclerView messageList, ArrayList<Message> messageArray, int port, String serverIpAddress) {
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
            while (!Thread.interrupted()) {
                Socket connectFileSocket = fileSocket.accept();
                Log.d(TAG, "run: File Opened");
                receiveFiles handleFile = new receiveFiles();
                handleFile.execute(connectFileSocket);
            }
            fileSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class receiveFiles extends AsyncTask<Socket, Void, String> {
        String text;

        @Override
        protected String doInBackground(Socket... sockets) {
            try {
                File testDirectory = Environment.getExternalStorageDirectory();
                if (!testDirectory.exists())
                    testDirectory.mkdirs();
                try {
                    InputStream inputStream = sockets[0].getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);

                    String fileName = dataInputStream.readUTF();
                    File outputFile = new File(testDirectory+"/Download/", fileName);
                    text = fileName;

                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
                    long fileSize = dataInputStream.readLong();
                    int bytesRead;
                    byte[] byteArray = new byte[8192 * 16];

                    while (fileSize > 0 && (bytesRead = dataInputStream.read(byteArray, 0, (int) Math.min(byteArray.length, fileSize))) != -1) {
                        outputStream.write(byteArray, 0, bytesRead);
                        fileSize -= bytesRead;
                    }
                    inputStream.close();
                    dataInputStream.close();
                    outputStream.flush();
                    outputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return text;
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: Result" + result);
            if (result != null) {
                messageArray.add(new Message("New File Received: " + result, 1, Calendar.getInstance().getTime()));
                messageList.setAdapter(mAdapter);

            }
        }
    }


}
