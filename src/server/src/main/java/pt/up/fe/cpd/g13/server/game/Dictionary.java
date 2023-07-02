package pt.up.fe.cpd.g13.server.game;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Stream;

public class Dictionary {

    public static String getWord() {
        Random random = new Random();
        int n = random.nextInt(58100);

        try (var lines = new BufferedReader(new InputStreamReader(Dictionary.class.getResourceAsStream("/wordlist/words.txt")))) {
           return lines.lines().skip(n).findFirst().get();
        } catch(NoSuchElementException e) {
            System.out.println("Failed to get random word");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
