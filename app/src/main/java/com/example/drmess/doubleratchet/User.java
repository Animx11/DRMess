package com.example.drmess.doubleratchet;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import at.favre.lib.crypto.HKDF;

import static java.lang.Integer.parseInt;

public class User implements Serializable {


    int MAX_SKIP = 10;

    DH dh;
    TypeConverter typeConverter;
    RatchetCipher ratchetCipher;
    ConnectHandler connect;

    public String messageToSend = "";
    public String receivedMessage = "";


    // Ratchet variables

    // dhSend
    byte[] dhSendPrivate;
    byte[] dhSendPublic;

    byte[] dhReceive;
    public byte[] rootKey;
    byte[] chainKeySend, chainKeyReceive;
    int ns, nr, pn;
    HashMap<byte[], byte[]> mkSkipped;

    public User() {
        dh = new DH();
        typeConverter = new TypeConverter();
        ratchetCipher = new RatchetCipher();
    }

    public void initializeMasterKeyExchange(boolean alice) {

        try {

            // Connect testing
            connect = new ConnectHandler();

            // Generating first DH key pair
            byte[] firstPrivate, firstPublic;
            firstPrivate = dh.generateDHPrivateKey();
            firstPublic = dh.generateDHPublicKey(firstPrivate);

            // Sending public key
            this.connect.getDataOutputStream().writeUTF(String.valueOf(firstPublic.length));
            this.connect.getDataOutputStream().write(firstPublic);

            // Receiving public key
            int lengthReceivingPubKey = parseInt(connect.getDataInputStream().readUTF());
            byte[] receivedPublicKey = new byte[lengthReceivingPubKey];
            this.connect.getDataInputStream().readFully(receivedPublicKey);

            // Computing shared key
            byte[] sharedKey = dh.generateDHSharedSecret(firstPrivate, receivedPublicKey);

            // Initialize double ratchet
            if(alice){
                initializeDoubleRatchetAlice(receivedPublicKey, sharedKey);
            } else {
                initializeDoubleRatchetBob(firstPrivate, firstPublic, sharedKey);
            }


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initializeDoubleRatchetAlice(byte[] bobPubKey, byte[] masterKey){

        try {

            dhSendPrivate = dh.generateDHPrivateKey();
            dhSendPublic = dh.generateDHPublicKey(dhSendPrivate);
            dhReceive = bobPubKey;

            // HKDF
            byte[] pseudoRandomKey = HKDF.fromHmacSha256().extract(masterKey, dh.generateDHSharedSecret(dhSendPrivate, dhReceive));
            rootKey = HKDF.fromHmacSha256().expand(pseudoRandomKey, "RootKey".getBytes(), 32);
            chainKeySend = HKDF.fromHmacSha256().expand(pseudoRandomKey, "ChainKey".getBytes(), 32);

            chainKeyReceive = null;
            ns = nr = pn = 0;
            mkSkipped = new HashMap<byte[], byte[]>();


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initializeDoubleRatchetBob(byte[] bobPrivKey, byte[] bobPubKey, byte[] masterKey){

        try{

            dhSendPrivate = bobPrivKey;
            dhSendPublic = bobPubKey;
            dhReceive = null;
            rootKey = masterKey;
            chainKeySend = null;
            chainKeyReceive = null;
            ns = nr = pn = 0;
            mkSkipped = new HashMap<byte[], byte[]>();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendMessage(){

        try{

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(chainKeySend, "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] messageKey = mac.doFinal("MessageKey".getBytes());
            chainKeySend = mac.doFinal("ChainKey".getBytes());
            ns += 1;

            byte[] encryptedMessage = ratchetCipher.encryptMessage(messageKey, messageToSend);

            // Header sending data
            connect.getDataOutputStream().writeUTF(String.valueOf(dhSendPublic.length));
            connect.getDataOutputStream().write(dhSendPublic);
            connect.getDataOutputStream().writeUTF(String.valueOf(pn));
            connect.getDataOutputStream().writeUTF(String.valueOf(ns));
            connect.getDataOutputStream().writeUTF(String.valueOf(encryptedMessage.length));
            connect.getDataOutputStream().write(encryptedMessage);


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public byte[] receivingMessage(){

        try{
            // Receiving data
            // Receiving Public Key
            int receivedPubKeyLength = parseInt(connect.getDataInputStream().readUTF());
            byte[] receivedPubKey = new byte[receivedPubKeyLength];
            connect.getDataInputStream().readFully(receivedPubKey);
            // Receiving message numbers
            int receivedPN = parseInt(connect.getDataInputStream().readUTF());
            int receivedNS = parseInt(connect.getDataInputStream().readUTF());
            // Receiving encrypted message
            int receivedEncryptedMessageLength = parseInt(connect.getDataInputStream().readUTF());
            byte[] receivedEncryptedMessage = new byte[receivedEncryptedMessageLength];
            connect.getDataInputStream().readFully(receivedEncryptedMessage);
            // End Receiving data

            byte[] plaintext = trySkippedMessageKeys(receivedPubKey, receivedEncryptedMessage);
            if(plaintext != null){
                return plaintext;
            }
            if(dhReceive == null){
                // Zobacz czy zadziała bez skippMessageKeys wtedy może tamto zadziała
                skipMessageKeys(receivedPN);
                dhRatchet(receivedPubKey);
            } else if(Arrays.equals(receivedPubKey, dhReceive) != true){
                skipMessageKeys(receivedPN);
                dhRatchet(receivedPubKey);
            }

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(chainKeyReceive, "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] messageKey = mac.doFinal("MessageKey".getBytes());
            chainKeyReceive = mac.doFinal("ChainKey".getBytes());
            nr += 1;

            byte[] decMessage = ratchetCipher.decryptMessage(receivedEncryptedMessage, messageKey);


            return decMessage;


        } catch (Exception e){
            return "".getBytes();
        }

    }

    private byte[] trySkippedMessageKeys(byte[] receivedPubKey, byte[] receivedEncryptedMessage){
        try{

            if(mkSkipped.isEmpty()){
                return null;
            }
            else if(mkSkipped.containsKey(receivedPubKey)){
                byte[] messageKey = mkSkipped.get(receivedPubKey);
                mkSkipped.remove(receivedPubKey);
                return ratchetCipher.decryptMessage(receivedEncryptedMessage, messageKey);
            } else {
                return null;
            }

        }catch (Exception e){
            System.out.println("trySkippedMessageKeys: Error has occured: " + e);
            return null;
        }
    }

    private void skipMessageKeys(int until){
        try{

            if(nr + MAX_SKIP < until){
                throw new Exception("Too many skipped messages");
            }
            if(dhReceive != null){
                while(nr < until){

                    Mac mac = Mac.getInstance("HmacSHA256");
                    SecretKeySpec secretKeySpec = new SecretKeySpec(chainKeyReceive, "HmacSHA256");
                    mac.init(secretKeySpec);

                    byte[] messageKey = mac.doFinal("MessageKey".getBytes());
                    chainKeyReceive = mac.doFinal("ChainKey".getBytes());
                    mkSkipped.put(dhReceive, messageKey);
                    nr += 1;
                }
            }

        }catch (Exception e){
            System.out.println("skipMessageKeys: Error has occured: " + e);
        }
    }

    private void dhRatchet(byte[] receivedPubKey){

        pn = ns;
        ns = nr = 0;
        dhReceive = receivedPubKey;

        byte[] pseudoRandomKey = HKDF.fromHmacSha256().extract(rootKey, dh.generateDHSharedSecret(dhSendPrivate, dhReceive));
        rootKey = HKDF.fromHmacSha256().expand(pseudoRandomKey, "RootKey".getBytes(), 32);
        chainKeyReceive = HKDF.fromHmacSha256().expand(pseudoRandomKey, "ChainKey".getBytes(), 32);

        dhSendPrivate = dh.generateDHPrivateKey();
        dhSendPublic = dh.generateDHPublicKey(dhSendPrivate);

        pseudoRandomKey = HKDF.fromHmacSha256().extract(rootKey, dh.generateDHSharedSecret(dhSendPrivate, dhReceive));
        rootKey = HKDF.fromHmacSha256().expand(pseudoRandomKey, "RootKey".getBytes(), 32);
        chainKeySend = HKDF.fromHmacSha256().expand(pseudoRandomKey, "ChainKey".getBytes(), 32);


    }

}
