package payon.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PayonSecurityTest {

    @Test
    public void test1() throws Exception {
        PayonSecurity payonSecurity = new PayonSecurity("SRsreL2f7AEgWsFGoyccNa");
        String plainText = "U2FsdGVkX18Kp+T3M9VajicIO9WGQQuAlMscLGiTnVyHRj2jHObWshzJXQ6RpJtW";
        String encrypted = payonSecurity.encrypt(plainText);
        assertEquals(plainText, payonSecurity.decrypt(encrypted));
    }

    @Test
    public void test2() throws Exception {
        PayonSecurity payonSecurity = new PayonSecurity("password");
        String plainText = "Test!";
        String encrypted = payonSecurity.encrypt(plainText);
        assertEquals(plainText, payonSecurity.decrypt(encrypted));
    }

    @Test
    public void test3() throws Exception {
        PayonSecurity payonSecurity = new PayonSecurity("password");
        String plainText = "1234567890";
        String encrypted = payonSecurity.encrypt(plainText);
        assertEquals(plainText, payonSecurity.decrypt(encrypted));
    }
}
