package com.miaxis.btfingerprinterlib.utils.cipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//http://blog.csdn.net/catoop/article/details/47757705

/**
 * 3DES加密
 */
public class DES3 {
	private static final String Algorithm = "DESede"; // 定义 加密算法,可用
													  // DES,DESede,Blowfish
	/**
	 * 加密方法
	 * 
	 * @param keybyte
	 *            加密密钥，长度为24字节或16字节
	 * @param src
	 *            被加密的数据缓冲区（源）
	 * @return
	 */
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		byte[] keybyte24 = new byte[24];
		if (keybyte.length == 16) {
			System.arraycopy(keybyte, 0, keybyte24, 0, keybyte.length);
			System.arraycopy(keybyte, 0, keybyte24, 16, 8);
		}
		else if (keybyte.length == 24) {
			System.arraycopy(keybyte, 0, keybyte24, 0, keybyte.length);
		}else{
			return null;
		}
		
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

			// 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 *
	 * @param keybyte
	 *            加密密钥，长度为24字节或16字节
	 * @param src
	 *            加密后的缓冲区
	 * @return
	 */
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {

		byte[] keybyte24 = new byte[24];
		if (keybyte.length == 16) {
			System.arraycopy(keybyte, 0, keybyte24, 0, keybyte.length);
			System.arraycopy(keybyte, 0, keybyte24, 16, 8);
		}
		else if (keybyte.length == 24) {
			System.arraycopy(keybyte, 0, keybyte24, 0, keybyte.length);
		}else{
			return null;
		}

		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

}