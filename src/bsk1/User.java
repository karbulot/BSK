/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsk1;

import javax.crypto.SecretKey;

/**
 *
 * @author Admin
 */
public class User {
    private String name;
    private SecretKey key;
    public String getName(){
        return name;
    }
    public SecretKey getKey(){
        return key;
    }
}
