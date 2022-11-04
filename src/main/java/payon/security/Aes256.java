package payon.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

final class Aes256 {

    static byte[] _encrypt(byte[] input, byte[] passphrase, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(passphrase, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(input);
    }

    static byte[] _decrypt(byte[] input, byte[] passphrase, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(passphrase, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(input, 16, input.length -16);
    }
}
