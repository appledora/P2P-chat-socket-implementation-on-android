package com.tgc.researchchat;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText ip;
    EditText port;
    EditText portText;
    Button connectButton;
    TextView showIPtextId;
    String showIPaddress;
    String TAG = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portText = (EditText) findViewById(R.id.myPortEditText);
        ip = (EditText) findViewById(R.id.ipEditText);
        port = (EditText) findViewById(R.id.portEditText);
        connectButton = (Button) findViewById(R.id.connectButton);
        showIPtextId = (TextView) findViewById(R.id.showIPtextId);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                showIPaddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        showIPtextId.setText(showIPaddress);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String info = getInfo();
                Intent intent = new Intent(MainActivity.this, chatClient.class);
                intent.putExtra("ip&port", info);
                startActivity(intent);
                finish();
            }
        });

    }

    String getInfo() {

        String info = this.ip.getText().toString()+" "+this.port.getText().toString()+" "+this.portText.getText().toString();
        Log.i(TAG, "info => "+info);
        return info;
    }
}
