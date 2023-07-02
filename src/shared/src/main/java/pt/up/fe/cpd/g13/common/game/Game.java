package pt.up.fe.cpd.g13.common.game;

import java.util.*;

public class Game {
    protected static final int MAX_FAILED_TRIES = 6; // Original

    protected final Set<Character> wrongCharacters = new TreeSet<>();
    protected final char[] guessedWord;

    public Game(int wordLength) {
        this.guessedWord = new char[wordLength];
        Arrays.fill(guessedWord, '_');
    }
    
    public char[] getGuessedWord() {
        return guessedWord;
    }

    public void setGuessedWord(char[] guessedWord) {
        System.arraycopy(guessedWord, 0, this.guessedWord, 0, this.guessedWord.length);
    }

    public void addWrongCharacter(char character) {
        wrongCharacters.add(character);
    }

    public boolean canPlay(char guessedCharacter) {
        if (wrongCharacters.contains(guessedCharacter))
            return false;

        for (char c : guessedWord) {
            if (c == guessedCharacter)
                return false;
        }

        return true;
    }
}
