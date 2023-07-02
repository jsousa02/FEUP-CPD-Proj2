package pt.up.fe.cpd.g13.client.menu;

import pt.up.fe.cpd.g13.client.io.TerminalIO;
import pt.up.fe.cpd.g13.client.model.UserCredentials;

import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AuthenticationMenu extends Menu {

    private final Consumer<UserCredentials> onCredentialsEntered;

    public AuthenticationMenu(TerminalIO term, Consumer<UserCredentials> onCredentialsEntered) {
        super(term);
        this.onCredentialsEntered = onCredentialsEntered;
    }

    @Override
    public void show() {
        var username = term.askText("Username: ");
        var password = term.askText("Password: ");

        onCredentialsEntered.accept(new UserCredentials(username, password));
    }

}
