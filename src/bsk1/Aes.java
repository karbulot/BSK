/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsk1;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import org.xml.sax.SAXException;
/**
 *
 * @author Admin
 */
public class Aes {    
    public static void encrypt(File inputFile, final String outputFileName, List<User> selectedUsers, String ciphermode, int blockSize) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
        SecretKey sessionKey = generateSessionKey();
        byte[] input = Files.readAllBytes(inputFile.toPath());
        Cipher cipher = Cipher.getInstance("AES/"+ciphermode+"/PKCS5Padding");
        byte[] iv = new byte[blockSize/8];
        if (!"ECB".equals(ciphermode)){
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivParameterSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey);
        } 
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        File outputFile = new File(inputFile.toPath().getParent().toFile(),outputFileName);
        new FileWriter(outputFile).close();
        FileOutputStream outputStream = new FileOutputStream(outputFile,true);
        outputStream.write(generateXML(sessionKey.getAlgorithm(),"256",Integer.toString(ctLength),ciphermode,iv,selectedUsers,sessionKey, cipherText).getBytes());
        System.out.println("Gotowe");
    }
    
    public static void decrypt(File inputFile, final String outputFileName, User currentUser) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ParserConfigurationException, SAXException, InvalidAlgorithmParameterException{
        SecretKey sessionKey = null;
        String algorithm, ciphermode;
        int keySize, blockSize;
        byte[] encryptedFile;
        byte[] iv;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("User");
        boolean userFound = false;
        for (int i = 0; i < nList.getLength(); i++) {
            if (nList.item(i).getFirstChild().getNextSibling().getTextContent().equals(currentUser.getName())){
                System.out.println(nList.item(i).getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent());
                sessionKey = decryptSessionKey(nList.item(i).getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent().getBytes(),currentUser.getPrivateKey());
                userFound = true;
                break;
            }
        }
        if (!userFound){
            sessionKey = decryptSessionKey(doc.getElementsByTagName("SessionKey").item(0).getTextContent().getBytes(),currentUser.getPrivateKey());
        }
        
        algorithm = doc.getElementsByTagName("Algorithm").item(0).getTextContent();
        System.out.println(algorithm);
        keySize = Integer.parseInt(doc.getElementsByTagName("KeySize").item(0).getTextContent());
        blockSize = Integer.parseInt(doc.getElementsByTagName("BlockSize").item(0).getTextContent());
        ciphermode = doc.getElementsByTagName("CipherMode").item(0).getTextContent();
        iv = Base64.getDecoder().decode(doc.getElementsByTagName("IV").item(0).getTextContent().getBytes());
        encryptedFile = Base64.getDecoder().decode(doc.getElementsByTagName("Content").item(0).getTextContent().getBytes());
        
        //byte[] input = Files.readAllBytes(inputFile.toPath());
        Cipher cipher = Cipher.getInstance(algorithm+"/"+ciphermode+"/PKCS5Padding");
        
        if (!"ECB".equals(ciphermode)){
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, sessionKey, ivParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, sessionKey);
        } 
        byte[] plainText = new byte[cipher.getOutputSize(blockSize)];
        int ptLength = cipher.update(encryptedFile, 0, blockSize, plainText, 0);
        ptLength += cipher.doFinal(plainText, ptLength);    
        System.out.println(new String(plainText));
        System.out.println(ptLength);
        Files.write(new File(inputFile.toPath().getParent().toFile(),outputFileName).toPath(), plainText);
        System.out.println("Gotowe");
        
    }

        /*

        public static void encryptCfb(File inputFile, final String outputFileName) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException{
        byte[] input = Files.readAllBytes(inputFile.toPath());
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        key = generator.generateKey();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        System.out.println(new String(cipherText));
        System.out.println(ctLength);
        Files.write(new File(inputFile.toPath().getParent().toFile(),outputFileName).toPath(), cipherText);
        System.out.println("Gotowe");
    }
    public static void decryptCfb(File inputFile, final String outputFileName) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException{
        byte[] input = Files.readAllBytes(inputFile.toPath());
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = new byte[cipher.getOutputSize(input.length)];
        int ptLength = cipher.update(input, 0, input.length, plainText, 0);
        ptLength += cipher.doFinal(plainText, ptLength);    
        System.out.println(new String(plainText));
        System.out.println(ptLength);
        Files.write(new File(inputFile.toPath().getParent().toFile(),outputFileName).toPath(), plainText);
        System.out.println("Gotowe");
    }
        public static void encryptOfb(File inputFile, final String outputFileName) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException{
        byte[] input = Files.readAllBytes(inputFile.toPath());
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        key = generator.generateKey();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        System.out.println(new String(cipherText));
        System.out.println(ctLength);
        Files.write(new File(inputFile.toPath().getParent().toFile(),outputFileName).toPath(), cipherText);
        System.out.println("Gotowe");
    }
    public static void decryptOfb(File inputFile, final String outputFileName) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException{
        byte[] input = Files.readAllBytes(inputFile.toPath());
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = new byte[cipher.getOutputSize(input.length)];
        int ptLength = cipher.update(input, 0, input.length, plainText, 0);
        ptLength += cipher.doFinal(plainText, ptLength);    
        System.out.println(new String(plainText));
        System.out.println(ptLength);
        Files.write(new File(inputFile.toPath().getParent().toFile(),outputFileName).toPath(), plainText);
        System.out.println("Gotowe");
    }
*/
    
    private static String generateXML(String algorithm, String keysize, String blocksize, String ciphermode, byte[] iv, List<User> users, SecretKey sessionKey, byte[] content){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n<EncryptedFile>\n<EncryptedFileHeader>\n<Algorithm>")
                .append(algorithm).append("</Algorithm>\n<KeySize>")
                .append(keysize).append("</KeySize>\n<BlockSize>")
                .append(blocksize).append("</BlockSize>\n<CipherMode>")
                .append(ciphermode).append("</CipherMode>\n<IV>").
                append(new String(Base64.getEncoder().encode(iv))).append("</IV>\n");
        if (!users.isEmpty()){
            stringBuilder.append("\t<ApprovedUsers>");
            for (User user : users){
                try {
                    stringBuilder.append("\n\t\t<User>\n\t\t<Email>")
                            .append(user.getName()).append("</Email>\n\t\t<SessionKey>")
                            .append(new String(encryptSessionKey(sessionKey,user.getPublicKey()))).append("</SessionKey>\n\t\t</User>\n");
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Aes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(Aes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchPaddingException ex) {
                    Logger.getLogger(Aes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalBlockSizeException ex) {
                    Logger.getLogger(Aes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadPaddingException ex) {
                    Logger.getLogger(Aes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            stringBuilder.append("\t</ApprovedUsers>\n");
        }    
        stringBuilder.append("</EncryptedFileHeader>\n").append("<Content>").append(new String(Base64.getEncoder().encode(content))).append("</Content>\n</EncryptedFile>\n");
        return stringBuilder.toString();
    }
    
    private static SecretKey generateSessionKey() throws NoSuchAlgorithmException{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        return generator.generateKey();
    }
    
    private static byte[] encryptSessionKey(SecretKey sessionKey, Key publicKey) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encodedKey = cipher.doFinal(sessionKey.getEncoded());
        return Base64.getEncoder().encode(encodedKey);
    }
    
        
    private static SecretKey decryptSessionKey(byte[] base64EncodedKey, Key privateKey) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedKey = cipher.doFinal(Base64.getDecoder().decode(base64EncodedKey));
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
