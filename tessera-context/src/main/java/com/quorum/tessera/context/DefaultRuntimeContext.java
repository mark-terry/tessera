package com.quorum.tessera.context;

import com.quorum.tessera.config.keys.KeyEncryptor;
import com.quorum.tessera.encryption.PublicKey;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.util.List;
import java.util.Set;

class DefaultRuntimeContext implements RuntimeContext {

    private final Set<PublicKey> keys;

    private final KeyEncryptor keyEncryptor;

    private final List<PublicKey> alwaysSendTo;

    private final List<URI> peers;

    private final Client p2pClient;

    private final boolean remoteKeyValidation;

    private final boolean enhancedPrivacy;

    private final URI p2pServerUri;

    private final boolean disablePeerDiscovery;

    private final boolean useWhiteList;

    private final boolean recoveryMode;

    protected DefaultRuntimeContext(
            Set<PublicKey> keys,
            KeyEncryptor keyEncryptor,
            List<PublicKey> alwaysSendTo,
            List<URI> peers,
            Client p2pClient,
            boolean remoteKeyValidation,
            boolean enhancedPrivacy,
            URI p2pServerUri,
            boolean disablePeerDiscovery,
            boolean useWhiteList,
            boolean recoveryMode) {
        this.keys = keys;
        this.keyEncryptor = keyEncryptor;
        this.alwaysSendTo = List.copyOf(alwaysSendTo);
        this.peers = List.copyOf(peers);
        this.p2pClient = p2pClient;
        this.remoteKeyValidation = remoteKeyValidation;
        this.enhancedPrivacy = enhancedPrivacy;
        this.p2pServerUri = p2pServerUri;
        this.disablePeerDiscovery = disablePeerDiscovery;
        this.useWhiteList = useWhiteList;
        this.recoveryMode = recoveryMode;
    }

    public Set<PublicKey> getKeys() {
        return Set.copyOf(keys);
    }

    public KeyEncryptor getKeyEncryptor() {
        return keyEncryptor;
    }

    public List<PublicKey> getAlwaysSendTo() {
        return alwaysSendTo;
    }

    public List<URI> getPeers() {
        return peers;
    }

    public Client getP2pClient() {
        return p2pClient;
    }

    public boolean isRemoteKeyValidation() {
        return remoteKeyValidation;
    }

    @Override
    public boolean isEnhancedPrivacy() {
        return enhancedPrivacy;
    }

    public URI getP2pServerUri() {
        return p2pServerUri;
    }

    @Override
    public boolean isDisablePeerDiscovery() {
        return disablePeerDiscovery;
    }

    @Override
    public boolean isUseWhiteList() {
        return useWhiteList;
    }

    public boolean isRecoveryMode() {
        return recoveryMode;
    }

    @Override
    public Set<PublicKey> getPublicKeys() {
        return Set.copyOf(getKeys());
    }
    @Override
    public String toString() {
        return "RuntimeContext{"
                + "keys="
                + keys
                + ", keyEncryptor="
                + keyEncryptor
                + ", alwaysSendTo="
                + alwaysSendTo
                + ", peers="
                + peers
                + ", p2pClient="
                + p2pClient
                + ", remoteKeyValidation="
                + remoteKeyValidation
                + ", enhancedPrivacy="
                + enhancedPrivacy
                + ", p2pServerUri="
                + p2pServerUri
                + ", disablePeerDiscovery="
                + disablePeerDiscovery
                + ", useWhiteList="
                + useWhiteList
                + ", recoveryMode="
                + recoveryMode
                + '}';
    }
}
