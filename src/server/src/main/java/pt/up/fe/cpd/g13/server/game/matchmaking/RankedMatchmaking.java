package pt.up.fe.cpd.g13.server.game.matchmaking;

import pt.up.fe.cpd.g13.server.entity.User;
import pt.up.fe.cpd.g13.server.repository.UserRepository;

import java.util.*;

public class RankedMatchmaking implements Matchmaking {

    private final int INITIAL_THRESHOLD = 10;
    private final int THRESHOLD_INCREASE = 5;
    private final long MILIS_UNTIL_INCREASE_THRESHOLD = 1000 * 10;

    private final List<User> queuedPlayers = new ArrayList<>();

    private long lastMatch = -1;

    private final int playersPerGame;

    public RankedMatchmaking(int playersPerGame) {
        this.playersPerGame = playersPerGame;
    }

    @Override
    public synchronized void register(User user) {
        if (queuedPlayers.contains(user)) return;
        queuedPlayers.add(user);
        queuedPlayers.sort(Comparator.comparingInt(User::rank));
    }

    @Override
    public synchronized List<User> select() {
        if (lastMatch == -1 || queuedPlayers.size() < playersPerGame) lastMatch = System.currentTimeMillis();

        var millisSinceLastIncrease = System.currentTimeMillis() - lastMatch;
        var currentThreshold = INITIAL_THRESHOLD + THRESHOLD_INCREASE * (millisSinceLastIncrease / MILIS_UNTIL_INCREASE_THRESHOLD);

        List<User> selectedPlayers = new ArrayList<>(playersPerGame);

        var indexOffset = playersPerGame - 1;

        var startIndex = 0;
        var endIndex = 0;
        var smallestDifference = Integer.MAX_VALUE;

        for (var i = 0; i < queuedPlayers.size() - indexOffset; i++) {
            var startRank = queuedPlayers.get(i).rank();
            var endRank = queuedPlayers.get(i + indexOffset).rank();
            var difference = endRank - startRank;

            if (difference < smallestDifference) {
                startIndex = i;
                endIndex = i + indexOffset;
                smallestDifference = difference;
            }
        }

        if (smallestDifference > currentThreshold)
            return null;

        for (int i = startIndex; i <= endIndex; i++) {
            selectedPlayers.add(queuedPlayers.get(startIndex));
            queuedPlayers.remove(startIndex);
        }

        lastMatch = System.currentTimeMillis();
        return selectedPlayers;
    }


    @Override
    public void onGameFinished(List<? extends User> users, User winner) {
        var repository = UserRepository.getInstance();

        for (User user : users) {
            if (user == winner) {
                repository.saveUser(winner.copyWithRank(winner.rank() + 10));
                continue;
            }

            repository.saveUser(user.copyWithRank(user.rank() - 5));
        }
    }
}
