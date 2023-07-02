package pt.up.fe.cpd.g13.client.menu;

import pt.up.fe.cpd.g13.client.io.TerminalIO;

import java.util.Scanner;

public abstract class Menu {

    protected final TerminalIO term;

    public Menu(TerminalIO term) {
        this.term = term;
    }

    public abstract void show();
}
