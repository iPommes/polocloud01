package dev.httpmarco.polocloud.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.polocloud.api.events.service.CloudServiceOnlineEvent;
import dev.httpmarco.polocloud.api.events.service.CloudServiceShutdownEvent;
import dev.httpmarco.polocloud.api.packets.service.CloudServiceStateChangePacket;
import dev.httpmarco.polocloud.api.services.CloudService;
import dev.httpmarco.polocloud.api.services.ServiceFilter;
import dev.httpmarco.polocloud.api.services.ServiceState;
import dev.httpmarco.polocloud.runner.CloudInstance;
import lombok.Getter;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.List;

@Getter
@Plugin(
        id = "polocloud",
        name = "PoloCloud",
        version = "1.0.0",
        authors = "HttpMarco"
)
public final class VelocityPlatform {

    private final ProxyServer server;

    @Inject
    public VelocityPlatform(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        var instance = CloudAPI.instance();

        for (var registered : this.server.getAllServers()) {
            this.server.unregisterServer(registered.getServerInfo());
        }

        instance.globalEventNode().addListener(CloudServiceOnlineEvent.class, startEvent -> {
            if (startEvent.cloudService().group().platform().proxy()) {
                return;
            }
            server.registerServer(new ServerInfo(startEvent.cloudService().name(), new InetSocketAddress("127.0.0.1", startEvent.cloudService().port())));
        });

        instance.globalEventNode().addListener(CloudServiceShutdownEvent.class, shutdownEvent ->
                server.getServer(shutdownEvent.cloudService().name()).ifPresent(registeredServer ->
                        server.unregisterServer(registeredServer.getServerInfo())));

        for (var service : instance.serviceProvider().filterService(ServiceFilter.SERVERS)) {
            server.registerServer(new ServerInfo(service.name(), new InetSocketAddress("127.0.0.1", service.port())));
        }
        //todo duplicated code
        CloudInstance.instance().client().transmitter().sendPacket(new CloudServiceStateChangePacket(CloudInstance.SERVICE_ID, ServiceState.ONLINE));
    }

    @Subscribe
    public void onProxyInitialize(PlayerChooseInitialServerEvent event) {

        var service = CloudAPI.instance().serviceProvider().filterService(ServiceFilter.LOWEST_FALLBACK);

        if (service.isEmpty()) {
            event.setInitialServer(null);
            return;
        }

        server.getServer(service.get(0).name()).ifPresentOrElse(event::setInitialServer, () -> event.setInitialServer(null));
    }
}
