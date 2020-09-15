package com.example.drmess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.drmess.connect.ConnectWithPhone;
import com.example.drmess.connect.WaitForConnect;
import com.example.drmess.doubleratchet.TypeConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Boolean createdServer;
    Executors executor;

    TypeConverter typeConverter;
    EditText inputId;
    TextView serverWait;
    Button connectButton;
    Button createServerButton;

    WaitForConnect waitForConnect;
    ConnectWithPhone connectWithPhone;

    ExecutorService executorService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputId = (EditText)findViewById(R.id.inputId);
        serverWait = (TextView)findViewById(R.id.waitForConnection);
        connectButton = (Button)findViewById(R.id.connectButton);
        createServerButton = (Button)findViewById(R.id.createServerButton);
        typeConverter = new TypeConverter();

        createdServer = false;




    }


    public void send(View v){

        try{

            connectWithPhone = new ConnectWithPhone();
            connectWithPhone.setConnectWithPhoneListener(new ConnectWithPhone.ConnectWithPhoneInterface(){
                @Override
                public void onConnect(){
                    chat();
                }
            });
            connectWithPhone.execute(inputId.getText().toString());

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void chat(){
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("isAlice", true);
        startActivity(i);
    }

    public void createServer(View v){

        Intent i = new Intent(this, WaitToConnectActivity.class);
        startActivity(i);

/*
        if(!createdServer){

            waitForConnect = new WaitForConnect();
            waitForConnect.setWaitForConnectListener(new WaitForConnect.WaitForConnectInterface(){
                @Override
                public void onConnect(){
                    test();
                }
                @Override
                public void waiting(){

                }
            });

            System.out.println("Created Server");
            createdServer = true;

            connectButton.setEnabled(false);
            inputId.setEnabled(false);
            serverWait.setText("Waiting for connection");
            createServerButton.setText("Close server");


            waitForConnect.execute();

        } else{

            System.out.println("Attempt to close server");
            waitForConnect.cancel(false);

            while(!waitForConnect.isCancelled()){
                System.out.println("Waiting for closing connection");
            }

            System.out.println("Server closed");
            createdServer = false;

            serverWait.setText("");
            createServerButton.setText("Create server");

            inputId.setEnabled(true);
            connectButton.setEnabled(true);

        }
*/
    }

    static {
        // Load native library ECDH-Curve25519-Mobile implementing Diffie-Hellman key
        // exchange with elliptic curve 25519.
        String TAG = "ECDH-Curve25519-Mobile";
        try {
            System.loadLibrary("ecdhcurve25519");
            Log.i(TAG, "Loaded ecdhcurve25519 library.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Error loading ecdhcurve25519 library: " + e.getMessage());
        }
    }



}