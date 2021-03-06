package dk.doggycraft.dcprison;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Prison extends JavaPlugin
{
	private PermissionsManager	permissionManager	= null;
	private Economy economyManager;
	private ChatManager			chatManager	= null;
	private PrisonRegionManager	regionManager		= null;
	private PrisonManager		prisonManager		= null;

	private FileConfiguration	config				= null;
	private Commands			commands			= null;

	public boolean				debug				= false;
	public String				killMessage			= "&r{killerPrefix}&r{killerDisplayname} &rdræbte &r{killedPrefix}&r{killedDisplayname}&r!";
	private static Prison		plugin;

	public PrisonManager getPrisonManager()
	{
		return this.prisonManager;
	}
	
	public PermissionsManager getPermissionsManager()
	{
		return this.permissionManager;
	}

	public Economy getEconomyManager() { return economyManager; }
	
	public ChatManager getChatManager()
	{
		return this.chatManager;
	}
	
	public PrisonRegionManager getRegionManager()
	{
		return this.regionManager;
	}

	public void log(String message)
	{
		getLogger().info(message);
	}

	public void logDebug(String message)
	{
		if (this.debug)
		{
			getLogger().info(message);
		}
	}

	public void sendInfo(Player player, String message)
	{
		player.sendMessage(ChatColor.AQUA + message);
	}

	public void sendToAll(String message)
	{
		getServer().broadcastMessage(message);
	}

	public void sendMessage(String playerName, String message)
	{
		getServer().getPlayer(playerName).sendMessage(ChatColor.AQUA + message);
	}

	public void reloadSettings()
	{
		reloadConfig();
		loadSettings();
		saveSettings();
	}

	public void loadSettings()
	{
		config = getConfig();
		
		debug = config.getBoolean("Debug", false);
		killMessage = config.getString("KillMessage", "&r{killerPrefix}&r{killerDisplayname} &rdræbte &r{killedPrefix}&r{killedDisplayname}&r!");
	}

	public void saveSettings()
	{
		config.set("Debug", debug);
		config.set("KillMessage", killMessage);

		saveConfig();
	}

	public void onEnable()
	{
		this.permissionManager = new PermissionsManager(this);
		this.chatManager = new ChatManager(this);
		this.prisonManager = new PrisonManager(this);
		this.regionManager = new PrisonRegionManager(this);

		this.commands = new Commands(this);

		PluginManager pm = getServer().getPluginManager();

		if (pm.getPlugin("Vault") != null)
		{
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null)
			{
				economyManager = economyProvider.getProvider();
			}
		}

		loadSettings();
		saveSettings();

		this.permissionManager.load();
		this.chatManager.load();
		this.prisonManager.load();
		this.regionManager.load();

		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new KillListener(this), this);

	}

	public void onDisable()
	{
		//reloadSettings();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return commands.onCommand(sender, cmd, label, args);
	}
}