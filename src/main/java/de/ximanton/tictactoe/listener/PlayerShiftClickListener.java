package de.ximanton.tictactoe.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerShiftClickListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType() == EntityType.PLAYER & e.getPlayer().isSneaking() & e.getHand() == EquipmentSlot.HAND) {
            e.getPlayer().performCommand("4wins " + e.getRightClicked().getName());
        }
    }
}
