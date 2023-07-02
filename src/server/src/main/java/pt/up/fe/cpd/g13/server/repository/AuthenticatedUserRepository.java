package pt.up.fe.cpd.g13.server.repository;

import pt.up.fe.cpd.g13.server.entity.AuthenticatedUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AuthenticatedUserRepository {

    private static AuthenticatedUserRepository instance = null;

    public static AuthenticatedUserRepository getInstance() {
        if (instance == null) instance = new AuthenticatedUserRepository();
        return instance;
    }

    private AuthenticatedUserRepository() {}

//    public void show() {
//        connectedUsers.forEach(user -> System.out.println(user.username() + " " + user.passwordHash()));
//    }

    private final List<AuthenticatedUser> connectedUsers = new ArrayList<>();

    public synchronized void forEach(Consumer<? super AuthenticatedUser> consumer) {
        connectedUsers.forEach(consumer);
    }

    public synchronized boolean addIfAbsent(AuthenticatedUser user) {
        if (findByUsername(user.username()).isPresent()) return false;

        connectedUsers.add(user);
        return true;
    }

    public synchronized boolean removeIf(Predicate<AuthenticatedUser> userPredicate) {
        connectedUsers.removeIf(userPredicate);
        return true;
    }

    public synchronized Optional<AuthenticatedUser> findByUsername(String username) {
        return connectedUsers.stream()
                .filter(user -> user.username().equals(username))
                .findAny();
    }

}
