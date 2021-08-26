package com.hanzyusuf.vitacompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TCPClient.TCPClientListener {

    private static final int PORT = 1338; // TODO: if needed, set port based on user input
    TCPClient tcpClient = null;
    View layout_manual, layout_device_actions, layout_progressBar;
    EditText et_ip_address;
    Button btn_download_note, btn_fill_network_id, btn_fill_last_saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init tcp client
        tcpClient = new TCPClient(this);

        // --- init layout views and related buttons ---

        layout_manual = findViewById(R.id.layout_manual);
        layout_device_actions = findViewById(R.id.layout_device_actions);
        layout_progressBar = findViewById(R.id.layout_progressBar);

        // --- init layout specific views ---

        // manual
        btn_download_note = findViewById(R.id.btn_download_note);
        btn_download_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/devnoname120/vitacompanion/"));
                startActivity(in);
            }
        });

        et_ip_address = findViewById(R.id.et_ip_address);

        // --- get saved ip (if any) else load current device's network ID ---
        String ip = SharedPreferencesManager.getLastIP();

        if(ip == null) {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            et_ip_address.setText(ip.substring(0, ip.lastIndexOf(".") + 1));
            et_ip_address.requestFocus();
            et_ip_address.setSelection(et_ip_address.getText().length());
        }
        else
            et_ip_address.setText(ip);

        btn_fill_network_id = findViewById(R.id.btn_fill_network_id);
        btn_fill_network_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                et_ip_address.setText(ip.substring(0, ip.lastIndexOf(".") + 1));
                et_ip_address.requestFocus();
                et_ip_address.setSelection(et_ip_address.getText().length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_ip_address, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        btn_fill_last_saved = findViewById(R.id.btn_fill_last_saved);
        btn_fill_last_saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = SharedPreferencesManager.getLastIP();
                if(ip==null){
                    Toast.makeText(MainActivity.this, "No previously saved device IP found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                et_ip_address.setText(ip);
                et_ip_address.requestFocus();
            }
        });

        // --- init device action buttons ---

        View.OnClickListener deviceActionButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.imgbtn_vita_screen_on:
                        sendCommand("screen on");
                        break;
                    case R.id.imgbtn_vita_screen_off:
                        sendCommand("screen off");
                        break;
                    case R.id.imgbtn_vita_kill_apps:
                        sendCommand("destroy");
                        break;
                    case R.id.imgbtn_vita_reboot:
                        sendCommand("reboot");
                        break;
                }
            }
        };

        ImageButton imgbtn_vita_screen_on = findViewById(R.id.imgbtn_vita_screen_on);
        ImageButton imgbtn_vita_screen_off = findViewById(R.id.imgbtn_vita_screen_off);
        ImageButton imgbtn_vita_kill_apps = findViewById(R.id.imgbtn_vita_kill_apps);
        ImageButton imgbtn_vita_reboot = findViewById(R.id.imgbtn_vita_reboot);

        imgbtn_vita_screen_on.setOnClickListener(deviceActionButtonClickListener);
        imgbtn_vita_screen_off.setOnClickListener(deviceActionButtonClickListener);
        imgbtn_vita_kill_apps.setOnClickListener(deviceActionButtonClickListener);
        imgbtn_vita_reboot.setOnClickListener(deviceActionButtonClickListener);

    }

    private void sendCommand(String cmd) {
        String ip = et_ip_address.getText().toString();
        if(!Patterns.IP_ADDRESS.matcher(ip).matches()) {
            Toast.makeText(this, "Please enter a valid ip address!", Toast.LENGTH_SHORT).show();
            et_ip_address.requestFocus();
            return;
        }
        layout_progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        tcpClient.sendData(et_ip_address.getText().toString(), PORT, cmd);
    }

    @Override
    public void onResponse(final String response) {
        Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
        Log.d("TEST", response);
        layout_progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}