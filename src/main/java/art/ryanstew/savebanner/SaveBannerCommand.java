package art.ryanstew.savebanner;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class SaveBannerCommand implements CommandExecutor
{
    private static final String HELP_MESSAGE = """

            &7-----------------------
            &d&lSaveBanner:
            &8- &7/banner help
            &8- &7/banner list
            &8- &7/banner save <name>
            &8- &7/banner load <name>
            &8- &7/banner delete <name>
            &7-----------------------
            &r""";

    private final SaveBanner plugin;



    public SaveBannerCommand(SaveBanner plugin)
    {
        this.plugin = plugin;
    }



    private boolean bannerNameExists(String name)
    {
        ConfigurationSection savedBannersSection = plugin.getBannerConfig().getConfigurationSection("savedBanners");
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

        ItemStack newItemStack = handItem.clone();
        newItemStack.setAmount(1);

        String path = String.format("savedBanners.%s", name.toLowerCase());
        plugin.getBannerConfig().set(path, newItemStack);
        boolean saveSuccess = plugin.saveBannerConfig();

        if (!saveSuccess)
        {
            plugin.sendFormattedMessage(player, "&cFailed to save the config file!", true);
            return;
        }

        plugin.sendFormattedMessage(player, String.format("&aSuccessfully saved banner named %s!", name), true);
    }



    private void loadBanner(Player player, String name)
    {
        if (!bannerNameExists(name))
        {
            plugin.sendFormattedMessage(player, "&cNo banner with that name exists!", true);
            return;
        }

        String path = String.format("savedBanners.%s", name.toLowerCase());
        ItemStack itemStack = plugin.getBannerConfig().getItemStack(path);

        if (itemStack == null)
        {
            plugin.sendFormattedMessage(player, "&cCould not load that banner!", true);
            return;
        }

        player.getInventory().addItem(itemStack);
        plugin.sendFormattedMessage(player, String.format("&aSuccessfully loaded banner named %s!", name), true);
    }



    private void deleteBanner(CommandSender sender, String name)
    {
        if (!bannerNameExists(name))
        {
            plugin.sendFormattedMessage(sender, "&cNo banner with that name exists!", true);
            return;
        }

        String path = String.format("savedBanners.%s", name.toLowerCase());
        plugin.getBannerConfig().set(path, null);
        boolean saveSuccess = plugin.saveBannerConfig();

        if (!saveSuccess)
        {
            plugin.sendFormattedMessage(sender, "&cFailed to save the config file!", true);
        }

        plugin.sendFormattedMessage(sender, String.format("&aSuccessfully deleted banner named %s!", name), true);
    }



    /*
        /savebanner
        /savebanner help
        /savebanner list
        /savebanner save <name>
        /savebanner load <name>
        /savebanner delete <name>
        /savebanner reload
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


        // RELOAD COMMAND
        if (args[0].equalsIgnoreCase("reload"))
        {
            plugin.reloadConfig();
            plugin.saveConfig();

            plugin.loadBannerConfig();
            boolean saveSuccess = plugin.saveBannerConfig();
            if (!saveSuccess)
            {
                plugin.sendFormattedMessage(sender, "&cFailed to reload the banner config!", true);
                return true;
            }

            plugin.sendFormattedMessage(sender, "&aSuccessfully reloaded the config!", true);
            return true;
        }


        // LIST COMMAND
        if (args[0].equalsIgnoreCase("list"))
        {
            ConfigurationSection configSection = plugin.getBannerConfig().getConfigurationSection("savedBanners");
            if (configSection == null)
            {
                plugin.sendFormattedMessage(sender, "&cThere are no saved banners!", true);
                return true;
            }

            Set<String> keys = configSection.getKeys(false);
            if (keys.isEmpty())
            {
                plugin.sendFormattedMessage(sender, "&cThere are no saved banners!", true);
                return true;
            }

            String message = "&fSaved banners:\n&8- &7" + String.join("\n&8- &7", keys);
            plugin.sendFormattedMessage(sender, message, true);
            return true;
        }


        if (args[0].equalsIgnoreCase("delete"))
        {
            if (args.length != 2)
            {
                plugin.sendFormattedMessage(sender, HELP_MESSAGE, false);
                return true;
            }

            deleteBanner(sender, args[1]);
            return true;
        }


        // SAVE/LOAD COMMANDS
        if (args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("load"))
        {
            if (!(sender instanceof Player player))
            {
                plugin.sendFormattedMessage(sender, "&cOnly players can run this command!", true);
                return true;
            }

            if (args.length != 2)
            {
                plugin.sendFormattedMessage(player, HELP_MESSAGE, false);
                return true;
            }

            if (args[0].equalsIgnoreCase("save"))
                saveBanner(player, args[1]);
            else
                loadBanner(player, args[1]);

            return true;
        }


        // HELP MESSAGE
        plugin.sendFormattedMessage(sender, HELP_MESSAGE, false);
        return true;
    }
}
