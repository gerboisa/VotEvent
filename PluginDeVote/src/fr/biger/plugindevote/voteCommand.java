package fr.biger.plugindevote;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import fr.farrael.rootlib.api.command.ProxySender;
import fr.farrael.rootlib.api.command.annotation.method.Command;
import fr.farrael.rootlib.api.command.annotation.method.Command.Sender;
import fr.farrael.rootlib.api.command.annotation.method.Permission;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;


@Command(alias = "event")

public class voteCommand{
	

	
	Scoreboard boarda;
	
    enum Type {
        PECHE, SHEEP, VOLEUR ;
 
        // initialise vote
        private int votevaleur = 0;
    }
    Map<UUID, Type> playerVote = new HashMap<>();
	
    @Permission("voteControl")
    @Command(alias = "start", sender = Sender.PLAYER)
    public void start(ProxySender sender) {
    	this.playerVote.clear();
        Bukkit.broadcastMessage("§bNous allons procéder à un vote pour savoir quel événement vous voulez faire, à vos cliques !");
        
        //test clickable
        for (Type type : Type.values()) {
        	
        	TextComponent message = new TextComponent(type.name().toLowerCase());
        	message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/event vote "+type.name().toLowerCase()) );
        	message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Voter pour le "+type.name().toLowerCase() ).create() ) );
            for(Player p : Bukkit.getOnlinePlayers()){
            	p.spigot().sendMessage( message );
            	
            }
        }
        


        // if it do not exist, create it
        if (this.boarda == null) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            this.boarda = manager.getMainScoreboard();
        }
 
        // If there is no objective, create it
        Objective obj = this.boarda.getObjective("Vote");
        if (obj == null)
            obj = this.boarda.registerNewObjective("Vote", "", "");
 
        // Set on the sidebar
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§aVotes");
 
        // Create score
        for (Type type : Type.values()) {
            Score score = obj.getScore(type.name().toLowerCase());
            score.setScore(0);
        }
        
        
    }
    
    @Command(alias = "vote @type")
    @Permission("voter")
    public void vote(ProxySender sender, Type type) {
        // Retrieve player UUID
        UUID uuid = sender.asPlayer().getUniqueId();
 
        // Increment vote
        type.votevaleur++;
 
        // Add to the list and remove previous vote
        Type previous = this.playerVote.put(uuid, type);
        
        if (previous != null)
            previous.votevaleur--;

        if(previous != null && previous == type) {
                sender.sendMessage("Tu as déjà voté pour : " + ChatColor.GOLD + type.name().toLowerCase());
            } else {
                sender.sendMessage("Tu viens de voter pour : " + ChatColor.GOLD + type.name().toLowerCase());
                this.update();
            }
    }
    
    @Command(alias = "end")
    @Permission("voteControl")
    public void end(ProxySender sender) {
        Bukkit.broadcastMessage("§aLe vote est maintenant terminé !");
 
        // Clear vote
        for (Type type : Type.values())
            type.votevaleur = 0;
 
        this.boarda.clearSlot(DisplaySlot.SIDEBAR);
    }
    
    
    
    public void update() {
        // The scoreboard do not exist yet, create it
        if (this.boarda == null) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            this.boarda = manager.getMainScoreboard();
        }
 
        // The objective do not exist yet, nothing to update
        Objective objective = this.boarda.getObjective("Vote");
        if (objective == null)
            return;
 
        // Update vote
        for (Type type : Type.values()) {
            Score score = objective.getScore(type.name().toLowerCase());
            score.setScore(type.votevaleur);
        }
    }
    
}
