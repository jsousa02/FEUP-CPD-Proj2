package pt.up.fe.cpd.g13.server.service;

import pt.up.fe.cpd.g13.common.executors.QueueingExecutor;
import pt.up.fe.cpd.g13.common.network.PacketHandle;
import pt.up.fe.cpd.g13.common.network.event.PacketListener;
import pt.up.fe.cpd.g13.common.service.Service;
import pt.up.fe.cpd.g13.server.Server;
import pt.up.fe.cpd.g13.server.entity.AuthenticatedUser;
import pt.up.fe.cpd.g13.server.repository.UserRepository;
import pt.up.fe.cpd.g13.server.service.listener.AuthPacketListener;

public class AuthService extends Service {

    private final QueueingExecutor executor = new QueueingExecutor();

    public AuthService() {
        super(AuthService.class);
    }

    @Override
    protected boolean tick() throws InterruptedException {
        logger.entering("AuthService", "tick");
        executor.flush();

        return true;
    }

    public PacketListener createListener(Server server) {
        var matchmaking = server.getMatchmakingService();

        var listener = new AuthPacketListener(user -> {
            var handle = user.handle();

            handle.setPacketListener(null);
            matchmaking.queuePlayer(user);
        });

        listener.setExecutor(executor);

        return listener;
    }
}
