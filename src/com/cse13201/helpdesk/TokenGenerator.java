package com.cse13201.helpdesk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TokenGenerator {
	public TokenGenerator(){
		
	}
	public String generateToken(){
		SecureRandom prng;
		try {
			prng = SecureRandom.getInstance("SHA1PRNG");
			String randomNum = new Integer(prng.nextInt()).toString();
		    MessageDigest sha = MessageDigest.getInstance("SHA-1");
		    byte[] result =  sha.digest(randomNum.getBytes());
		    return toHex(result);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    return null;
	}
	
	static private String toHex(byte[] x){
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < x.length; i++) {
          sb.append(Integer.toString((x[i] & 0xff) + 0x100, 16).substring(1));
        }
	    return sb.toString();
	  }
}
