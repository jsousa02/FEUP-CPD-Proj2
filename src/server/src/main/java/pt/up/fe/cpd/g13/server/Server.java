package pt.up.fe.cpd.g13.server;

import org.mindrot.jbcrypt.BCrypt;
import pt.up.fe.cpd.g13.common.network.NetworkService;
import pt.up.fe.cpd.g13.common.network.PacketHandle;
import pt.up.fe.cpd.g13.common.network.event.NetworkListener;
import pt.up.fe.cpd.g13.server.game.matchmaking.Matchmaking;
import pt.up.fe.cpd.g13.server.game.matchmaking.RankedMatchmaking;
import pt.up.fe.cpd.g13.server.game.matchmaking.SimpleMatchmaking;
import pt.up.fe.cpd.g13.server.repository.AuthenticatedUserRepository;
import pt.up.fe.cpd.g13.server.repository.UserRepository;
import pt.up.fe.cpd.g13.server.service.AuthService;
import pt.up.fe.cpd.g13.server.service.GameService;
import pt.up.fe.cpd.g13.server.service.MatchmakingService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class Server implements Runnable, NetworkListener {

    private static Server instance = null;

    public static Server getInstance() {
        return instance;
    }

    private final NetworkService networkService;
    private final AuthService authService;
    private final MatchmakingService matchmakingService;
    private final GameService gameService;

    private final ExecutorService threadPool;

    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration(Server.class.getResourceAsStream("/conf/logging.properties"));

        int numPlayers = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[2]);

        String matchmakingType = args[0];
        Matchmaking matchmaking;

        if (matchmakingType.equals("-r")) {
            matchmaking = new RankedMatchmaking(numPlayers);
        } else if (matchmakingType.equals("-s")) matchmaking = new SimpleMatchmaking(numPlayers);
        else throw new IllegalArgumentException("Invalid game mode");

        instance = new Server(port, numPlayers, matchmaking);
        instance.run();
    }

    private Server(int port, int players, Matchmaking matchmakingType) {
        this.networkService = NetworkService.bind(new InetSocketAddress(port));
        this.networkService.setNetworkListener(this);

        this.authService = new AuthService();
        this.matchmakingService = new MatchmakingService(this, players, matchmakingType);
        this.gameService = new GameService();

        this.threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
    }

    @Override
    public void run() {
        threadPool.execute(networkService);
        threadPool.execute(authService);
        threadPool.execute(matchmakingService);
        threadPool.execute(gameService);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            UserRepository.getInstance()
                    .close();
        }));
    }

    @Override
    public void onConnect(PacketHandle handle) {
        var listener = authService.createListener(this);
        handle.setPacketListener(listener);
    }

    @Override
    public void onDisconnect(PacketHandle handle) {
        var playerList = AuthenticatedUserRepository.getInstance();
        playerList.removeIf(user -> user.handle() == handle);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    public MatchmakingService getMatchmakingService() {
        return matchmakingService;
    }

    public GameService getGameService() {
        return gameService;
    }
}
