package pt.up.fe.cpd.g13.server.service;

import pt.up.fe.cpd.g13.common.executors.QueueingExecutor;
import pt.up.fe.cpd.g13.common.service.Service;
import pt.up.fe.cpd.g13.server.Server;
import pt.up.fe.cpd.g13.server.entity.AuthenticatedUser;
import pt.up.fe.cpd.g13.server.entity.User;
import pt.up.fe.cpd.g13.server.game.matchmaking.Matchmaking;
import pt.up.fe.cpd.g13.server.game.matchmaking.RankedMatchmaking;
import pt.up.fe.cpd.g13.server.game.matchmaking.SimpleMatchmaking;
import pt.up.fe.cpd.g13.server.repository.AuthenticatedUserRepository;

import java.util.ArrayList;
import java.util.Optional;

public class MatchmakingService extends Service {

    private final QueueingExecutor executor = new QueueingExecutor();
    private final Server server;
    private final Matchmaking queue;

    public MatchmakingService(Server server, int players, Matchmaking matchmakingType) {
        super(MatchmakingService.class);
        this.server = server;
        this.queue = matchmakingType;
    }

    @Override
    protected boolean tick() throws InterruptedException {
//        logger.entering("MatchmakingService", "tick");

        executor.flush(2000);

        var group = queue.select();
        if (group == null)
            return true;

        logger.config("Found group");

        var playerList = AuthenticatedUserRepository.getInstance();

        var connectionGroup = group.stream()
                .map(user -> playerList.findByUsername(user.username()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        var gameService = server.getGameService();
        gameService.startGame(queue, connectionGroup);

        return true;
    }

    public void queuePlayer(AuthenticatedUser user) {
        executor.execute(() -> queue.register(user));
    }
}
