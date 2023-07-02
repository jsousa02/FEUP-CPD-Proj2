package pt.up.fe.cpd.g13.server.util;

import org.mindrot.jbcrypt.BCrypt;
import pt.up.fe.cpd.g13.server.entity.User;
import pt.up.fe.cpd.g13.server.repository.UserRepository;

import java.util.Optional;

public class AuthUtils {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String hashed, String password) {
        return BCrypt.checkpw(password, hashed);
    }

    public static Optional<User> findUserOrCreate(UserRepository users, String username, String password) {
        var user = users.findUserByUsername(username)
                .orElseGet(() -> {
                    var createdUser = new User(username, hashPassword(password), 0);
                    users.saveUser(createdUser);

                    return createdUser;
                });

        return checkPassword(user.passwordHash(), password)
                ? Optional.of(user) : Optional.empty();
    }
}
