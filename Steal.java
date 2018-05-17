package com.bravebots.bravethieving.thief;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;

/**
 * NOTES:
 * 
 */
public class Steal extends LeafTask {

    private SpriteItem Salmon;
    Area Lumby = new Area.Rectangular(new Coordinate(3213, 3237, 0), new Coordinate(3224, 3250, 0));
    Area BankArea = new Area.Rectangular(new Coordinate(3206, 3223, 2), new Coordinate(3210, 3214, 2));
    GameObject firstStair = GameObjects.newQuery().on(new Coordinate(3204, 3207, 0)).actions("Climb-up").results().first();
    WebPath ToBank = Traversal.getDefaultWeb().getPathBuilder().buildTo(BankArea);
    WebPath ToStairs = Traversal.getDefaultWeb().getPathBuilder().useTeleports(false).buildTo(firstStair);
    GameObject upstairs = GameObjects.newQuery().on(new Coordinate(3204, 3207, 2)).actions("Climb-down").results().first();
    GameObject BankChest = GameObjects.newQuery().within(BankArea).names("Bank booth").actions("Bank").results().first();


    @Override
    public void execute() {

        Salmon = Inventory.newQuery().names("Salmon").results().first();
        Npc man = Npcs.newQuery().names("Man").actions("Pickpocket").results().nearestTo(Players.getLocal());

        //Checks if Salmon is in your inventory
            if (Inventory.contains("Salmon")) {
                //Checks if The player is within a good distance of Lumby area
                if (Players.getLocal() != null && Distance.between(Players.getLocal(), Lumby) <= 20) {
                    WebPath ToLumby = Traversal.getDefaultWeb().getPathBuilder().buildTo(Lumby);

                    //If Lumby Path isnt null it will walk to it
                    if (ToLumby != null) {
                        ToLumby.step(Path.TraversalOption.MANAGE_RUN, Path.TraversalOption.MANAGE_DISTANCE_BETWEEN_STEPS);
                        Execution.delay(400, 2000, 900);
                    }
                }

                //Checks if your health is below  or equal to 4
                else if (Health.getCurrent() <= 4) {

                    //Eats the Salmon
                    if (Salmon.interact("Eat")) {
                        Execution.delay(700, 3000, 1740);
                    }
                }

                //Checks if the man is Null and visible
                else if (man != null && man.isVisible()) {
                    System.out.println("Here?!");

                    //If the man isnt visible Turn the Camera Towards him
                    if (!man.isVisible()) {
                        Camera.turnTo(man);
                    }

                    //Pickpocket him
                    if (man.interact("Pickpocket")) {
                        System.out.println("Will I make it here?");
                        Execution.delayWhile(() -> man.isValid(), 700, 2500);
                    }
                }

            } else {
                if (firstStair != null && firstStair.isVisible()) {
                    if (firstStair.interact("Climb-up")) {
                        Execution.delayWhile(() -> firstStair.isValid(), 700, 2500);
                    }
                } else if (ToStairs != null) {
                    ToStairs.step(Path.TraversalOption.MANAGE_DISTANCE_BETWEEN_STEPS, Path.TraversalOption.MANAGE_RUN);
                    Execution.delayWhile(() -> firstStair.isValid(), 700, 2300);
                    if (firstStair != null) {
                        if (firstStair.interact("Climb-up")) {
                            Execution.delayWhile(() -> firstStair.isValid(), 4500, 6000);
                        }
                    }
                }
                if (BankChest.isValid()) {
                    Camera.turnTo(BankChest);
                    if (BankChest != null && BankChest.isVisible()) {
                        Camera.turnTo(BankChest);
                        if (BankChest.interact("Bank")) {
                            Execution.delayUntil(Bank::isOpen, () -> Players.getLocal() != null && Players.getLocal().isMoving(), 100, 2000);
                        }
                    } else if (!BankChest.isVisible()) {
                        Camera.turnTo(BankChest);
                        if (BankChest.interact("Bank")) {
                            Execution.delayUntil(Bank::isOpen, () -> Players.getLocal() != null && Players.getLocal().isMoving(), 100, 2000);
                        }
                    }

                }
                if (Bank.isOpen()) {
                    if (Bank.withdraw("Salmon", 15)) {
                        Execution.delayWhile(() -> Inventory.containsAnyOf("Salmon"), 100, 1200);
                    }
                    if (Bank.close()) {
                        Execution.delayWhile(() -> !Inventory.containsAnyOf("Salmon"), 300, 1200);
                    }
                }
            }
        }
    }
