package com.miaxis.btfingerprinterlib.utils.cipher;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class Encrypt {
	 /**
	   * Encrypts given data using session key, iv, aad
	   * @param cipherOperation - true for encrypt, false otherwise
	   * @param skey  - Session key
	   * @param iv    - initialization vector or nonce
	   * @param aad   - additional authenticated data
	   * @param data   - data to encrypt
	   * @return encrypted data
	   * @throws IllegalStateException
	   * @throws InvalidCipherTextException
	   */
	  public static byte[] encryptAES_GCM_NOPADDINGSessionKey(boolean cipherOperation, byte[] skey, 
			  byte[] iv, byte[] aad,
	      byte[] data) throws IllegalStateException, InvalidCipherTextException {
		int AUTH_TAG_SIZE_BITS = 128;
	    AEADParameters aeadParam = new AEADParameters(new KeyParameter(skey), AUTH_TAG_SIZE_BITS, iv, aad);
	    GCMBlockCipher gcmb = new GCMBlockCipher(new AESEngine());

	    gcmb.init(cipherOperation, aeadParam);
	    int outputSize = gcmb.getOutputSize(data.length);
	    byte[] result = new byte[outputSize];
	    int processLen = gcmb.processBytes(data, 0, data.length, result, 0);
	    gcmb.doFinal(result, processLen);

	    return result;
	  }
}


