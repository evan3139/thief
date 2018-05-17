package com.bravebots.bravethieving.thief;

import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;


public class BraveThieving extends TreeBot{

    @Override
    public TreeTask createRootTask(){
        return new InLumbridge();
    }
}
