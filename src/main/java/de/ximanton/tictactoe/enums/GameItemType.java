package de.ximanton.tictactoe.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public enum GameItemType {
    EMPTY_BORDER, SAME_ITEM, ITEM_SUBMIT_ITEM;

    public ItemStack getItem() {
        ItemStack item = null;
        ItemMeta meta;
        switch (this) {
            case EMPTY_BORDER:
                item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                meta = item.getItemMeta();
                meta.setDisplayName(" ");
                item.setItemMeta(meta);
                break;
            case SAME_ITEM:
                item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Ihr könnt nicht mit denselben Symbolen spielen");
                meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Ein Spieler sollte ein anderes", ChatColor.YELLOW + "Item auswählen"));
                item.setItemMeta(meta);
                break;
            case ITEM_SUBMIT_ITEM:
                item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + "Bestätigen");
                meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Klicke zum Bestätigen"));
                item.setItemMeta(meta);
                break;
        }
        return item;
    }
}
