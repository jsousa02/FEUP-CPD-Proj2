package pt.up.fe.cpd.g13.server.game.matchmaking;

import pt.up.fe.cpd.g13.server.entity.User;

import java.util.*;

public class SimpleMatchmaking implements Matchmaking {
    private final int playersPerGame;
    private final LinkedList<User> queuedPlayers = new LinkedList<>();

    public SimpleMatchmaking(int playersPerGame) {
        this.playersPerGame = playersPerGame;
    }

    @Override
    public void register(User user) {
        if (queuedPlayers.contains(user)) return;
        queuedPlayers.add(user);
    }

    @Override
    public List<User> select() {
        if (queuedPlayers.size() < playersPerGame)
            return null;

        var group = new ArrayList<User>(playersPerGame);
        while (group.size() < playersPerGame) {
            var nextPlayer = queuedPlayers.removeFirst();
            group.add(nextPlayer);
        }

        return group;
    }

    @Override
    public void onGameFinished(List<? extends User> users, User winner) {

    }
}
