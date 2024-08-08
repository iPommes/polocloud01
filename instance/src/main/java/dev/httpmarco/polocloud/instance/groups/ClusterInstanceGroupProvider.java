package dev.httpmarco.polocloud.instance.groups;

import dev.httpmarco.osgan.networking.packet.PacketBuffer;
import dev.httpmarco.polocloud.api.groups.ClusterGroup;
import dev.httpmarco.polocloud.api.groups.ClusterGroupProvider;
import dev.httpmarco.polocloud.api.packet.resources.group.GroupCollectionPacket;
import dev.httpmarco.polocloud.api.platforms.PlatformGroupDisplay;
import dev.httpmarco.polocloud.instance.ClusterInstance;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ClusterInstanceGroupProvider extends ClusterGroupProvider {

    @Override
    public @NotNull CompletableFuture<Set<ClusterGroup>> groupsAsync() {
        var future = new CompletableFuture<Set<ClusterGroup>>();
        ClusterInstance.instance().client().request("groups-all", GroupCollectionPacket.class, it -> future.complete(it.groups()));
        return future;
    }

    @Override
    public CompletableFuture<Boolean> existsAsync(String group) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<String>> deleteAsync(String group) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<String>> createAsync(String name, String[] nodes, PlatformGroupDisplay platform, int minMemory, int maxMemory, boolean staticService, int minOnline, int maxOnline) {
        return null;
    }

    @Override
    public CompletableFuture<ClusterGroup> findAsync(@NotNull String group) {
        return null;
    }

    @Override
    public void reload() {

    }

    @Contract("_ -> new")
    @Override
    public @NotNull ClusterGroup read(@NotNull PacketBuffer buffer) {
        var name = buffer.readString();
        var minMemory = buffer.readInt();
        var maxMemory = buffer.readInt();
        var minOnlineServerInstances = buffer.readInt();
        var maxOnlineServerInstances = buffer.readInt();
        var staticService = buffer.readBoolean();
        var platform = new PlatformGroupDisplay(buffer.readString(), buffer.readString());
        var amountOfNodes = buffer.readInt();
        var nodes = new String[amountOfNodes];

        for (int i = 0; i < amountOfNodes; i++) {
            nodes[i] = buffer.readString();
        }

        return new ClusterInstanceGroupImpl(name, platform, nodes, minMemory, maxMemory, staticService, minOnlineServerInstances, maxOnlineServerInstances);
    }
}
