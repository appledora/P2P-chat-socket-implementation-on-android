package com.tgc.researchchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class chatClient extends AppCompatActivity {
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        sent = findViewById(R.id.button_chatbox_send);
        fileUp = findViewById(R.id.file_send);

        setSupportActionBar(toolbar);

        messageArray = new ArrayList<>();
        mAdapter = new ChatAdapter(this, messageArray);
        message_List.setAdapter(mAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String info = bundle.getString("ip&port");
            String[] infos = info.split(" ");
            serverIpAddress = infos[0];
            sendPort = Integer.parseInt(infos[1]);
            myport = Integer.parseInt(infos[2]);
        }

        getSupportActionBar().setTitle("Connection to " + serverIpAddress);

        if (!serverIpAddress.equals("")) {
            chatServer s = new chatServer(this, getApplicationContext(), mAdapter, message_List, messageArray, myport, serverIpAddress);
            s.start();
            fileServer f = new fileServer(getApplicationContext(), mAdapter, message_List, messageArray, myport,serverIpAddress);
            f.start();
        }
        sent.setOnClickListener(v -> {
            if (!smessage.getText().toString().isEmpty()) {
                User user = new User("1:" + smessage.getText().toString());
                user.execute();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please write something", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        fileUp.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*");
            startActivityForResult(Intent.createChooser(intent, "Select file"), 1);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings: {
                final Context context = chatClient.this;
                ColorPickerDialogBuilder
                        .with(context)
                        .setTitle("Choose color")
                        .initialColor(0xffffffff)
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                changeBackgroundColor(selectedColor);
                                User user = new User("2:" + Integer.toHexString(selectedColor));
                                user.execute();
                                Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public final void changeBackgroundColor(Integer selectedColor) {
        LayerDrawable layerDrawable = (LayerDrawable) message_List.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.shapeColor);
        gradientDrawable.setColor(selectedColor);
    }

    @SuppressLint("StaticFieldLeak")

    public class User extends AsyncTask<Void, Void, String> {
        String msg;

        User(String message) {
            msg = message;
        }
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
            if (stringBuilder.charAt(0) == '1' && stringBuilder.charAt(1) == ':') {
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                result = stringBuilder.toString();
                File path = getApplicationContext().getObbDir();
                Log.i(TAG, "FilesDir =>" + path + "\n");
                String fileName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + serverIpAddress + ".txt";
                File file = new File(path, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file, true);
                    String history = "client: " + result + "\n";
                    fos.write(history.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                messageArray.add(new Message(result, 0));
                message_List.setAdapter(mAdapter);
                smessage.setText("");
            }
        }


    }

    class fileTransfer extends AsyncTask<Void, Integer, String> {
        String path;

        fileTransfer(String path) {
            this.path = path;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String filenameX = "";
            String ipadd = serverIpAddress;
            int portr = sendPort + 1;
            try {
                Socket clientSocket = new Socket(ipadd, portr);
                if (path.charAt(0) != '/') {
                    path = "/storage/emulated/0/" + path;
                }
                File file = new File(path);
                if (path.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Path is empty", Toast.LENGTH_SHORT);
                    toast.show();
                }
                Log.d(TAG, "doInBackground: " + path);

                FileInputStream fileInputStream = new FileInputStream(file);

                long fileSize = file.length();
                byte[] byteArray = new byte[(int) fileSize];

                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                dataInputStream.readFully(byteArray, 0, byteArray.length);

                OutputStream outputStream = clientSocket.getOutputStream();

                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF(file.getName());
                dataOutputStream.writeLong(byteArray.length);

                filenameX = file.getName();

                dataOutputStream.write(byteArray, 0, byteArray.length);
                dataOutputStream.flush();

                outputStream.write(byteArray, 0, byteArray.length);
                outputStream.flush();

                outputStream.close();
                dataOutputStream.close();

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return filenameX;
        }

        @Override
        protected void onPostExecute(String name) {
            Log.d(TAG, "onPostExecute: " + name);
            File filepath = getApplicationContext().getObbDir();
            Log.i(TAG, "FilesDir =>" + filepath + "\n");
            String fileName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + serverIpAddress + ".txt";
            File file = new File(filepath, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file, true);
                String history = "client sent a file from => " + path + "\n";
                fos.write(history.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!name.isEmpty()) {
                messageArray.add(new Message("New File Sent: " + name, 0));
                message_List.setAdapter(mAdapter);
                smessage.setText("");
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "File cannot be sent. No Internet Connection", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}