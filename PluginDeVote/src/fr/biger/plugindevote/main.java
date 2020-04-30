package fr.biger.plugindevote;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import fr.farrael.rootlib.api.command.CommandManager;

public class main extends JavaPlugin implements Listener{
	
	public static CommandManager commandes;
	
	
	@Override
	public void onEnable() {
		System.out.println("Le plugin se lance");
		
		commandes = new CommandManager(this);
		commandes.register(new voteCommand());
		
		
		getServer().getPluginManager().registerEvents(this, this);
	}
}
