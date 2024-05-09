package art.ryanstew.savebanner;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Colorable;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SaveBannerCommand implements CommandExecutor
{
    private static final String HELP_MESSAGE = new StringBuilder()
            .append("&7----------------")
            .append("\n&e&lSaveBanner:")
            .append("\n&7- &e/savebanner help")
            .append("\n&7- &e/savebanner list")
            .append("\n&7- &e/savebanner save <name>")
            .append("\n&7- &e/savebanner load <name>")
            .append("\n&7----------------")
            .toString();

    private final SaveBanner plugin;


    public SaveBannerCommand(SaveBanner plugin)
    {
        this.plugin = plugin;
    }


    private boolean bannerNameExists(String name)
    {
        ConfigurationSection savedBannersSection = plugin.getConfig().getConfigurationSection("savedBanners");
        return savedBannersSection != null && savedBannersSection.contains(name.toLowerCase());
    }


    private void saveBanner(Player player, String name)
    {
        if (bannerNameExists(name))
        {
            plugin.sendFormattedMessage(player, "&cA banner with that name already exists!", true);
            return;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        Material handItemMaterial = handItem.getType();
        if (!MaterialSetTag.BANNERS.isTagged(handItemMaterial))
        {
            plugin.sendFormattedMessage(player, "&cYou are not holding a banner!", true);
            return;
        }

        plugin.sendFormattedMessage(player, "material: " + handItemMaterial.name(), true);

        BannerMeta bannerMeta = (BannerMeta) handItem.getItemMeta();
        List<Pattern> patterns = bannerMeta.getPatterns();
        for (Pattern pattern : patterns)
        {
            PatternType type = pattern.getPattern();
            DyeColor color = pattern.getColor();
            plugin.sendFormattedMessage(player, type.toString(), true);
            plugin.sendFormattedMessage(player, color.name(), true);
        }

        plugin.getConfig().set(String.format("savedBanners.%s.material", name.toLowerCase()), handItemMaterial.name());
        plugin.getConfig().set(String.format("savedBanners.%s.patterns", name.toLowerCase()), patterns);
        plugin.saveConfig();
    }


    private void loadBanner(Player player, String name)
    {
        if (!bannerNameExists(name))
        {
            plugin.sendFormattedMessage(player, "&cNo banner with that name exists!", true);
            return;
        }

        ConfigurationSection section = plugin.getConfig().getConfigurationSection(String.format("savedBanners.%s", name.toLowerCase()));
        String bannerMaterialName = section.getString("material");
        ItemStack itemStack = new ItemStack(Material.getMaterial(bannerMaterialName));
        plugin.sendFormattedMessage(player, itemStack.toString(), true);

        List<Pattern> patternList = (List<Pattern>) section.getList("patterns");
        for (Pattern pat : patternList)
        {
            plugin.sendFormattedMessage(player, pat.getColor().name(), true);
            plugin.sendFormattedMessage(player, pat.getPattern().name(), true);
        }

        BannerMeta meta = (BannerMeta) itemStack.getItemMeta();
        meta.setPatterns(patternList);
        itemStack.setItemMeta(meta);

        player.getInventory().addItem(itemStack);
    }


    /*
        /savebanner
        /savebanner help
        /savebanner list
        /savebanner save <name>
        /savebanner load <name>
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        // HELP COMMAND
        if (args.length == 0 || args[0].equalsIgnoreCase("help"))
        {
            plugin.sendFormattedMessage(sender, HELP_MESSAGE, false);
            return true;
        }


        // LIST COMMAND
        if (args[0].equalsIgnoreCase("list"))
        {
            plugin.sendFormattedMessage(sender, "&eList:", false);
            return true;
        }


        // SAVE/LOAD COMMANDS
        if (args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("load"))
        {
            if (!(sender instanceof Player))
            {
                plugin.sendFormattedMessage(sender, "&cOnly players can run this command!", true);
                return true;
            }

            Player player = (Player) sender;

            if (args.length != 2)
            {
                plugin.sendFormattedMessage(player, HELP_MESSAGE, false);
                return true;
            }

            String name = args[1];
            if (args[0].equalsIgnoreCase("save"))
                saveBanner(player, name);
            else
                loadBanner(player, name);

            return true;
        }


        // HELP MESSAGE
        plugin.sendFormattedMessage(sender, HELP_MESSAGE, false);
        return true;


//        // SAVE/LOAD COMMAND
//        boolean isSaveCommand = args[0].equalsIgnoreCase("save");
//        boolean isLoadCommand = args[0].equalsIgnoreCase("load");
//
//        if (!isSaveCommand && !isLoadCommand)
//        {
//            plugin.sendFormattedMessage(sender, HELP_MESSAGE, false);
//            return true;
//        }
//
//        if (!(sender instanceof Player))
//        {
//            plugin.sendFormattedMessage(sender, "&cOnly players can run this command!", true);
//            return true;
//        }
//
//        Player player = (Player) sender;
//
//        if (args.length != 2)
//        {
//            plugin.sendFormattedMessage(player, HELP_MESSAGE, false);
//            return true;
//        }
//
//        if (isSaveCommand)
//        {
//            saveBanner(player, args[1]);
//            return true;
//        }
//
//        if (isLoadCommand)
//        {
//            loadBanner(player, args[1]);
//        }
//
//        return false;
    }
}
