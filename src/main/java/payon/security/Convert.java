package payon.security;

import java.math.BigInteger;

final class Convert {

    public static String byteToHex(byte[] from) {
        BigInteger bigInteger = new BigInteger(1, from);
        return bigInteger.toString(16);
    }
}
