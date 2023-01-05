package fr.cnumr.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class InitializeBufferWithAppropriateSizeTest {

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/InitializeBufferWithAppropriateSize.java")
                .withCheck(new InitializeBufferWithAppropriateSize())
                .verifyIssues();
    }

}