package dev.opencollab.benchkit.geyser;

import dev.opencollab.benchkit.BenchkitPlatform;
import lombok.Getter;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;

import java.net.InetSocketAddress;

@Getter
public class GeyserBenchkitExtension implements Extension {
    private BenchkitPlatform platform;
    private InetSocketAddress address;

    @Subscribe
    public void onPostInitialize(GeyserPostInitializeEvent event) {
        BenchkitConfiguration config = ConfigLoader.loadConfig(this);

        if (config == null) {
            logger().error("Failed to load config, extension will not start!");
            this.disable();
            return;
        }

        address = new InetSocketAddress(config.getAddress(), config.getPort());
        platform = new BenchkitPlatform(new GeyserBenchkitAdapter(this, config));
        platform.enable();
    }
}
