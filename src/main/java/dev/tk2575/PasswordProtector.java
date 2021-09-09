package dev.tk2575;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class PasswordProtector {

	private static final String ALGORITHM = "AES";
	private static final int ITERATIONS = 2;
	private static final byte[] keyValue = new byte[]{'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

	public static String encrypt(String value, String salt) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);

		String valueToEnc = null;
		String eValue = value;
		for (int i = 0; i < ITERATIONS; i++) {
			valueToEnc = salt + eValue;
			byte[] encValue = c.doFinal(valueToEnc.getBytes());
			eValue = Base64.getEncoder().encodeToString(encValue);
		}
		return eValue;
	}

	public static String decrypt(String value, String salt) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, key);

		String dValue = null;
		String valueToDecrypt = value;
		for (int i = 0; i < ITERATIONS; i++) {
			byte[] decordedValue = Base64.getDecoder().decode(valueToDecrypt);
			byte[] decValue = c.doFinal(decordedValue);
			dValue = new String(decValue).substring(salt.length());
			valueToDecrypt = dValue;
		}
		return dValue;
	}

	private static Key generateKey() throws Exception {
		return new SecretKeySpec(keyValue, ALGORITHM);
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException("Expecting two arguments: password and salt");
		}

		String password = args[0];
		String salt = args[1];

		if (password == null || password.isBlank() || salt == null || salt.isBlank()) {
			throw new IllegalArgumentException("password and salt arguments cannot be null or empty");
		}

		String passwordEnc = PasswordProtector.encrypt(password, salt);
		String passwordDec = PasswordProtector.decrypt(passwordEnc, salt);

		System.out.println("Plain Text : " + password);
		System.out.println("Encrypted : " + passwordEnc);
		System.out.println("Decrypted : " + passwordDec);
	}
}
