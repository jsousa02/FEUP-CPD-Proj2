package pt.up.fe.cpd.g13.server.service.listener;

import pt.up.fe.cpd.g13.common.network.PacketHandle;
import pt.up.fe.cpd.g13.common.network.event.PacketListener;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthRequestPacket;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthResponsePacket;
import pt.up.fe.cpd.g13.server.entity.AuthenticatedUser;
import pt.up.fe.cpd.g13.server.repository.AuthenticatedUserRepository;
import pt.up.fe.cpd.g13.server.repository.UserRepository;
import pt.up.fe.cpd.g13.server.service.AuthService;
import pt.up.fe.cpd.g13.server.util.AuthUtils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class AuthPacketListener extends PacketListener {

    private final Logger logger = Logger.getLogger(AuthService.class.getName());

    private final Consumer<AuthenticatedUser> setNextListener;

    public AuthPacketListener(Consumer<AuthenticatedUser> setNextListener) {
        this.setNextListener = setNextListener;
    }

    @Override
    public void onAuthRequestPacket(PacketHandle handle, AuthRequestPacket packet) {
        logger.entering("AuthPacketListener", "onAuthRequestPacket", packet);

        var users = UserRepository.getInstance();

        var username = packet.username();
        var password = packet.password();

        var user = AuthUtils.findUserOrCreate(users, username, password)
                .map(foundUser -> new AuthenticatedUser(foundUser, handle))
                .orElse(null);

        var playerList = AuthenticatedUserRepository.getInstance();

        AuthResponsePacket responsePacket = user != null && playerList.addIfAbsent(user)
                ? new AuthResponsePacket(AuthResponsePacket.UserState.LOGGED_IN)
                : new AuthResponsePacket(AuthResponsePacket.UserState.REFUSED);

        if (responsePacket.state() == AuthResponsePacket.UserState.LOGGED_IN) {
            assert user != null;
            setNextListener.accept(user);
        }

        handle.sendPacket(responsePacket);
    }
}
