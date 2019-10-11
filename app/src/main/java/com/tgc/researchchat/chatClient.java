package com.tgc.researchchat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class chatClient extends Activity {
    EditText smessage;
    Button sent;
    String serverIpAddress = "";
    int myport;
    int sendPort;
    ServerSocket serverSocket;
    Handler handler = new Handler();
    String TAG = "CLIENT ACTIVITY";
    String tempS;
    ListView messageRecycler;
    public static ChatAdapter mAdapter;
    ListView message_List;
    ArrayList<Message> messageArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbox);
        smessage = (EditText) findViewById(R.id.edittext_chatbox);
        message_List = findViewById(R.id.message_list);
        messageArray = new ArrayList<Message>();
        mAdapter = new ChatAdapter(this, messageArray);
        message_List.setAdapter(mAdapter);

        sent = (Button) findViewById(R.id.button_chatbox_send);
        messageRecycler = findViewById(R.id.message_list);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String info = bundle.getString("ip&port");
            String[] infos = info.split(" ");
            serverIpAddress = infos[0];
            sendPort = Integer.parseInt(infos[1]);
            myport = Integer.parseInt(infos[2]);
            Log.d(TAG, "info => " + serverIpAddress + " " + sendPort + " " + myport);
        }
        if (!serverIpAddress.equals("")) {
            chatServer s = new chatServer(mAdapter, message_List, messageArray, myport);
            s.start();
        }
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.execute();
            }
        });


    }

    public class User extends AsyncTask<Void, Void, String> {

        String msg = smessage.getText().toString();


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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return msg;
        }

        protected void onPostExecute(String result) {

            Log.i(TAG, "on post execution result => " + result);

            messageArray.add(new Message(result, 0));
            message_List.setAdapter(mAdapter);
            smessage.setText("");
        }


    }


}