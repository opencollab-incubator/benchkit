package dev.opencollab.benchkit.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import dev.opencollab.benchkit.nukkit.NukkitBenchkitPlugin;

public class BenchkitCommand extends Command {
    private final NukkitBenchkitPlugin plugin;

    public BenchkitCommand(NukkitBenchkitPlugin plugin) {
        super("benchkit", "Benchkit Commands");
        this.plugin = plugin;

        this.commandParameters.put("default", new CommandParameter[] {
                new CommandParameter("version"),
        });
        this.commandParameters.put("spawnhumanoid", new CommandParameter[] {
                new CommandParameter("spawnhumanoid")
        });
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("must be player");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("help todo");
            return true;
        }
        if (args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(TextFormat.GRAY + "benchkit by lukeeey");
            return true;
        }
        if (args[0].equalsIgnoreCase("spawnhumannoid")) {
            EntityHuman human = spawnHumanoid(player);
            sender.sendMessage("Human spawned! UUID: " + TextFormat.GOLD + human.getUniqueId());
            return true;
        }
        return true;
    }

    private EntityHuman spawnHumanoid(Player player) {
        Position position = player.getPosition();

        CompoundTag tag = Entity.getDefaultNBT(position)
                .putBoolean("Invulnerable", true)
                .putBoolean("npc", true)
                .putCompound("Skin", new CompoundTag()
                        .putString("ModelId", "7b21-4637-9b63-8ad63622ef01.Custom58af4afb-de8e-3249-a7c3-f1cfe27d2acf")
                        .putByteArray("Data", player.getSkin().getSkinData().data)
                        .putInt("CapeImageWidth", 0)
                        .putInt("CapeImageHeight", 0)
                        .putInt("SkinImageHeight", 64)
                        .putInt("SkinImageWidth", 64)
                        .putString("CapeId", "")
                        .putByteArray("CapeData", new byte[0])
                        .putByteArray("GeometryData", player.getSkin().getGeometryData().getBytes())
                        .putString("GeometryName", "geometry.humanoid.custom")
                        .putBoolean("PremiumSkin", false)
                        .putBoolean("PersonaSkin", false)
                        .putBoolean("CapeOnClassicSkin", false)
                        .putBoolean("Transparent", false)
                        .putBoolean("IsTrustedSkin", false));

        EntityHuman human = (EntityHuman) Entity.createEntity(EntityHuman.NETWORK_ID, position.getLevel().getChunk(position.getChunkX(), position.getChunkZ(), true), tag);;
        human.spawnToAll();
        return human;
    }
}
