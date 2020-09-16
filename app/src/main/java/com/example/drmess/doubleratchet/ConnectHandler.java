package com.example.drmess.doubleratchet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ConnectHandler {

    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;


    public static synchronized Socket getSocket(){
        return socket;
    }


    public static synchronized void setSocket(Socket socket){
        try {
            ConnectHandler.socket = socket;
            socket.setSoTimeout(750);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public static DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public static DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public static void disconnectSocket(){
        try {
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
