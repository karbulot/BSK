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
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
/**
 *
 * @author Admin
 */
public class Aes {
    static SecretKey key;
    
    public static void encryptEcb(File inputFile, final String outputFileName) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException{
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
    public static void decryptEcb(File inputFile, final String outputFileName) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException{
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
    
    private static String generateXML(String algorithm, String keysize, String blocksize, String ciphermode, String iv, User[] users){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n<EncryptedFileHeader> \n<Algorithm>")
                .append(algorithm).append("</Algorithm>\n<KeySize>")
                .append(keysize).append("</KeySize> \n <BlockSize>")
                .append(blocksize).append("</BlockSize> \n<CipherMode>")
                .append(ciphermode).append("</CipherMode><IV>").
                append(iv).append(" </IV>\n");
        if (users.length!=0){
            stringBuilder.append("\t<ApprovedUsers>\n");
            for (User user : users){
                stringBuilder.append("\t\t<User>\n<Email>")
                        .append(user.getName()).append("</Email>\n\t\t<SessionKey>")
                        .append(user.getKey()).append("</SessionKey>\n</User>\n");
            }
            stringBuilder.append("\t</ApprovedUsers>\n");
        }    
        stringBuilder.append("</EncryptedFileHeader> \n");
        return stringBuilder.toString();
    }
    
    
}
