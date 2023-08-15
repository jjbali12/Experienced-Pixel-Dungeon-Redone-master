/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2020 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.levels.ArenaLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SecondArenaLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class TicketToSecondArena extends Item{

    {
        image = ItemSpriteSheet.TICKET_TO_ARENA_2ND;
        stackable = true;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    private static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    public int depth;
    public int branch;
    public int pos;

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)){
            if (Dungeon.branch != Dungeon.BRANCH_CUSTOM){
                depth = Dungeon.depth;
                branch = Dungeon.branch;
                pos = hero.pos;
                try {
                    Dungeon.saveLevel(GamesInProgress.curSlot);
                } catch (Exception e){
                    Game.reportException(e);
                }
                InterlevelScene.curTransition = new LevelTransition(Dungeon.level, -1, LevelTransition.Type.BRANCH_EXIT, 104, Dungeon.BRANCH_CUSTOM, LevelTransition.Type.BRANCH_ENTRANCE);
                InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
                Game.switchScene( InterlevelScene.class );
                Buff.affect(hero, SecondArenaLevel.SecondArenaCounter.class);
            } else {
//                InterlevelScene.curTransition = new LevelTransition(Dungeon.level, -1, LevelTransition.Type.BRANCH_ENTRANCE, depth, branch, LevelTransition.Type.BRANCH_EXIT);
                InterlevelScene.mode = InterlevelScene.Mode.RETURN;
                InterlevelScene.returnDepth = depth;
                InterlevelScene.returnBranch = branch;
                InterlevelScene.returnPos = pos;
                Game.switchScene( InterlevelScene.class );
                detach(hero.belongings.backpack);
                Buff.detach(hero, SecondArenaLevel.SecondArenaCounter.class);
            }
        }
    }
    private static final String DEPTH	= "depth";
    private static final String BRANCH	= "branch";
    private static final String POS		= "pos";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( DEPTH, depth );
        bundle.put( BRANCH, branch );
        if (depth != -1) {
            bundle.put( POS, pos);
        }
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        depth	= bundle.getInt( DEPTH );
        if (bundle.contains(BRANCH))
            branch	= bundle.getInt( BRANCH );
        else
            branch = Dungeon.BRANCH_NORMAL;
        pos	= bundle.getInt( POS );
    }

    @Override
    public String name() {
        return "Ticket To 2nd Arena";
    }

    @Override
    public String desc() {
        return "Teleports you into the newly created arena!\n\n"
             + "_EVERY CREATURE HERE WILL BECOME OVERPOWERED WHEN YOU LAST LONGER HERE, I WARNED YOU!_";
    }

    @Override
    public int value() {
        return 150000;
    }
}
