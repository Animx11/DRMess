package com.example.drmess.doubleratchet;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import de.frank_durr.ecdh_curve25519.ECDHCurve25519;

public class DH implements Serializable {
/*
    public KeyPair generateDH(){

        try{

            SecureRandom random = new SecureRandom();
            byte[] privateKey = ECDHCurve25519.generate_secret_key(random);
            byte[] publicKey = ECDHCurve25519.generate_public_key(privateKey);

            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            PrivateKey privKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));

            KeyPair keyPair = new KeyPair(pubKey, privKey);

            return keyPair;

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
  */


    public byte[] generateDHPrivateKey(){
        SecureRandom random = new SecureRandom();
        return ECDHCurve25519.generate_secret_key(random);
    }

    public byte[] generateDHPublicKey(byte[] privateKey){
        return ECDHCurve25519.generate_public_key(privateKey);
    }

    public byte[] generateDHSharedSecret(byte[] privateKey, byte[] receivedPublicKey){
        return ECDHCurve25519.generate_shared_secret(privateKey, receivedPublicKey);
    }

}
