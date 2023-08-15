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
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ExpGenerator extends Item {
    {
        image = ItemSpriteSheet.MAGIC_INFUSE;
        defaultAction = AC_THROW;
        identify();
        stackable = false;
    }

//    @Override
//    public boolean doPickUp(Hero hero) {
//        ExpGenerator generator = hero.belongings.getItem(ExpGenerator.class);
//        if (generator == null) return super.doPickUp(hero, pos, time)hero);
//        else {
//            GameScene.pickUp( this, hero.pos );
//            Sample.INSTANCE.play( Assets.Sounds.ITEM );
//            hero.spendAndNext( TIME_TO_PICK_UP );
//            generator.upgrade();
//            return true;
//        }
//    }

    @Override
    protected void onThrow(int cell) {
        if (!Dungeon.level.passable[cell] || Dungeon.bossLevel()){
            super.onThrow(cell);
        } else {
            com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ExpGenerator generator = new com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ExpGenerator();
            generator.pos = cell;
            generator.set(level());

            GameScene.add( generator );
            ScrollOfTeleportation.appear(generator, cell);
        }
    }

    @Override
    public int level() {
        return super.level() + 1;
    }

    @Override
    public int visiblyUpgraded() {
        return super.level();
    }

    @Override
    public int value() {
        return 120;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Math.round((Dungeon.escalatingDepth()/5) * (1.5f * level())*Dungeon.IntRange(1, 100)));
    }
}
