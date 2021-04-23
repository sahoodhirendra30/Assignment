package com.dhirendra.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;

import com.dhirendra.exception.PaymentException;

public class PaymentCertUtil {

	static String tmpName, name = "";

	/**
	 * 
	 * @param certs
	 * @return
	 * @throws PaymentException
	 */
	public static String getCN(X509Certificate certs[]) throws PaymentException

	{
		X509Certificate principalCert = null;
		principalCert = certs[0];

		// Get the Distinguished Name from the certificate
		Principal principal = principalCert.getSubjectDN();

		// Extract the common name (CN)
		int start = principal.getName().indexOf("CN");

		if (start > 0) {
			tmpName = principal.getName().substring(start + 3);
			int end = tmpName.indexOf(",");
			if (end > 0) {
				name = tmpName.substring(0, end);
			} else {
				name = tmpName;
			}
		}
		return name;

	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */

	public static KeyPair generateKeyPair() throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom());
		KeyPair pair = generator.generateKeyPair();

		return pair;
	}

	/**
	 * 
	 * read keys from the store
	 * 
	 * @return
	 * @throws Exception
	 */
	public static KeyPair getKeyPairFromKeyStore() throws Exception {
		InputStream ins = PaymentCertUtil.class.getResourceAsStream("/keystore.jks");
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		keyStore.load(ins, "s3cr3t".toCharArray()); // Keystore password
		KeyStore.PasswordProtection keyPassword = // Key password
				new KeyStore.PasswordProtection("s3cr3t".toCharArray());
		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("mykey", keyPassword);
		java.security.cert.Certificate cert = keyStore.getCertificate("mykey");
		PublicKey publicKey = cert.getPublicKey();
		PrivateKey privateKey = privateKeyEntry.getPrivateKey();
		return new KeyPair(publicKey, privateKey);
	}

	/**
	 * Sign the message with private key
	 * 
	 * @param plainText
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String sign(String plainText, PrivateKey privateKey) throws Exception {
		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(privateKey);
		privateSignature.update(plainText.getBytes(UTF_8));
		byte[] signature = privateSignature.sign();
		return Base64.getEncoder().encodeToString(signature);
	}

	/**
	 * 
	 * Verify signature with public key
	 * 
	 * @param plainText
	 * @param signature
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
		Signature publicSignature = Signature.getInstance("SHA256withRSA");
		publicSignature.initVerify(publicKey);
		publicSignature.update(plainText.getBytes(UTF_8));
		byte[] signatureBytes = Base64.getDecoder().decode(signature);
		return publicSignature.verify(signatureBytes);
	}

	/**
	 * 
	 * 
	 * @param plainText
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */

	public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));
		return Base64.getEncoder().encodeToString(cipherText);
	}

	/**
	 * 
	 * @param cipherText
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
		byte[] bytes = Base64.getDecoder().decode(cipherText);
		Cipher decriptCipher = Cipher.getInstance("RSA");
		decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);
		return new String(decriptCipher.doFinal(bytes), UTF_8);
	}

	/**
	 * convert String to publicKey
	 * 
	 * @param certificate
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	public static PublicKey stringToPublicKeyConverter(String certificate)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		String publicKeyContent = certificate.replaceAll("\\n", "").replace("-----BEGIN CERTIFICATE-----", "")
				.replace("-----END CERTIFICATE-----", "");
		KeyFactory kf = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
		RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
		return pubKey;
	}

	/**
	 * 
	 * String to private key
	 * 
	 * @param certificate
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	public static PrivateKey stringToPrivateKeyConverter(String key)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		String privateKeyContent = key.replaceAll("\\n", "").replace("-----BEGIN RSA PRIVATE KEY-----", "")
				.replace("-----BEGIN RSA PRIVATE KEY-----", "");
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
		PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
		return privKey;
	}

	public static String signSHA256RSA(Object obj, String privateKey) throws Exception {
		// Remove markers and new line characters in private key
		String realPrivateKey = privateKey.replaceAll("-----END PRIVATE KEY-----", "")
				.replaceAll("-----BEGIN PRIVATE KEY-----", "").replaceAll("\n", "");

		byte[] b1 = Base64.getDecoder().decode(realPrivateKey);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
		KeyFactory kf = KeyFactory.getInstance("RSA");

		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(kf.generatePrivate(spec));
		privateSignature.update(((String) obj).getBytes("UTF-8"));
		byte[] s = privateSignature.sign();
		return Base64.getEncoder().encodeToString(s);
	}

	public static String signSHA256WithRSA(String privateKey, Map<String, String> headers, String requestBody)
			throws Exception {
		// Remove markers and new line characters in private key
		String realPrivateKey = privateKey.replaceAll("-----END PRIVATE KEY-----", "")
				.replaceAll("-----BEGIN PRIVATE KEY-----", "").replaceAll("\n", "");

		byte[] b1 = Base64.getDecoder().decode(realPrivateKey);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
		KeyFactory kf = KeyFactory.getInstance("RSA");

		MessageDigest hashString = MessageDigest.getInstance("SHA-256");
		byte[] digestBytes = hashString.digest(requestBody.getBytes());
		// Creating the BASE64 String
		String digestString = Base64.getEncoder().encodeToString(digestBytes);
		// Creating the Digest

		String digest = "SHA-256=" + digestString;

		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(kf.generatePrivate(spec));
		privateSignature.update((requestBody).getBytes("UTF-8"));
		byte[] s = privateSignature.sign();
		return Base64.getEncoder().encodeToString(s);
	}

}
