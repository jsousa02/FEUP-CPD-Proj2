package pt.up.fe.cpd.g13.client;

import pt.up.fe.cpd.g13.client.io.TerminalIO;
import pt.up.fe.cpd.g13.common.executors.QueueingExecutor;
import pt.up.fe.cpd.g13.common.network.NetworkService;
import pt.up.fe.cpd.g13.common.network.PacketHandle;
import pt.up.fe.cpd.g13.common.network.ReconnectException;
import pt.up.fe.cpd.g13.common.network.event.NetworkListener;
import pt.up.fe.cpd.g13.common.service.Service;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends Service implements NetworkListener {

    private final QueueingExecutor executor = new QueueingExecutor();
    private final ClientListener listener;

    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
    private NetworkService networkService;
    private final InetSocketAddress address;

    public Client(TerminalIO term, InetSocketAddress address) {
        super(Client.class);
        this.listener = new ClientListener(term);
        this.address = address;
    }

    @Override
    protected void runWithResources(Runnable service) throws Exception {
        networkService = NetworkService.connect(address);
        networkService.setNetworkListener(this);

        networkExecutor.submit(networkService);
        service.run();

        networkService.stop();
    }

    @Override
    protected boolean tick() throws InterruptedException {
        executor.flush();
        return true;
    }

    @Override
    public void onConnect(PacketHandle handle) {
        listener.setExecutor(executor);
        handle.setPacketListener(listener);

        listener.sendLoginCredentials(handle);
    }

    @Override
    public void onDisconnect(PacketHandle handle) {
        throw new ReconnectException();
    }
}
