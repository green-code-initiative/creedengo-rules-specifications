package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;
import org.sonar.plugins.java.api.JavaFileScanner;

class CookieWithExpirationCheckTest {

    /**
     * @formatter:off
     */
    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/CookieWithoutExpirationCheck.java")
                .withCheck(new CookieWithoutExpirationRule())
                .verifyIssues();
    }

}