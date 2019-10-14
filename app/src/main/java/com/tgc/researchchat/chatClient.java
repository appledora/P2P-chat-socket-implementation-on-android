package com.tgc.researchchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class chatClient extends Activity {
    EditText smessage;
    ImageButton sent;
    String serverIpAddress = "";
    int myport;
    int sendPort;
    ServerSocket serverSocket;
    Handler handler = new Handler();
    String TAG = "CLIENT ACTIVITY";
    String tempS;
    public static ChatAdapter mAdapter;
    ListView message_List;
    ArrayList<Message> messageArray;
    ImageButton fileUp;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbox);
        smessage = findViewById(R.id.edittext_chatbox);
        message_List = findViewById(R.id.message_list);
        messageArray = new ArrayList<>();
        mAdapter = new ChatAdapter(this, messageArray);
        message_List.setAdapter(mAdapter);

        sent = findViewById(R.id.button_chatbox_send);
        fileUp = findViewById(R.id.file_send);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String info = bundle.getString("ip&port");
            String[] infos = info.split(" ");
            serverIpAddress = infos[0];
            sendPort = Integer.parseInt(infos[1]);
            myport = Integer.parseInt(infos[2]);
        }
        if (!serverIpAddress.equals("")) {
            chatServer s = new chatServer(getApplicationContext(), mAdapter, message_List, messageArray, myport);
            s.start();
            fileServer f = new fileServer(getApplicationContext(), mAdapter, message_List, messageArray, myport);
            f.start();
        }
        sent.setOnClickListener(v -> {
            if (!smessage.getText().toString().isEmpty()) {
                User user = new User();
                user.execute();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please write something", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        fileUp.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Select file"), 1);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path;
        if (requestCode == 1) {
            Uri txtUri = data.getData();
            path = txtUri.getPath();
            Log.d(TAG, "onActivityResult: " + path);
            String[] arrOfStr = path.split(":");
            Log.d(TAG, "onActivityResult: " + arrOfStr[1]);
            new fileTransfer(arrOfStr[1]).execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class User extends AsyncTask<Void, Void, String> {

        String nmsg = smessage.getText().toString();
        String msg = "1:" + nmsg;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String ipadd = serverIpAddress;
                int portr = sendPort;
                Socket clientSocket = new Socket(ipadd, portr);
                OutputStream outToServer = clientSocket.getOutputStream();
                PrintWriter output = new PrintWriter(outToServer);
                output.println(msg);
                output.flush();
                clientSocket.close();
                runOnUiThread(() -> sent.setEnabled(false)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return msg;
        }

        protected void onPostExecute(String result) {
            runOnUiThread(() -> sent.setEnabled(true));
            Log.i(TAG, "on post execution result => " + result);
            StringBuilder stringBuilder = new StringBuilder(result);
            stringBuilder.deleteCharAt(0);
            stringBuilder.deleteCharAt(0);
            result = stringBuilder.toString();
            messageArray.add(new Message(result, 0));
            message_List.setAdapter(mAdapter);
            smessage.setText("");

        }


    }

    class fileTransfer extends AsyncTask<Void, Integer, Integer> {
        String path;

        fileTransfer(String path) {
            this.path = path;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            String ipadd = serverIpAddress;
            int portr = sendPort + 1;
            try {
                Socket clientSocket = new Socket(ipadd, portr);

                File file = new File(path);
                if (path.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Path is empty", Toast.LENGTH_SHORT);
                    toast.show();
                }
                Log.d(TAG, "doInBackground: " + path);

                FileInputStream fileInputStream = new FileInputStream(file);
//                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                long fileSize = file.length();
                byte[] byteArray = new byte[(int) fileSize];

                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                dataInputStream.readFully(byteArray, 0, byteArray.length);

                OutputStream outputStream = clientSocket.getOutputStream();

                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF(file.getName());
                dataOutputStream.writeLong(byteArray.length);

                dataOutputStream.write(byteArray, 0, byteArray.length);
                dataOutputStream.flush();

                outputStream.write(byteArray, 0, byteArray.length);
                outputStream.flush();

                outputStream.close();
                dataOutputStream.close();
//                int transactionBytes = 0;
//                while ((transactionBytes = bufferedInputStream.read(byteArray, 0, byteArray.length)) != -1) {
//                    outputStream.write(byteArray, 0, byteArray.length);
//                    Log.d(TAG, "doInBackground: Transfering Bytes" + transactionBytes);
//                }
//                outputStream.flush();
//                bufferedInputStream.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}