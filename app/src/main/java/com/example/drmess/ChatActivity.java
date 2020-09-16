package com.example.drmess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.drmess.connect.ExchangeMasterKeys;
import com.example.drmess.connect.ReceiveMessage;
import com.example.drmess.connect.SendMessage;
import com.example.drmess.doubleratchet.ConnectHandler;
import com.example.drmess.doubleratchet.User;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class ChatActivity extends AppCompatActivity {

    User user;
    Button endConnectionButton;
    Button sendMessageButton;
    EditText newMessageText;
    ConnectHandler connectHandler;

    ReceiveMessage receiveMessage;
    SendMessage sendMessage;

    Boolean waitForConnectionIsClicked;
    LinearLayout linearLayout;
    ScrollView scrollView;
    private static Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Boolean isAlice = getIntent().getExtras().getBoolean("isAlice");

        context = getApplicationContext();
        connectHandler = new ConnectHandler();
        waitForConnectionIsClicked = false;

        endConnectionButton = (Button)findViewById(R.id.endConnectionButton);
        sendMessageButton = (Button)findViewById(R.id.sendMessageButton);
        newMessageText = (EditText)findViewById(R.id.newMessageText);

        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        scrollView = (ScrollView)findViewById(R.id.scrollView);


        sendMessageButton.setOnClickListener(sendMessageClick);
        endConnectionButton.setOnClickListener(disconnectConnectionClick);

        ExchangeMasterKeys exchangeMasterKeys = new ExchangeMasterKeys();
        exchangeMasterKeys.execute(isAlice);
        try {
            user = exchangeMasterKeys.get();
        } catch (Exception e){
            e.printStackTrace();
        }

        waitForMessage();

    }

    private void waitForMessage(){
        receiveMessage = new ReceiveMessage();
        receiveMessage.setReceiveMessageListener(new ReceiveMessage.ReceiveMessageInterface() {
            @Override
            public void receivedMessage(String message) {
                if(message.compareTo("11ErrorSocket11") != 0) {
                    TextView textView = new TextView(context);
                    textView.setTextSize(24);
                    textView.setTextColor(Color.BLACK);
                    textView.setText(message);
                    linearLayout.addView(textView);
                    textView.setGravity(Gravity.LEFT);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                }

                waitForMessage();
            }
        });
        receiveMessage.execute(user);
    }

    private View.OnClickListener sendMessageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                sendMessage = new SendMessage();
                user.messageToSend = newMessageText.getText().toString();

                if(user.messageToSend.compareTo("") != 0) {
                    TextView textView = new TextView(context);
                    textView.setTextSize(24);
                    textView.setTextColor(Color.BLUE);


                    textView.setText(newMessageText.getText().toString());

                    linearLayout.addView(textView);
                    textView.setGravity(Gravity.RIGHT);
                }
                newMessageText.setText("");
                sendMessage.execute(user);

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });


        }
    };

    private View.OnClickListener disconnectConnectionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            disconnectConnection();
        }
    };

    private void disconnectConnection(){
        try {
            connectHandler.getSocket().close();
        } catch (Exception e){
            e.printStackTrace();
        }
        Context context = getApplicationContext();
        ProcessPhoenix.triggerRebirth(context);


    }


}
