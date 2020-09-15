package com.example.drmess;

import android.content.Intent;
import android.os.Bundle;

import com.example.drmess.connect.WaitForConnect;
import com.example.drmess.doubleratchet.Connect;
import com.example.drmess.doubleratchet.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WaitToConnectActivity extends AppCompatActivity {

    WaitForConnect waitForConnect;
    Button closeServerButton;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_to_connect_activity);
        closeServerButton = (Button)findViewById(R.id.closeServer);
        closeServerButton.setOnClickListener(closeServerClick);
        textView = (TextView)findViewById(R.id.waitForConnectionText);

        waitForConnect = new WaitForConnect();
        waitForConnect.setWaitForConnectListener(new WaitForConnect.WaitForConnectInterface(){
            @Override
            public void onConnect(){
                chat();
            }
        });

        waitForConnect.execute();

    }

    public void chat(){
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("isAlice", false);
        startActivity(i);
    }

    public void closeServer(){
        waitForConnect.cancel(true);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private View.OnClickListener closeServerClick = new View.OnClickListener() {
        public void onClick(View v) {
            closeServer();
        }
    };

}