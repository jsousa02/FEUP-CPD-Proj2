package pt.up.fe.cpd.g13.server.game.matchmaking;

import pt.up.fe.cpd.g13.server.entity.AuthenticatedUser;
import pt.up.fe.cpd.g13.server.entity.User;

import java.util.Collection;
import java.util.List;

public interface Matchmaking {

    void register(User username);

    List<User> select();

    void onGameFinished(List<? extends User> users, User winner);
}
