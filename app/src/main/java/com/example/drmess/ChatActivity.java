package com.example.drmess;

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

public class ChatActivity extends AppCompatActivity {

    User user;
    TextView textView;
    TypeConverter typeConverter;
    Button endConnectionButton;
    Button sendMessageButton;
    Button waitForMessageButton;
    EditText newMessageText;
    ConnectHandler connectHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Boolean isAlice = getIntent().getExtras().getBoolean("isAlice");

        connectHandler = new ConnectHandler();

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


    private View.OnClickListener waitForMessageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*
            receiveMessage = new ReceiveMessage();
            receiveMessage.setReceiveMessageListener(new ReceiveMessage.ReceiveMessageInterface() {
                @Override
                public void receivedMessage(String receivedMessage) {
                    showMessage(receivedMessage);
                }
            });
            receiveMessage.execute(user);
             */
            ReceiveMessage receiveMessage = new ReceiveMessage();
            receiveMessage.execute(user);
            try {
                String message = receiveMessage.get();
                textView.setText(message);
                v.invalidate();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    private View.OnClickListener sendMessageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*
            String message = newMessageText.getText().toString();
            user.messageToSend = message;
            SendMessage sendMessage = new SendMessage();
            sendMessage.execute(user);
             */
            SendMessage sendMessage = new SendMessage();
            user.messageToSend = newMessageText.getText().toString();
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
