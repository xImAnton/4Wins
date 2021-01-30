package de.ximanton.tictactoe.listener;

import de.ximanton.tictactoe.Main;
import de.ximanton.tictactoe.structure.FourWinsTableItem;
import de.ximanton.tictactoe.structure.Game;
import de.ximanton.tictactoe.enums.GameState;
import de.ximanton.tictactoe.enums.PlayerStatus;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Game game = Main.getPlugin().getGame((Player) e.getWhoClicked());
        if (game != null) {
            int slot = e.getRawSlot();
            e.setCancelled(true);
            int playerIndex;
            if (game.getPlayer(1).equals(e.getWhoClicked())) {
                playerIndex = 1;
            } else if (game.getPlayer(2).equals(e.getWhoClicked())) {
                playerIndex = 2;
            } else {
                return;
            }
            if (((slot == 28 & playerIndex == 1) || (slot == 34 & playerIndex == 2)) & game.getPlayerStatus(playerIndex) == PlayerStatus.IN_START_SCREEN) {
                handleReadyClick(game, playerIndex);
                return;
            }
            if (((slot == 19 & playerIndex == 1) || (slot == 25 & playerIndex == 2)) & game.getPlayerStatus(playerIndex) == PlayerStatus.IN_START_SCREEN) {
                openItemSelectionScreen(game, playerIndex);
                return;
            }
            if ((slot > 38 & slot < 42) & game.getPlayerStatus(playerIndex) == PlayerStatus.IN_ITEM_SELECTION) {
                closeItemSelectionScreen(game, playerIndex, (Player) e.getWhoClicked());
                return;
            }
            if (game.getPlayerStatus(playerIndex) == PlayerStatus.IN_ITEM_SELECTION) {
                setNewItem(game, playerIndex, e.getCurrentItem());
                return;
            }
            if (game.getState() == GameState.RUNNING & slot != -999) {
                handleGameClick(e, playerIndex, game);
                return;
            }
        }
    }

    private void handleGameClick(InventoryClickEvent e, int playerIndex, Game game) {
        int slot = e.getRawSlot();
        if (game.getPlayerOnTurn() != playerIndex) {
            return;
        }
        if (!ArrayUtils.contains(Game.borderSlots, slot)) {
            boolean somethingChanged = game.getTable().addItem(playerIndex, slot % 9 - 1);
            if (somethingChanged) {
                game.addItem();
            }
        }
    }

    private void handleReadyClick(Game game, int playerIndex) {
        game.getStartScreen().setReady(playerIndex, !game.getStartScreen().getReady(playerIndex));
    }

    private void openItemSelectionScreen(Game game, int playerIndex) {
        game.ignoreNextInvClose(playerIndex, true);
        Main.scheduleInvOpen(game.getPlayer(playerIndex), game.getStartScreen().getItemSelectionScreen(playerIndex).getGUI());
        game.setPlayerStatus(playerIndex, PlayerStatus.IN_ITEM_SELECTION);
    }

    private void closeItemSelectionScreen(Game game, int playerIndex, Player playerWhoClicked) {
        game.ignoreNextInvClose(playerIndex, true);
        Main.scheduleInvOpen(playerWhoClicked, game.getStartScreen().getGui());
        game.setPlayerStatus(playerIndex, PlayerStatus.IN_START_SCREEN);
        if (game.getState() == GameState.STARTING) {
            game.getStartScreen().stopCountdown();
            game.getStartScreen().setStartTimer(-1);
            game.getStartScreen().setReady(playerIndex, false);
        }
        game.getStartScreen().updateCurrentPlayerItems();
    }

    private void setNewItem(Game game, int playerIndex, ItemStack currentItem) {
        game.getStartScreen().getItemSelectionScreen(playerIndex).setCurrentItem(currentItem.getType());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Game game = Main.getPlugin().getGame((Player) e.getPlayer());
        if (game != null) {
            int player;
            if (game.getPlayer(1).equals(e.getPlayer())) {
                player = 1;
            } else if (game.getPlayer(2).equals(e.getPlayer())) {
                player = 2;
            } else { return; }
            if (game.isIgnoreNextInvClose(player)) {
                game.ignoreNextInvClose(player, false);
                return;
            }
            game.delete((Player) e.getPlayer());
        }
    }
}
