package io.github.derec4.bowsersBigBlast.command;

import io.github.derec4.bowsersBigBlast.game.GameState;

public class CommandManager {
    public void onCommand(String command, String[] args) {
        if (command.equalsIgnoreCase("bowsergame")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                GameState.getInstance().reset();
                System.out.println("Bowser's Big Blast game stopped.");
            } else if (args.length == 1) {
                try {
                    int numPlayers = Integer.parseInt(args[0]);
                    if (numPlayers < GameState.getInstance().getMinPlayers() || numPlayers > GameState.getInstance().getMaxPlayers()) {
                        System.out.println("Player count must be between 4 and 6.");
                        return;
                    }
                    GameState.getInstance().setMaxPlayers(numPlayers);
                    GameState.getInstance().setGameRunning(true);
                    System.out.println("Bowser's Big Blast game started with " + numPlayers + " players.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number of players.");
                }
            } else {
                System.out.println("Usage: /bowsergame [number] or /bowsergame stop");
            }
        }
    }
}
