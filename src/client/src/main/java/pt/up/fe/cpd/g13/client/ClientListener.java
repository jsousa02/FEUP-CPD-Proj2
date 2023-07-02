package pt.up.fe.cpd.g13.client;

import pt.up.fe.cpd.g13.client.game.ClientGame;
import pt.up.fe.cpd.g13.client.io.TerminalIO;
import pt.up.fe.cpd.g13.client.menu.AuthenticationMenu;
import pt.up.fe.cpd.g13.client.model.UserCredentials;
import pt.up.fe.cpd.g13.common.network.PacketHandle;
import pt.up.fe.cpd.g13.common.network.event.PacketListener;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthRequestPacket;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthResponsePacket;
import pt.up.fe.cpd.g13.common.network.packet.game.*;

import java.util.Arrays;

public class ClientListener extends PacketListener {

    private final TerminalIO term;
    private UserCredentials currentCredentials = null;
    private ClientGame currentGame = null;

    public ClientListener(TerminalIO term) {
        this.term = term;
    }

    public void sendLoginCredentials(PacketHandle handle) {
        if (currentCredentials == null) {
            var credentialsMenu = new AuthenticationMenu(term, credentials -> currentCredentials = credentials);
            credentialsMenu.show();
        }

        handle.sendPacket(new AuthRequestPacket(currentCredentials.username(), currentCredentials.password()));
    }

    @Override
    public void onAuthResponsePacket(PacketHandle handle, AuthResponsePacket packet) {
        if (packet.state() == AuthResponsePacket.UserState.REFUSED) {
            currentCredentials = null;
            System.out.println("Incorrect credentials, please try again!");
            sendLoginCredentials(handle);

            return;
        }

        System.out.println("You have been successfully logged in!");
        System.out.println("Please wait while a game is being created...");
    }

    @Override
    public void onGameStartPacket(PacketHandle handle, GameStartPacket packet) {
        currentGame = new ClientGame(term, packet.wordLength());
        currentGame.show();
    }

    @Override
    public void onPlayRequestPacket(PacketHandle handle, PlayRequestPacket packet) {
        char play = currentGame.askNextChar();
        handle.sendPacket(new PlayResponsePacket(play));
    }

    @Override
    public void onGameUpdatePacket(PacketHandle handle, GameUpdatePacket packet) {
        if (Arrays.equals(currentGame.getGuessedWord(), packet.updatedWord())) {
            currentGame.addWrongCharacter(packet.letterPlayed());
        } else {
            currentGame.setGuessedWord(packet.updatedWord());
        }

        currentGame.show();
    }

    @Override
    public void onGameEndPacket(PacketHandle handle, GameEndPacket packet) {
        currentGame = null;

        if (packet.won()) {
            System.out.printf("The game has ended, %s won!%n", packet.currentPlayerUsername());
        } else {
            System.out.println("The game has ended, everyone lost!");
        }

        System.out.println("You will be disconnected shortly...");
    }

    public void onGameAbortPacket(PacketHandle handle, GameAbortPacket packet) {
        currentGame = null;

        System.out.println("The game has been aborted, you will be disconnected shortly...");
    }
}
