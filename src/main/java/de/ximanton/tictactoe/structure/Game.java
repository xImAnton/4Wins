package de.ximanton.tictactoe.structure;

import com.sun.istack.internal.NotNull;
import de.ximanton.tictactoe.Main;
import de.ximanton.tictactoe.enums.GameEnding;
import de.ximanton.tictactoe.enums.GameItemType;
import de.ximanton.tictactoe.enums.GameState;
import de.ximanton.tictactoe.enums.PlayerStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;


public class Game {
    private final Player player1;
    private final Player player2;
    private StartScreen startScreen;
    private final Player[] players;
    private GameState state;
    private PlayerStatus player1Status;
    private PlayerStatus player2Status;
    private boolean ignoreNextInvClosePlayer1 = false;
    private boolean ignoreNextInvClosePlayer2 = false;
    private Material player1Item;
    private Material player2Item;
    private Inventory gui;
    private FourWinsTable table;
    private int playerOnTurn;
    private final boolean opponentIsAI;


    public static int[] borderSlots = {0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53};

    public Game(@NotNull Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.opponentIsAI = player2 == null;
        this.startScreen = new StartScreen(this);
        this.playerOnTurn = 1;
        player1.openInventory(startScreen.getGui());
        if (!opponentIsAI) { player2.openInventory(startScreen.getGui()); }
        players = new Player[]{player1, player2};
        this.state = GameState.ITEM_SELECTION;
        setPlayerStatus(1, PlayerStatus.IN_START_SCREEN);
        setPlayerStatus(2, PlayerStatus.IN_START_SCREEN);
        Main.getPlugin().getGames().add(this);
    }

    private void setupGUI() {
        this.gui = Bukkit.createInventory(null, 54, Main.getPrefix() + "Spiel");

        for (int slot : new int[]{0, 8, 9, 17, 36, 44, 45, 53}) {
            gui.setItem(slot, GameItemType.EMPTY_BORDER.getItem());
        }

        gui.setItem(18, StartScreen.playerHeadItem(getPlayer(1), 1));
        gui.setItem(26, opponentIsAI ? StartScreen.aiHeadItem() : StartScreen.playerHeadItem(getPlayer(2), 2));
        gui.setItem(27, StartScreen.makeItemField(getPlayerItem(1), getPlayer(1).getName()));
        gui.setItem(35, opponentIsAI ? StartScreen.makeItemField(getPlayerItem(2), "Die KI") : StartScreen.makeItemField(getPlayerItem(2), getPlayer(2).getName()));
        updatePlayerStatusEnchant();
    }

    public void ignoreNextInvClose(int playerIndex, boolean ignore) {
        if (playerIndex == 1) {
            ignoreNextInvClosePlayer1 = ignore;
        } else if (playerIndex == 2) {
            ignoreNextInvClosePlayer2 = ignore;
        }
    }

    public void nextPlayer() {
        if (playerOnTurn == 1) {
            playerOnTurn = 2;
        } else if (playerOnTurn == 2) {
            playerOnTurn = 1;
        } else if (playerOnTurn == 10) {
            playerOnTurn = 1;
        } else if (playerOnTurn == 20) {
            playerOnTurn = 2;
        }
        if (playerOnTurn == 2 & isOpponentAi()) {
            doAiMove();
        }
        updatePlayerStatusEnchant();
    }

    public boolean isOpponentAi() {
        return opponentIsAI;
    }

    private void updatePlayerStatusEnchant() {
        if (getState() == GameState.ENDED | isOpponentAi()) {
            return;
        }
        ItemStack item = GameItemType.EMPTY_BORDER.getItem();

        if (playerOnTurn == 1) {
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }

        for (int player1SideSlot : new int[]{0, 9, 36, 45}) {
            gui.setItem(player1SideSlot, item);
        }

        item = GameItemType.EMPTY_BORDER.getItem();

        if (playerOnTurn == 2) {
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }

        for (int player2SideSlot : new int[]{8, 17, 44, 53}) {
            gui.setItem(player2SideSlot, item);
        }
    }

    public void addItem() {
        FourWinsTableItem item = getTable().getLastPlacedItem();
        setUnclickable();
        new BukkitRunnable() {
            @Override
            public void run() {
                int rowsToGo = item.getRow() + 1;
                int currentSlot = item.getCol() + 1;
                for (int i = 0; i < rowsToGo; i++) {
                    gui.setItem(currentSlot, guiPlayerSetItem(item));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gui.clear(currentSlot);
                    currentSlot += 9;
                }
                getTable().checkForResults(item.getCol(), item.getRow());
                redrawGameScreen();
                nextPlayer();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    public void doAiMove() {
        if (playerOnTurn == 2 & opponentIsAI & getState() == GameState.RUNNING) {
            boolean somethingChanged = false;
            while (!somethingChanged) {
                int bestScore = -10;
                int bestColToMove = 0;
                for (int column : FourWinsTable.columns) {
                    if (table.canPlace(column)) {
                        table.addItem(2, column);
                        int score = minimax(false, 0);
                        table.removeLastItem(column);
                        if (score > bestScore) {
                            bestScore = score;
                            bestColToMove = column;
                        }
                    }
                }
                somethingChanged = table.addItem(2, bestColToMove);
            }
            addItem();
        }
    }

    private int minimax(boolean isMaximizing, int depth) {
        System.out.println("mini");
        GameEnding result = table.checkWinner();
        int score = 0;
        if (result != null) {
            return result.getScore();
        }
        int bestScore = -10;
        if (depth > 15) {
            return bestScore;
        }
        if (isMaximizing) {
            for (int col : FourWinsTable.columns) {
                if (table.canPlace(col)) {
                    table.addItem(2, col);
                    score = minimax(false, depth + 1);
                    if (score > bestScore) {
                        bestScore = score;
                    }
                    table.removeLastItem(col);
                }
            }
        } else {
            for (int col : FourWinsTable.columns) {
                if (table.canPlace(col)) {
                    table.addItem(1, col);
                    score = minimax(true, depth + 1);
                    if (score < bestScore) {
                        bestScore = score;
                    }
                    table.removeLastItem(col);
                }
            }
        }
        return bestScore;
    }

    public void setUnclickable() {
        if (playerOnTurn == 1) {
            playerOnTurn = 20;
        } else if (playerOnTurn == 2) {
            playerOnTurn = 10;
        }
    }

    public int getPlayerOnTurn() {
        return playerOnTurn;
    }

    public boolean isIgnoreNextInvClose(int playerIndex) {
        if (playerIndex == 1) {
            return ignoreNextInvClosePlayer1;
        } else if (playerIndex == 2) {
            return ignoreNextInvClosePlayer2;
        }
        return false;
    }

    public PlayerStatus getPlayerStatus(int playerIndex) {
        if (playerIndex == 1) {
            return player1Status;
        }
        if (playerIndex == 2) {
            return player2Status;
        }
        return null;
    }

    public void setPlayerStatus(int playerIndex, PlayerStatus status) {
        if (playerIndex == 1) {
            player1Status = status;
        } else if (playerIndex == 2) {
            player2Status = status;
        }
    }

    public void tie() {
        setState(GameState.ENDED);
        for (Player player : players) {
            if (player != null) {
                player.sendMessage(Main.getPrefix() + "Das Spiel ist unentschieden ausgegangen!");
            }
        }
    }

    public void start() {
        this.player1Item = startScreen.getItemSelectionScreen(1).getCurrentItem();
        this.player2Item = startScreen.getItemSelectionScreen(2).getCurrentItem();
        this.startScreen = null;
        this.table = new FourWinsTable(this);
        setState(GameState.RUNNING);
        setupGUI();
        String startMsg = Main.getPrefix() + "Das Spiel beginnt";
        for (int playerIndex : new int[]{1, 2}) {
            setPlayerStatus(playerIndex, PlayerStatus.IN_GAME);
            ignoreNextInvClose(playerIndex, true);
            Main.scheduleInvOpen(getPlayer(playerIndex), gui);
            if (!(playerIndex == 2 & opponentIsAI)) {
                getPlayer(playerIndex).sendMessage(startMsg);
            }
        }
    }

    public void end(int winning) {
        setState(GameState.ENDED);
        for (Player player : players) {
            if (player != null) {
                player.sendMessage(Main.getPrefix() + ChatColor.GREEN + ((winning == 2 & isOpponentAi()) ? "Die KI" : getPlayer(winning).getName()) + ChatColor.GRAY + " hat das Spiel gewonnen.");
            }
        }
    }

    public void redrawGameScreen() {
        int slot;
        for (int col : FourWinsTable.columns) {
            for (int row : FourWinsTable.rows) {
                slot = (9 * (row)) + col + 1;
                if (getTable().getItem(col, row).getPlayerIndex() == 0) {
                    continue;
                }
                gui.setItem(slot, guiPlayerSetItem(getTable().getItem(col, row)));
            }
        }
    }

    private ItemStack guiPlayerSetItem(FourWinsTableItem e) {
        ItemStack item = new ItemStack(getPlayerItem(e.getPlayerIndex()));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + (opponentIsAI & e.getPlayerIndex() == 2 ? "Die KI" : getPlayer(e.getPlayerIndex()).getName()));
        if (e.isPartOfWinPosition()) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        } else {
            item.setItemMeta(meta);
        }
        return item;
    }

    public FourWinsTable getTable() {
        return table;
    }

    public Material getPlayerItem(int playerIndex) {
        if (playerIndex == 1) {
            return player1Item;
        } else if (playerIndex == 2) {
            return player2Item;
        } else return null;
    }

    public Player getPlayer(int index) {
        if (index == 1) {
            return player1;
        } else if (index == 2) {
            return player2;
        } else {
            return null;
        }
    }

    public GameState getState() {
        return state;
    }

    public void delete(Player playerWhoCancelled) {
        Main.getPlugin().getGames().remove(this);
        for (Player player : players) {
            if (player == null) continue;
            if (getState() != GameState.ENDED) {
                player.sendMessage(Main.getPrefix() + "Das Spiel wurde von " + ChatColor.GREEN + playerWhoCancelled.getDisplayName() + ChatColor.GRAY + " abgebrochen");
            }
            player.closeInventory();
        }
    }

    public StartScreen getStartScreen() {
        return startScreen;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
