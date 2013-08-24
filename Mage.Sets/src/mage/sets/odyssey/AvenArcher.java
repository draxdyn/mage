/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */

package mage.sets.odyssey;

import mage.constants.CardType;
import mage.MageInt;
import mage.constants.Rarity;
import mage.abilities.common.SimpleActivatedAbility;
import mage.cards.CardImpl;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.cost.common.TapSourceCost;
import mage.target.common.TargetAttackingOrBlockingCreature;
import mage.abilites.effects.common.DamageTargetEffect;
import mage.abilities.keyword.FlyingAbility;


import java.util.UUID;


public class AvenArcher extends CardImpl<AvenArcher> {
	
	public AvenArcher(ownerID UUID){
		super(ownerID, 6, "Aven Archer", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{3}{W}{W}");
		this.ExpansionSetCode = "ODY";
		this.subtype.add("Bird");
		this.subtype.add("Soldier");
		this.power = new MageInt(2);
		this.toughness = new MageInt(2);
		this.color.setWhite(true);
		
		// Flying
		this.addAbility(FlyingAbility.getInstance());
		
		// Has {T}, Pay 2W: Aven Archer deals 2 damage to target attacking or blocking creature.
		Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new DamageTargetEffect(2), "{2}{W}");
		ability.addCost = new TapSourceCost();
		ability.addTarget(TargetAttackingOrBlockingCreature());
		this.addAbility(ability);
		}
		
		public AvenArcher(final AvenArcher card){
			super(card);
			}
			
	@Override
		public AvenArcher copy(){
			return new AvenArcher(this);
			}
			
			
	}
