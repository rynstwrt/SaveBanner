package art.ryanstew.savebanner;

import art.ryanstew.savebanner.util.CommandUtil;
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

    private static final int BANNER_LIST_HEIGHT = 9;

    private final SaveBanner plugin;
    private final CommandUtil commandUtil;



    public SaveBannerCommand(SaveBanner plugin)
    {
        this.plugin = plugin;
        commandUtil = new CommandUtil(plugin);
    }



    /**
     * Sends the command sender the list of saved banners,
     * or at least a page of them.
     *
     * @param sender The CommandSender to send the list to.
     * @param page The page number of the banner list entries.
     */
    private void sendBannerList(CommandSender sender, int page)
    {
        ConfigurationSection configSection = plugin.getConfigManager().getBannerConfig().getConfigurationSection("savedBanners");
        if (configSection == null)
        {
            plugin.getGeneralUtil().sendFormattedMessage(sender, "&cThere are no saved banners!", true);
            return;
        }

        Set<String> keys = configSection.getKeys(false);
        if (keys.isEmpty())
        {
            plugin.getGeneralUtil().sendFormattedMessage(sender, "&cThere are no saved banners!", true);
            return;
        }

        int totalPages = (int) Math.ceil((double) keys.size() / BANNER_LIST_HEIGHT);
        if (page > totalPages)
        {
            plugin.getGeneralUtil().sendFormattedMessage(sender, "&cThe page number you entered is not valid!", true);
            return;
        }

        Set<String> pageEntries = commandUtil.getPageOfBannerList(keys, BANNER_LIST_HEIGHT, page);
        String colorCode = plugin.getConfig().getString("accent-color-code");
        StringBuilder message = new StringBuilder(String.format("&8-------- %sSaved Banners &f(page %d/%d)&8 --------\n&8- &7", colorCode, page, totalPages) + String.join("\n&8- &7", pageEntries));

        int neededLines = BANNER_LIST_HEIGHT - pageEntries.size();
        for (int i = 0; i < neededLines; ++i)
            message.append("\n&r");

        plugin.getGeneralUtil().sendFormattedMessage(sender, message.toString(), false);
    }



    /**
     * Saves the banner that the player is holding in their
     * main hand to config.
     *
     * @param player The player who ran the command.
     * @param name The name to save the banner under.
     */
    private void saveBanner(Player player, String name)
    {
        if (commandUtil.bannerNameExists(name))
        {
            plugin.getGeneralUtil().sendFormattedMessage(player, "&cA banner with that name already exists!", true);
            return;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        Material handItemMaterial = handItem.getType();
        if (!MaterialSetTag.BANNERS.isTagged(handItemMaterial))
        {
            plugin.getGeneralUtil().sendFormattedMessage(player, "&cYou are not holding a banner!", true);
            return;
        }

        ItemStack newItemStack = handItem.clone();
        newItemStack.setAmount(1);

        String path = String.format("savedBanners.%s", name.toLowerCase());
        plugin.getConfigManager().getBannerConfig().set(path, newItemStack);
        boolean saveSuccess = plugin.getConfigManager().saveBannerConfig();

        if (!saveSuccess)
        {
            plugin.getGeneralUtil().sendFormattedMessage(player, "&cFailed to save the config file!", true);
            return;
        }

        plugin.getGeneralUtil().sendFormattedMessage(player, String.format("&aSuccessfully saved banner named %s!", name), true);
    }



    /**
     * Loads the banner from config by name and
     * gives it to the player.
     *
     * @param player The player who ran the command.
     * @param name The name of the banner to load.
     */
    private void loadBanner(Player player, String name)
    {
        if (!commandUtil.bannerNameExists(name))
        {
            plugin.getGeneralUtil().sendFormattedMessage(player, "&cNo banner with that name exists!", true);
            return;
        }

        String path = String.format("savedBanners.%s", name.toLowerCase());
        ItemStack itemStack = plugin.getConfigManager().getBannerConfig().getItemStack(path);

        if (itemStack == null)
        {
            plugin.getGeneralUtil().sendFormattedMessage(player, "&cCould not load that banner!", true);
            return;
        }

        player.getInventory().addItem(itemStack);
        plugin.getGeneralUtil().sendFormattedMessage(player, String.format("&aSuccessfully loaded banner named %s!", name), true);
    }



    /**
     * Deletes a saved banner from the config by name.
     *
     * @param sender The CommandSender who ran the command.
     * @param name The name of the banner to delete.
     */
    private void deleteBanner(CommandSender sender, String name)
    {
        if (!commandUtil.bannerNameExists(name))
        {
            plugin.getGeneralUtil().sendFormattedMessage(sender, "&cNo banner with that name exists!", true);
            return;
        }

        String path = String.format("savedBanners.%s", name.toLowerCase());
        plugin.getConfigManager().getBannerConfig().set(path, null);
        boolean saveSuccess = plugin.getConfigManager().saveBannerConfig();

        if (!saveSuccess)
        {
            plugin.getGeneralUtil().sendFormattedMessage(sender, "&cFailed to save the config file!", true);
        }

        plugin.getGeneralUtil().sendFormattedMessage(sender, String.format("&aSuccessfully deleted banner named %s!", name), true);
    }



    /**
     * The main command function that executes when
     * a SaveBanner command is ran.
     *
     * @param sender The CommandSender who ran the command.
     * @param command The command itself.
     * @param s The command label.
     * @param args The arguments passed with the command.
     * @return True if the command was executed correct, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        /*
            /savebanner
            /savebanner help
            /savebanner reload
            /savebanner list [page]
            /savebanner save <name>
            /savebanner load <name>
            /savebanner delete <name>
         */


        // HELP COMMAND
        if (args.length == 0 || args[0].equalsIgnoreCase("help"))
        {
            commandUtil.sendHelpMessage(sender);
            return true;
        }


        // RELOAD COMMAND
        if (args[0].equalsIgnoreCase("reload"))
        {
            boolean saveSuccess = plugin.getConfigManager().reloadConfigs();
            if (!saveSuccess)
            {
                plugin.getGeneralUtil().sendFormattedMessage(sender, "&cFailed to reload the banner config!", true);
                return true;
            }

            plugin.getGeneralUtil().sendFormattedMessage(sender, "&aSuccessfully reloaded the config!", true);
            return true;
        }


        // LIST COMMAND
        if (args[0].equalsIgnoreCase("list"))
        {
            if (args.length == 2)
            {
                int pageNumber = commandUtil.getListPageIntegerFromArgument(args[1]);
                if (pageNumber < 1)  // includes -1
                {
                    plugin.getGeneralUtil().sendFormattedMessage(sender, "&cThe page number you entered is not valid!", true);
                    return true;
                }

                sendBannerList(sender, pageNumber);
            }
            else
            {
                sendBannerList(sender, 1);
            }

            return true;
        }


        // DELETE COMMAND
        if (args[0].equalsIgnoreCase("delete"))
        {
            if (args.length != 2)
            {
                plugin.getGeneralUtil().sendFormattedMessage(sender, "&cYou did not specify the name of the banner to delete!", true);
                return true;
            }

            deleteBanner(sender, args[1]);
            return true;
        }


        // SAVE/LOAD COMMANDS
        boolean isSaveCommand = args[0].equalsIgnoreCase("save");
        boolean isLoadCommand = args[0].equalsIgnoreCase("load");
        if (isSaveCommand || isLoadCommand)
        {
            if (!(sender instanceof Player player))
            {
                plugin.getGeneralUtil().sendFormattedMessage(sender, "&cOnly players can run this command!", true);
                return true;
            }

            if (args.length != 2)
            {
                String errorMessage = isSaveCommand ?
                        "&cYou did not specify the name of the banner to save!" :
                        "&cYou did not specify the name of the banner to load!";
                plugin.getGeneralUtil().sendFormattedMessage(sender, errorMessage, true);
                return true;
            }

            if (isSaveCommand) saveBanner(player, args[1]);
            else loadBanner(player, args[1]);

            return true;
        }


        // HELP MESSAGE FALLBACK
        commandUtil.sendHelpMessage(sender);
        return true;
    }
}
