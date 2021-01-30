package de.ximanton.tictactoe.commands;

import de.ximanton.tictactoe.Main;
import de.ximanton.tictactoe.structure.Game;
import de.ximanton.tictactoe.structure.GameRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;

public class FourWinsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getPrefix() + "Nur Spieler können 4 Gewinnt spielen :P");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage(Main.getPrefix() + "Bitte gib den Spieler an, den du herausfordern möchstest");
            return false;
        }
        Player player = (Player) sender;
        Player playerToChallenge = Bukkit.getPlayerExact(args[0]);
        if (args[0].equalsIgnoreCase("ai")) {
            new Game(player, null);
            return true;
        }
        if (playerToChallenge == null) {
            sender.sendMessage(Main.getPrefix() + "Dieser Spieler ist nicht online");
            return false;
        }
        if (player.equals(playerToChallenge)) {
            sender.sendMessage(Main.getPrefix() + "Du kannst dich nicht selbst herausfordern");
            return false;
        }

        ArrayList<GameRequest> toDelete = new ArrayList<>();
        boolean start = false;
        for (GameRequest g : Main.getPlugin().getChallengeRequests()) {
            if (g.getChallengedPlayer().equals(player.getUniqueId().toString()) && g.getPlayer().equals(playerToChallenge.getUniqueId().toString())) {
                g.makeGame();
                start = true;
                toDelete.add(g);
            }
            else if (player.getUniqueId().toString().equals(g.getPlayer()) && playerToChallenge.getUniqueId().toString().equals(g.getChallengedPlayer())) {
                player.sendMessage(Main.getPrefix() + "Du hast diesen Spieler bereits herausgefordert");
                return false;
            }
        }
        if (start) {
            Main.getPlugin().getChallengeRequests().removeAll(toDelete);
            return true;
        }
        Main.getPlugin().getChallengeRequests().add(new GameRequest(player.getUniqueId().toString(), playerToChallenge.getUniqueId().toString()));
        player.sendMessage(Main.getPrefix() + "Deine Herausforderung wird gesendet");

        playerToChallenge.sendMessage(Main.getPrefix() + ChatColor.GREEN + player.getDisplayName() + ChatColor.GRAY + " hat dich zu einer Runde 4 Gewinnt herausgefordert!");
        TextComponent clickText = new TextComponent(Main.getPrefix() + "Klicke " + ChatColor.RED + "hier" + ChatColor.GRAY + " zum akzeptieren");
        clickText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/4wins " + player.getName()));
        clickText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "Klicken zum akzeptieren")));
        playerToChallenge.spigot().sendMessage(clickText);
        return true;
    }
}
