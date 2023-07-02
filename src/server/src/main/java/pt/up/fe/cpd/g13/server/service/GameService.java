package pt.up.fe.cpd.g13.server.service;

import pt.up.fe.cpd.g13.common.executors.QueueingExecutor;
import pt.up.fe.cpd.g13.common.network.event.PacketListener;
import pt.up.fe.cpd.g13.common.network.packet.game.GameAbortPacket;
import pt.up.fe.cpd.g13.common.network.packet.game.GameStartPacket;
import pt.up.fe.cpd.g13.common.service.Service;
import pt.up.fe.cpd.g13.server.entity.AuthenticatedUser;
import pt.up.fe.cpd.g13.server.entity.User;
import pt.up.fe.cpd.g13.server.game.Dictionary;
import pt.up.fe.cpd.g13.server.game.ServerGame;
import pt.up.fe.cpd.g13.server.game.matchmaking.Matchmaking;
import pt.up.fe.cpd.g13.server.service.listener.GamePacketListener;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameService extends Service {

    private final QueueingExecutor executor = new QueueingExecutor();

    public GameService() {
        super(GameService.class);
    }

    @Override
    protected boolean tick() throws InterruptedException {
        logger.entering("GameService", "tick");
        executor.flush();

        return true;
    }

    public void startGame(Matchmaking matchmaking, List<AuthenticatedUser> users) {
        if (users.isEmpty())
            return;

        var word = Dictionary.getWord();
        if (word == null) {
            users.forEach(user -> {
                var handle = user.handle();
                handle.sendPacket(new GameAbortPacket());
                handle.disconnect();
            });

            return;
        }

        logger.config("Word is %s".formatted(word));

        var game = new ServerGame(word);
        var listener = new GamePacketListener(game, matchmaking, new ArrayList<>(users));
        listener.setExecutor(executor);

        users.forEach(user -> {
            var handle = user.handle();
            handle.setPacketListener(listener);
        });

        executor.execute(listener::start);
    }
}
