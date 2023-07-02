package pt.up.fe.cpd.g13.client.menu;

import pt.up.fe.cpd.g13.client.io.TerminalIO;
import pt.up.fe.cpd.g13.common.utils.Pair;

import java.util.Scanner;

public class MainMenu extends Menu {

    private final Menu connectionMenu;
    private final Runnable onExit;

    public MainMenu(TerminalIO term, Menu connectionMenu, Runnable onExit) {
        super(term);
        this.connectionMenu = connectionMenu;
        this.onExit = onExit;
    }

    @Override
    public void show() {
        term.askOption(
            """
            Welcome to HANGMAN!
                            
            What do you want to do?
            Please choose one of the following options.
            """.stripIndent(),

            Pair.of("Connect to a game server", () -> {
                connectionMenu.show();
                return null;
            }),
            Pair.of("Exit", () -> {
                onExit.run();
                return null;
            }));
    }
}
