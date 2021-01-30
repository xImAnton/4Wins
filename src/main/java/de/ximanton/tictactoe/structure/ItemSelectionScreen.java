package de.ximanton.tictactoe.structure;

import de.ximanton.tictactoe.Main;
import de.ximanton.tictactoe.enums.GameItemType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Random;

public class ItemSelectionScreen {
    private static final Material[] materials = {Material.ENDER_PEARL, Material.OAK_SAPLING, Material.TORCH, Material.ENDER_EYE, Material.LILY_PAD, Material.CACTUS, Material.CHORUS_FLOWER,
    Material.SUNFLOWER, Material.REDSTONE, Material.OBSERVER, Material.HOPPER, Material.REDSTONE_TORCH, Material.NOTE_BLOCK, Material.DIAMOND, Material.NETHERITE_SCRAP, Material.SCUTE, Material.BELL,
    Material.CAMPFIRE, Material.ARMOR_STAND, Material.ITEM_FRAME, Material.FLOWER_POT};

    private static final int[] borderItemSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 42, 43, 44};

    private static final int[] submitItemSlots = {39, 40, 41};

    private Inventory gui;
    private Material currentItem;

    public ItemSelectionScreen(Material startItem) {
        this.gui = Bukkit.createInventory(null, 45, Main.getPrefix() + "Itemauswahl");
        this.currentItem = startItem;
        setupGui();
    }

    private void setupGui() {
        gui.clear();
        ItemStack item = GameItemType.EMPTY_BORDER.getItem();
        ItemMeta meta;

        for (int borderSlot : ItemSelectionScreen.borderItemSlots) {
            gui.setItem(borderSlot, item);
        }

        for (Material material : ItemSelectionScreen.materials) {
            item = new ItemStack(material);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + Main.niceName(material.toString()));

            if (material != currentItem) {
                meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Klicke um dieses Item auszuwählen"));
                item.setItemMeta(meta);
            } else {
                meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Dieses Item ist aktuell ausgewählt"));
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }
            gui.addItem(item);
        }

        for (int borderSlot : ItemSelectionScreen.submitItemSlots) {
            gui.setItem(borderSlot, GameItemType.ITEM_SUBMIT_ITEM.getItem());
        }
    }

    public Inventory getGUI() {
        return gui;
    }

    public void setCurrentItem(Material currentItem) {
        if (currentItem != Material.LIME_STAINED_GLASS_PANE & currentItem != Material.BLACK_STAINED_GLASS_PANE) {
            this.currentItem = currentItem;
            setupGui();
        }
    }

    public static Material getRandomMaterial(Material not) {
        boolean found = false;
        Random random = new Random();
        int index = 0;
        while (!found) {
            index = random.nextInt(ItemSelectionScreen.materials.length);
            if (ItemSelectionScreen.materials[index] != not) {
                found = true;
            }
        }
        return ItemSelectionScreen.materials[index];
    }

    public Material getCurrentItem() {
        return currentItem;
    }
}
