/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musala.drone.service.crypto;

/**
 *
 * @author Eboda
 */
public interface CryptoService {
    String ENCRYPTED_INDICATOR = "encrypted#";
    public String encrypt(String plainText) throws Exception;
    public String decrypt(String cypherText)throws Exception;
    public byte[] signData(byte[] data)throws Exception;
    public boolean verifyData(byte[] data, byte[] digiSignature)throws Exception;    
    public byte[] hash(byte[] data, String algorithm) throws Exception;
    
    
    
    
    
}
