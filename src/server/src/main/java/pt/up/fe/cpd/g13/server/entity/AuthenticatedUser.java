package pt.up.fe.cpd.g13.server.entity;

import pt.up.fe.cpd.g13.common.network.PacketHandle;

public class AuthenticatedUser extends User {

    private final PacketHandle handle;

    public AuthenticatedUser(User user, PacketHandle handle) {
        super(user.username(), user.passwordHash(), user.rank());
        this.handle = handle;
    }

    public PacketHandle handle() {
        return handle;
    }
}
