package de.ximanton.tictactoe.listener;

import de.ximanton.tictactoe.Main;
import de.ximanton.tictactoe.structure.GameRequest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class QuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        ArrayList<GameRequest> toDelete = new ArrayList<>();
        for (GameRequest r : Main.getPlugin().getChallengeRequests()) {
            if (r.getPlayer().equals(e.getPlayer().getUniqueId().toString()) | r.getChallengedPlayer().equals(e.getPlayer().getUniqueId().toString())) {
                toDelete.add(r);
            }
        }
        Main.getPlugin().getChallengeRequests().removeAll(toDelete);
    }
}
