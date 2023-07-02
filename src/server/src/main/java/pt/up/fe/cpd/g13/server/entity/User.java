package pt.up.fe.cpd.g13.server.entity;

import java.util.Objects;

public class User {

    private final String username;
    private final String passwordHash;
    private final int rank;

    public User(String username, String passwordHash, int rank) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.rank = rank;
    }

    public String username() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public int rank() {
        return rank;
    }

    public User copyWithRank(int newRank) {
        return new User(username, passwordHash, newRank);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return rank == user.rank && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, rank);
    }
}
