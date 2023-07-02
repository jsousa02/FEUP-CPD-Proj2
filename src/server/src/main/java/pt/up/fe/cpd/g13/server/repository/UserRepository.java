package pt.up.fe.cpd.g13.server.repository;

import pt.up.fe.cpd.g13.server.entity.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class UserRepository implements AutoCloseable {

    private static UserRepository instance;

    public synchronized static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }


    private static final Path filePath = Path.of("data", "users.txt");
    private final Map<String, User> users = new TreeMap<>();

    private UserRepository() {
        try (var database = Files.lines(filePath)) {
            database
                .map(userLine -> {
                    String[] userData = userLine.split(";");
                    return new User(userData[0], userData[1], Integer.parseInt(userData[2]));
                })
                .forEach(user -> users.put(user.username(), user));
        } catch (IOException ignored) {}
    }

    public synchronized Optional<User> findUserByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public synchronized void saveUser(User user) {
        users.put(user.username(), user);
    }

    @Override
    public void close() {
        try {
            Files.createDirectories(filePath.getParent());

            var openOptions = new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE};
            try (var writer = Files.newBufferedWriter(filePath, openOptions)) {

                users.forEach((username, user) -> {
                    try {
                        writer.write("%s;%s;%d\n".formatted(user.username(), user.passwordHash(), user.rank()));
                    } catch (IOException ignored) {}
                });
            }
        } catch (IOException ignored) {}


        System.out.println(filePath.toAbsolutePath());
    }
}
