package de.ximanton.tictactoe.structure;

import org.bukkit.Bukkit;

import java.util.UUID;

public class GameRequest {
    private final String player;
    private final String challengedPlayer;

    public GameRequest(String player, String challengedPlayer) {
        this.player = player;
        this.challengedPlayer = challengedPlayer;
    }

    public String getPlayer() {
        return player;
    }

    public String getChallengedPlayer() {
        return challengedPlayer;
    }

    public Game makeGame() {
        return new Game(Bukkit.getPlayer(UUID.fromString(player)), Bukkit.getPlayer(UUID.fromString(challengedPlayer)));
    }
}
