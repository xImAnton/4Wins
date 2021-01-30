package de.ximanton.tictactoe.structure;

import de.ximanton.tictactoe.Main;
import de.ximanton.tictactoe.enums.GameItemType;
import de.ximanton.tictactoe.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;


public class StartScreen {
    private final Inventory gui;
    private static final int[] borderItemSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
    private final Game game;
    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private final ItemSelectionScreen player1ItemScreen;
    private final ItemSelectionScreen player2ItemScreen;
    private int startState = 3;
    private BukkitRunnable startGameRunnable = null;
    private boolean sameItems = false;

    public StartScreen(Game game) {
        this.game = game;
        this.gui = Bukkit.createInventory(null, 45, Main.getPrefix() + "Warten auf " + ChatColor.YELLOW + "Start");
        borderItem(GameItemType.EMPTY_BORDER);
        player1ItemScreen = new ItemSelectionScreen(ItemSelectionScreen.getRandomMaterial(null));
        player2ItemScreen = new ItemSelectionScreen(ItemSelectionScreen.getRandomMaterial(player1ItemScreen.getCurrentItem()));
        setupGUI();
        updateCurrentPlayerItems();
    }

    public ItemSelectionScreen getItemSelectionScreen(int playerIndex) {
        if (playerIndex == 1) {
            return player1ItemScreen;
        } else if (playerIndex == 2) {
            return player2ItemScreen;
        }
        return null;
    }

    public Inventory getGui() {
        return gui;
    }

    public void borderItem(GameItemType itemType) {
        ItemStack item = itemType.getItem();
        for (int slot : borderItemSlots) {
            gui.setItem(slot, item);
        }
    }

    private void setupGUI() {
        gui.setItem(10, playerHeadItem(game.getPlayer(1), 1));

        setReady(1, player1Ready);
        if (!game.isOpponentAi()) {
            gui.setItem(16, playerHeadItem(game.getPlayer(2), 2));
            setReady(2, player2Ready);
        } else {
            gui.setItem(16, aiHeadItem());
            setReady(2, true);
        }
        setStartTimer(-1);
    }

    public static ItemStack playerHeadItem(Player player, int playerIndex) {
        ItemStack item = Main.getPlugin().getPlayerHead(player);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Spieler " + ChatColor.YELLOW + playerIndex + ChatColor.GRAY + ": "+ ChatColor.GREEN + player.getName());
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack aiHeadItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("442c85f6-3e3b-49ed-9432-00611ccea37d")));
        skull.setDisplayName(ChatColor.GRAY + "Spieler " + ChatColor.YELLOW + "2" + ChatColor.GRAY + ": "+ ChatColor.GREEN + "Die KI");
        item.setItemMeta(skull);
        return item;
    }

    public void updateCurrentPlayerItems() {
        if (player1ItemScreen.getCurrentItem().equals(player2ItemScreen.getCurrentItem())) {
            borderItem(GameItemType.SAME_ITEM);
            sameItems = true;
        }
        if (sameItems & !player1ItemScreen.getCurrentItem().equals(player2ItemScreen.getCurrentItem())) {
            borderItem(GameItemType.EMPTY_BORDER);
            sameItems = false;
            if (player1Ready & player2Ready) {
                startState = 3;
                startCountdown();
            }
        }
        gui.setItem(19, makeItemField(player1ItemScreen.getCurrentItem(), game.getPlayer(1).getName()));
        gui.setItem(25, makeItemField(player2ItemScreen.getCurrentItem(), game.isOpponentAi() ? "Die KI" : game.getPlayer(2).getName() ));
    }

    public static ItemStack makeItemField(Material mat, String playerName) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + playerName + ChatColor.GRAY + "'s Item");
        meta.setLore(Arrays.asList("", ChatColor.YELLOW + Main.niceName(mat.toString())));
        item.setItemMeta(meta);
        return item;
    }

    public void setReady(int playerIndex, boolean ready) {
        if (playerIndex == 1) {
            gui.setItem(28, readyItem(ready, game.getPlayer(playerIndex).getName()));
            player1Ready = ready;
        } else if (playerIndex == 2) {
            if (!game.isOpponentAi()) {
                gui.setItem(34, readyItem(ready, game.getPlayer(playerIndex).getName()));
            } else {
                gui.setItem(34, readyItem(ready, "Die KI"));
            }
            player2Ready = ready;
        } else {
            return;
        }

        if (player1Ready & player2Ready & !sameItems) {
            startCountdown();
        } else if (game.getState() == GameState.STARTING) {
            stopCountdown();
        }
    }

    public void stopCountdown() {
        startGameRunnable.cancel();
        setStartTimer(-1);
        startState = 3;
        game.setState(GameState.ITEM_SELECTION);
    }

    private void startCountdown() {
        startGameRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (startState < 4 & startState > 0) {
                    setStartTimer(startState);
                    startState--;
                } else if (startState == 0) {
                    this.cancel();
                    startGame();
                }
            }
        };
        startGameRunnable.runTaskTimer(Main.getPlugin(), 20, 20);
        game.setState(GameState.STARTING);
    }

    private void startGame() {
        game.start();
    }

    private ItemStack readyItem(boolean ready, String playerName) {
        ItemStack item;
        ItemMeta meta;
        if (ready) {
            item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + playerName + ChatColor.GRAY + " ist bereit");
            item.setItemMeta(meta);
            return item;
        } else {
            item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + playerName + ChatColor.GRAY + " ist nicht bereit");
            item.setItemMeta(meta);
            return item;
        }
    }

    public boolean getReady(int playerIndex) {
        if (playerIndex == 1) {
            return player1Ready;
        } else {
            return player2Ready;
        }
    }

    public void setStartTimer(int time) {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        if (time >= 0) {
            meta.setDisplayName(ChatColor.GRAY + "Spielstart in " + ChatColor.YELLOW + time + " Sekunden");
            int am = time;
            if (time == 0) {
                am = 1;
            }
            item.setAmount(am);
        } else {
            meta.setDisplayName(ChatColor.RED + "Das Spiel startet, sobald alle Spieler bereit sind.");
            item.setAmount(1);
        }
        item.setItemMeta(meta);
        gui.setItem(22, item);
    }
}
