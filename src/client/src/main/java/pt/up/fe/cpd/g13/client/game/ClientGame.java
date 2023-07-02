package pt.up.fe.cpd.g13.client.game;

import pt.up.fe.cpd.g13.client.io.TerminalIO;
import pt.up.fe.cpd.g13.common.game.Game;

import java.util.*;

public class ClientGame extends Game {

    private final TerminalIO io;

    public ClientGame(TerminalIO io, int wordLength) {
        super(wordLength);
        this.io = io;
    }

    public char askNextChar() {
        return io.askChar("Your play: ", c -> Character.isAlphabetic(c) && canPlay(c));
    }

    public void show() {
        var failedTries = wrongCharacters.size();

        var rope = HangingRope.getHangingRopeForNumberOfFailedTries(failedTries);
        System.out.println(rope.indent(4));

        System.out.println();

        System.out.print("    ");
        for(char c : guessedWord) {
            System.out.print(c);
            System.out.print(' ');
        }
        System.out.println();

        System.out.println();
        System.out.printf("Number of tries left: %d%n", MAX_FAILED_TRIES - failedTries);
        System.out.print("Wrong characters: ");

        for (char c : wrongCharacters) {
            System.out.print(c);
            System.out.print(' ');
        }

        System.out.println();
    }
}
