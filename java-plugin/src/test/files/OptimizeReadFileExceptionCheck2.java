package fr.cnumr.java.checks;

import java.util.Arrays;
import java.util.List;

class ReadFile {
    ReadFile(ReadFile readFile) {
    }

    public void readPreferences(String filename) {
        //...
        try (InputStream in = new FileInputStream(filename)) { // Noncompliant
            logger.log("my log");
        } catch (FileNotFoundException e) {
            logger.log(e);
        }
        //...
    }
}