/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsk1;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 *
 * @author Admin
 */
public class User {
    private String name;
    private KeyPair keyPair;
    private byte[] passwordHash;

    User(String name, byte[] password, byte[] encodedPublicKey, byte[] encodedPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

        this.name = name;
        this.passwordHash = password;
        //Key publicKey = new SecretKeySpec(encodedPublicKey,0,encodedPublicKey.length, "RSA");    
        //Key privateKey = new SecretKeySpec(encodedPrivateKey,0,encodedPrivateKey.length, "RSA");  
        //keyPair = new KeyPair((PublicKey)publicKey, (PrivateKey)privateKey);
        
        
        KeyFactory keyFactoryPriv = KeyFactory.getInstance("RSA");
        KeyFactory keyFactoryPubl = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publicKey = keyFactoryPriv.generatePublic(publicKeySpec);
        System.out.println(encodedPrivateKey);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactoryPubl.generatePrivate(privateKeySpec);
        keyPair = new KeyPair(publicKey, privateKey);
        
    }
    public String getName(){
        return name;
    }
    public Key getPrivateKey(){
        return keyPair.getPrivate();
    }
    
    public byte[] getPrivateKeyString(){
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(getPrivateKey().getEncoded());
        return pkcs8EncodedKeySpec.getEncoded();
    }
    
    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }
    
    public byte[] getPublicKeyString(){
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(getPublicKey().getEncoded());
        return x509EncodedKeySpec.getEncoded();
    }
    
    public byte[] getPasswordHash(){
        return passwordHash;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    User(String name, String pass) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        this.name = name;
        this.passwordHash = digest.digest(pass.getBytes());
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.genKeyPair();
    }
}
