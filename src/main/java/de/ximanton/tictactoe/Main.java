package de.ximanton.tictactoe;

import de.ximanton.tictactoe.commands.FourWinsCommand;
import de.ximanton.tictactoe.listener.InventoryListener;
import de.ximanton.tictactoe.listener.PlayerShiftClickListener;
import de.ximanton.tictactoe.listener.QuitListener;
import de.ximanton.tictactoe.structure.Game;
import de.ximanton.tictactoe.structure.GameRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Scanner;

public final class Main extends JavaPlugin {
    private static Main INSTANCE;
    private final ArrayList<GameRequest> challengeRequests = new ArrayList<>();
    private final ArrayList<Game> games = new ArrayList<>();

    public Main() {
        Main.INSTANCE = this;
    }

    public static Main getPlugin() {
        return Main.INSTANCE;
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerListener();
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        getCommand("4wins").setExecutor(new FourWinsCommand());
    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new QuitListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new PlayerShiftClickListener(), this);
    }

    public static String getPrefix() {
        return ChatColor.DARK_GRAY + "| " + ChatColor.YELLOW + "» " + ChatColor.AQUA + "4" + ChatColor.BLUE + " Gewinnt" + ChatColor.GRAY + ": ";
    }

    public ArrayList<GameRequest> getChallengeRequests() {
        return challengeRequests;
    }

    public ItemStack getPlayerHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwningPlayer(player);
        skull.setDisplayName(ChatColor.GRAY + player.getName());
        item.setItemMeta(skull);
        return item;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public Game getGame(Player player) {
        for (Game game : games) {
            if (game.isOpponentAi() & game.getPlayer(1).equals(player)) {
                return game;
            }
            if (game.getPlayer(1).equals(player) | game.getPlayer(2).equals(player)) {
                return game;
            }
        }
        return null;
    }

    public static String niceName(String name) {
        String oldName = name.replace("_", " ").toLowerCase();
        StringBuilder newName = new StringBuilder();
        Scanner lineScan = new Scanner(oldName);
        while (lineScan.hasNext()) {
            String word = lineScan.next();
            newName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return newName.toString();
    }

    public static void scheduleInvOpen(Player player, Inventory inv) {
        if (player == null) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                player.openInventory(inv);
            }
        }.runTaskLater(Main.getPlugin(), 1);
    }
}
