package pt.up.fe.cpd.g13.client;

import pt.up.fe.cpd.g13.client.io.TerminalIO;
import pt.up.fe.cpd.g13.client.menu.ConnectionMenu;
import pt.up.fe.cpd.g13.client.menu.MainMenu;
import pt.up.fe.cpd.g13.common.network.NetworkService;
import pt.up.fe.cpd.g13.common.network.PacketHandle;
import pt.up.fe.cpd.g13.common.network.event.NetworkListener;
import pt.up.fe.cpd.g13.common.network.event.PacketListener;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthResponsePacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class Launcher {

    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration(Launcher.class.getResourceAsStream("/conf/logging.properties"));

        var scanner = new Scanner(System.in);
        var term = new TerminalIO(scanner);

        var menu = new MainMenu(term,
            new ConnectionMenu(term, (address) -> {
                var client = new Client(term, address);
                client.run();
            }),
            () -> {
                System.exit(0);
            }
        );

        menu.show();
    }
}
