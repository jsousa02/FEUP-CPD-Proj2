package pt.up.fe.cpd.g13.client.menu;

import pt.up.fe.cpd.g13.client.io.TerminalIO;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConnectionMenu extends Menu {

    private final Consumer<InetSocketAddress> onConnectionAddressEntered;

    public ConnectionMenu(TerminalIO term, Consumer<InetSocketAddress> onConnectionAddressEntered) {
        super(term);
        this.onConnectionAddressEntered = onConnectionAddressEntered;
    }

    @Override
    public void show() {
        var hostname = term.askText("Please input an IP address to connect to: ");
        var port = term.askInt("Please input a port to connect to: ");

        onConnectionAddressEntered.accept(new InetSocketAddress(hostname, port));
    }
}
