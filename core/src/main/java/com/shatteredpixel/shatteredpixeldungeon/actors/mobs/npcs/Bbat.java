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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Marked;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BbatSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Bbat extends Mob {

    public static int level = 1;

    {
        HP = HT = 10;
        defenseSkill = 15;
        baseSpeed = 2f;
        spriteClass = BbatSprite.class;
        alignment = Alignment.ALLY;
        WANDERING = new Wandering();
        intelligentAlly = true;
    }

    public static void saveLevel(Bundle bundle){
        bundle.put("bbatLevel", level);
    }

    public static void loadLevel(Bundle bundle){
        level = bundle.getInt("bbatLevel");
    }

    @Override
    public int damageRoll() {
        if (Dungeon.hero.isSubclass(HeroSubClass.ASSASSIN)){
            int i = Random.NormalIntRange(0, level * 2);
            if (enemy.buff(Marked.class) != null) i *= enemy.buff(Marked.class).bonusDamage();
            return i;
        }
        return Random.NormalIntRange( level, 1 + level * 2 );
    }

    @Override
    public float attackDelay() {
        return super.attackDelay() * (Dungeon.hero.isSubclass(HeroSubClass.ASSASSIN) ? 0.33f : 0.5f);
    }

    @Override
    public int attackSkill(Char target) {
        return 10 + level * 2;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (Dungeon.hero.isSubclass(HeroSubClass.ASSASSIN)) Buff.affect(enemy, Marked.class).stack++;
        return super.attackProc(enemy, damage);
    }

    @Override
    protected Char chooseEnemy() {
        Char enemy = super.chooseEnemy();

        int targetPos = Dungeon.hero.pos;
        int distance = Dungeon.hero.isSubclass(HeroSubClass.ASSASSIN) ? 99999 : 8;

        //will never attack something far from their target
        if (enemy != null
                && (Dungeon.level.mobs.contains(enemy) || Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE))
                && (Dungeon.level.distance(enemy.pos, targetPos) <= distance)){
            if (enemy instanceof Mob)
            ((Mob)enemy).aggro(this);
            return enemy;
        }

        return null;
    }

    public static void updateHP(){
        level += 1;
        if (Dungeon.level != null) {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob instanceof Bbat) {
                    mob.HP = mob.HT = 8 + level * 2;
                    ((Bbat) mob).defenseSkill = 13 + level * 2;
                }
            }
        }
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(dmg, src);
        Buff.affect(Dungeon.hero, ArtifactRecharge.class).prolong(dmg*2);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Buff.affect(Dungeon.hero, BbatRecharge.class, 800f);
    }

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            if ( enemyInFOV ) {

                enemySeen = true;

                notice();
                alerted = true;
                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;

                int oldPos = pos;
                target = Dungeon.hero.pos;
                //always move towards the hero when wandering
                if (getCloser( target )) {
                    if (target != Dungeon.hero.pos) {
                        spend(1 / speed());
                    } else {
                        spend(1 / 100f);
                    }
                    return moveSprite( oldPos, pos );
                } else {
                    spend( TICK );
                }

            }
            return true;
        }

    }

    public static class BbatRecharge extends FlavourBuff {

        public static final float DURATION = 800f;

        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.DEFERRED;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.tint(0, 0, 0, 0.05f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns());
        }

    }
}
