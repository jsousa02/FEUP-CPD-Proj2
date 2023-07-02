package pt.up.fe.cpd.g13.server.service.listener;

import pt.up.fe.cpd.g13.common.network.PacketHandle;
import pt.up.fe.cpd.g13.common.network.event.PacketListener;
import pt.up.fe.cpd.g13.common.network.packet.game.*;
import pt.up.fe.cpd.g13.server.entity.AuthenticatedUser;
import pt.up.fe.cpd.g13.server.game.ServerGame;
import pt.up.fe.cpd.g13.server.game.matchmaking.Matchmaking;
import pt.up.fe.cpd.g13.server.repository.UserRepository;

import java.util.List;

public class GamePacketListener extends PacketListener {

    private final ServerGame game;
    private final List<AuthenticatedUser> users;
    private final Matchmaking matchmaking;

    private boolean isRunning = true;
    private int currentPlayerIndex = 0;

    public GamePacketListener(ServerGame game, Matchmaking matchmaking, List<AuthenticatedUser> users) {
        this.game = game;
        this.users = users;
        this.matchmaking = matchmaking;
    }

    public void start() {
        if (!isRunning) return;

        users.forEach(user -> {
            var handle = user.handle();
            handle.sendPacket(new GameStartPacket(game.getTargetWord().length));
        });

        requestPlay();
    }

    @Override
    public void onDisconnect(PacketHandle handle) {
        users.removeIf(user -> user.handle() == handle);
        if (!isRunning) return;

        if (users.size() < 2) {
            isRunning = false;

            users.forEach(user -> {
                var userHandle = user.handle();
                userHandle.sendPacket(new GameAbortPacket());
                userHandle.disconnect();
            });
        } else {
            currentPlayerIndex %= users.size();
        }
    }

    @Override
    public void onPlayResponsePacket(PacketHandle handle, PlayResponsePacket packet) {
        if (!isRunning) return;

        var currentPlayer = users.get(currentPlayerIndex);
        if (handle != currentPlayer.handle()) return;

        currentPlayerIndex = (currentPlayerIndex + 1) % users.size();

        var letter = packet.letterPlayed();
        game.play(letter);

        users.forEach(user -> {
             var userHandle = user.handle();
             userHandle.sendPacket(new GameUpdatePacket(game.getGuessedWord(), letter, currentPlayer.username()));
        });

        var won = game.isWon();
        var lost = game.isLost();

        if (!won && !lost) {
            requestPlay();
            return;
        }

        isRunning = false;

        matchmaking.onGameFinished(users, won ? currentPlayer : null);

        users.forEach(user -> {
            var userHandle = user.handle();
            userHandle.sendPacket(new GameEndPacket(won, game.getTargetWord(), currentPlayer.username()));
            userHandle.disconnect();
        });
    }

    private void requestPlay() {
        var currentPlayer = users.get(currentPlayerIndex);
        currentPlayer.handle()
                .sendPacket(new PlayRequestPacket());
    }
}
