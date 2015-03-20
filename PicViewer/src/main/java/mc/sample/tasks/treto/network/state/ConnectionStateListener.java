package mc.sample.tasks.treto.network.state;

public interface ConnectionStateListener {
    void onNetworkConnected();
    void onNetworkDisconnected();
}
