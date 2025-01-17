package dev.httpmarco.polocloud.plugin.fabric;

import dev.httpmarco.osgan.networking.ClassSupplier;
import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.polocloud.plugin.PluginPlatform;
import net.fabricmc.api.ModInitializer;

public final class FabricPlatformBootstrap implements ModInitializer, ClassSupplier {

    private final PluginPlatform platform = new PluginPlatform();

    @Override
    public void onInitialize() {
        platform.presentServiceAsOnline();

        CloudAPI.instance().classSupplier(this);
    }

    @Override
    public Class<?> classByName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
}
