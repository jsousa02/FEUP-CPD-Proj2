package pt.up.fe.cpd.g13.server.game;

import pt.up.fe.cpd.g13.common.game.Game;

import java.util.Arrays;

public class ServerGame extends Game {

    private final char[] targetWord;

    public ServerGame(String targetWord) {
        super(targetWord.length());

        this.targetWord = targetWord.toCharArray();
    }

    public boolean isWon() {
        return Arrays.equals(targetWord, guessedWord);
    }

    public boolean isLost() {
        return wrongCharacters.size() == MAX_FAILED_TRIES;
    }

    public void play(char guessedCharacter) {
        boolean isInTargetWord = false;
        for (int i = 0; i < targetWord.length; i++){
            char targetCharacter = targetWord[i];

            if (targetCharacter == guessedCharacter) {
                isInTargetWord = true;
                guessedWord[i] = targetCharacter;
            }
        }

        if (!isInTargetWord)
            wrongCharacters.add(guessedCharacter);
    }

    public char[] getTargetWord() {
        return targetWord;
    }
}
