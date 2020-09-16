package com.example.drmess;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.drmess.connect.ExchangeMasterKeys;
import com.example.drmess.connect.ReceiveMessage;
import com.example.drmess.connect.SendMessage;
import com.example.drmess.doubleratchet.ConnectHandler;
import com.example.drmess.doubleratchet.TypeConverter;
import com.example.drmess.doubleratchet.User;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class ChatActivity extends AppCompatActivity {

    User user;
    TextView textView;
    TypeConverter typeConverter;
    Button endConnectionButton;
    Button sendMessageButton;
    Button waitForMessageButton;
    EditText newMessageText;
    ConnectHandler connectHandler;

    ReceiveMessage receiveMessage;
    SendMessage sendMessage;

    Boolean waitForConnectionIsClicked;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Boolean isAlice = getIntent().getExtras().getBoolean("isAlice");

        connectHandler = new ConnectHandler();
        waitForConnectionIsClicked = false;

        endConnectionButton = (Button)findViewById(R.id.endConnectionButton);
        sendMessageButton = (Button)findViewById(R.id.sendMessageButton);
        newMessageText = (EditText)findViewById(R.id.newMessageText);
        waitForMessageButton = (Button)findViewById(R.id.waitForMessageButton);
        textView = (TextView)findViewById(R.id.message);


        sendMessageButton.setOnClickListener(sendMessageClick);
        endConnectionButton.setOnClickListener(disconnectConnectionClick);
        waitForMessageButton.setOnClickListener(waitForMessageClick);

        ExchangeMasterKeys exchangeMasterKeys = new ExchangeMasterKeys();
        exchangeMasterKeys.execute(isAlice);
        try {
            user = exchangeMasterKeys.get();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void waitForMessage(){
        receiveMessage = new ReceiveMessage();
        receiveMessage.setReceiveMessageListener(new ReceiveMessage.ReceiveMessageInterface() {
            @Override
            public void receivedMessage(String message) {
                if(message.compareTo("11ErrorSocket11") != 0) {
                    textView.setText(message);
                }
                waitForMessage();
            }
        });
        receiveMessage.execute(user);
    }

    private View.OnClickListener waitForMessageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(!waitForConnectionIsClicked) {
                receiveMessage = new ReceiveMessage();
                receiveMessage.setReceiveMessageListener(new ReceiveMessage.ReceiveMessageInterface() {
                    @Override
                    public void receivedMessage(String message) {
                        if(message.compareTo("11ErrorSocket11") != 0) {
                            textView.setText(message);
                        }
                        waitForMessage();
                    }
                });
                waitForConnectionIsClicked = true;
                sendMessageButton.setEnabled(false);
                newMessageText.setEnabled(false);

                receiveMessage.execute(user);
            } else {
                waitForConnectionIsClicked = false;
                sendMessageButton.setEnabled(true);
                newMessageText.setEnabled(true);
                receiveMessage.cancel(true);
            }

            /*
            try {
                String message = receiveMessage.get();
                textView.setText(message);
            } catch (Exception e){
                e.printStackTrace();
            }
            */
        }
    };


    private View.OnClickListener sendMessageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                sendMessage = new SendMessage();
                user.messageToSend = newMessageText.getText().toString();
                newMessageText.setText("");
                sendMessage.execute(user);

        }
    };

    private View.OnClickListener disconnectConnectionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            disconnectConnection();
        }
    };

    private void disconnectConnection(){
        Intent i = new Intent(this, MainActivity.class);
        connectHandler.setSocket(null);
        user = null;
        startActivity(i);
    }


}
