package com.bravebots.bravethieving.thief;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

////import path.to.your.Steal
////import path.to.your.WalkToLumbridge

/**
 * NOTES:
 * This is the root node.

Add children of this branch using the settings to the right.
 */
public class InLumbridge extends BranchTask {
    Area Lumby = new Area.Rectangular(new Coordinate(3213,3237,0), new Coordinate(3224,3250,0));

    private Steal steal = new Steal();

    @Override
    public boolean validate() {
        return Players.getLocal() != null && Distance.between(Lumby,Players.getLocal()) <= 10;
    }

    @Override
    public TreeTask failureTask() {
        return steal;
    }

    @Override
    public TreeTask successTask() {
        return steal;
    }
}
