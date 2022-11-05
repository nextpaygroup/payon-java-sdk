package payon.security;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class PayonSecurity {

    private final String passphrase;
    private static final byte[] SALTED = "Salted__".getBytes(US_ASCII);

    public PayonSecurity(String passphrase) {
        this.passphrase = passphrase;
    }

    /**
     *
     * @param plainText - plain text
     * @return Base64 of encrypted text
     * @throws Exception - Exception throws when call api get error
     */
    public String encrypt(String plainText) throws Exception
                                                     {
        byte[] salt = (new SecureRandom()).generateSeed(8);
        Object[] keyIv = deriveKeyAndIv(this.passphrase.getBytes(StandardCharsets.UTF_8), salt);

        byte[] saltAttach = concat(SALTED, salt);

        byte[] encrypted = Aes256._encrypt(plainText.getBytes(), ((byte[])keyIv[0]), ((byte[])keyIv[1]));
        return Base64.getEncoder().encodeToString(concat(saltAttach, encrypted));
    }

    /**
     * @param cipherText - encrypted text
     * @return plain text
     * @throws Exception - Exception throws when call api get error
     */
    public String decrypt(String cipherText) throws Exception {
        byte[] encrypted  = Base64.getDecoder().decode(cipherText);

        if(!Arrays.equals(Arrays.copyOfRange(encrypted, 0, 8),SALTED)){
            throw new IllegalArgumentException("Invalid crypted data");
        }

        byte[] salt = Arrays.copyOfRange(encrypted, 8, 16);
        Object[] keyIv = deriveKeyAndIv(this.passphrase.getBytes(StandardCharsets.UTF_8), salt);

        byte[] decrypted = Aes256._decrypt(encrypted, ((byte[])keyIv[0]), ((byte[])keyIv[1]));
        return new String(decrypted);
    }

    private static Object[] deriveKeyAndIv(byte[] passphrase, byte[] salt) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] passSalt = concat(passphrase, salt);
        byte[] dx = new byte[0];
        byte[] di = new byte[0];

        for(int i = 0; i < 3; ++i) {
            di = md5.digest(concat(di, passSalt));
            dx = concat(dx, di);
        }

        return new Object[]{Arrays.copyOfRange(dx, 0, 32), Arrays.copyOfRange(dx, 32, 48)};
    }

    /**
     *
     * @param input - String to encode
     * @return Base64 md5
     */
    public static String md5(String input) {
        byte[] byteData;
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byteData = instance.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // convert the byte to hex format
        String hexString = Convert.byteToHex(byteData);
        return hexString.toLowerCase();
    }

    private static byte[] concat(byte[] a, byte[] b)
    {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
