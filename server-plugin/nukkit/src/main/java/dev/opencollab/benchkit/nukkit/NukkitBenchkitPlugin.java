package dev.opencollab.benchkit.nukkit;

import java.net.InetSocketAddress;

import cn.nukkit.plugin.PluginBase;
import dev.opencollab.benchkit.BenchkitPlatform;
import dev.opencollab.benchkit.nukkit.command.BenchkitCommand;
import lombok.Getter;

@Getter
public class NukkitBenchkitPlugin extends PluginBase {
    private BenchkitPlatform platform;
    private InetSocketAddress address;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        address = new InetSocketAddress(getConfig().getString("address"), getConfig().getInt("port"));

        platform = new BenchkitPlatform(new NukkitBenchkitAdapter(this));
        platform.enable();

        getServer().getCommandMap().register("benchkit", new BenchkitCommand(this));
    }

    @Override
    public void onDisable() {
        platform.disable();
    }
}
