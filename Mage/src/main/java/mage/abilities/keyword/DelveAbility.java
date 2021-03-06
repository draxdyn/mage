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
package mage.abilities.keyword;

import java.util.List;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.SpecialAction;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.ExileFromGraveCost;
import mage.abilities.costs.mana.AlternateManaPaymentAbility;
import mage.abilities.costs.mana.ManaCost;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.constants.AbilityType;
import mage.constants.ManaType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.players.ManaPool;
import mage.players.Player;
import mage.target.common.TargetCardInYourGraveyard;
import mage.util.CardUtil;

/**
 * 702.65. Delve 702.65a Delve is a static ability that functions while the
 * spell with delve is on the stack. “Delve” means “For each generic mana in
 * this spell’s total cost, you may exile a card from your graveyard rather than
 * pay that mana.” The delve ability isn’t an additional or alternative cost and
 * applies only after the total cost of the spell with delve is determined.
 * 702.65b Multiple instances of delve on the same spell are redundant.
 *
 * * The rules for delve have changed slightly since it was last in an
 * expansion. Previously, delve reduced the cost to cast a spell. Under the
 * current rules, you exile cards from your graveyard at the same time you pay
 * the spell’s cost. Exiling a card this way is simply another way to pay that
 * cost. * Delve doesn’t change a spell’s mana cost or converted mana cost. For
 * example, Dead Drop’s converted mana cost is 10 even if you exiled three cards
 * to cast it. * You can’t exile cards to pay for the colored mana requirements
 * of a spell with delve. * You can’t exile more cards than the generic mana
 * requirement of a spell with delve. For example, you can’t exile more than
 * nine cards from your graveyard to cast Dead Drop. * Because delve isn’t an
 * alternative cost, it can be used in conjunction with alternative costs.
 *
 * @author LevelX2
 *
 * TODO: Change card exiling to a way to pay mana costs, now it's maybe not
 * passible to pay costs from effects that increase the mana costs.
 */
public class DelveAbility extends SimpleStaticAbility implements AlternateManaPaymentAbility {

    public DelveAbility() {
        super(Zone.STACK, null);
        this.setRuleAtTheTop(true);
    }

    public DelveAbility(final DelveAbility ability) {
        super(ability);
    }

    @Override
    public DelveAbility copy() {
        return new DelveAbility(this);
    }

    @Override
    public String getRule() {
        return "Delve <i>(Each card you exile from your graveyard while casting this spell pays for {1})</i>";
    }

    @Override
    public void addSpecialAction(Ability source, Game game, ManaCost unpaid) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null && controller.getGraveyard().size() > 0) {
            if (unpaid.getMana().getGeneric() > 0 && source.getAbilityType().equals(AbilityType.SPELL)) {
                SpecialAction specialAction = new DelveSpecialAction();
                specialAction.setControllerId(source.getControllerId());
                specialAction.setSourceId(source.getSourceId());
                int unpaidAmount = unpaid.getMana().getGeneric();
                if (!controller.getManaPool().isAutoPayment() && unpaidAmount > 1) {
                    unpaidAmount = 1;
                }
                specialAction.addCost(new ExileFromGraveCost(new TargetCardInYourGraveyard(
                        0, Math.min(controller.getGraveyard().size(), unpaidAmount), new FilterCard())));
                if (specialAction.canActivate(source.getControllerId(), game)) {
                    game.getState().getSpecialActions().add(specialAction);
                }
            }
        }
    }
}

class DelveSpecialAction extends SpecialAction {

    public DelveSpecialAction() {
        super(Zone.ALL, true);
        this.addEffect(new DelveEffect());
    }

    public DelveSpecialAction(final DelveSpecialAction ability) {
        super(ability);
    }

    @Override
    public DelveSpecialAction copy() {
        return new DelveSpecialAction(this);
    }
}

class DelveEffect extends OneShotEffect {

    public DelveEffect() {
        super(Outcome.Benefit);
        this.staticText = "Delve (Each card you exile from your graveyard while casting this spell pays for {1}.)";
    }

    public DelveEffect(final DelveEffect effect) {
        super(effect);
    }

    @Override
    public DelveEffect copy() {
        return new DelveEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            ExileFromGraveCost exileFromGraveCost = (ExileFromGraveCost) source.getCosts().get(0);
            List<Card> exiledCards = exileFromGraveCost.getExiledCards();
            if (exiledCards.size() > 0) {
                ManaPool manaPool = controller.getManaPool();
                manaPool.addMana(new Mana(0, 0, 0, 0, 0, 0, 0, exiledCards.size()), game, source);
                manaPool.unlockManaType(ManaType.COLORLESS);
                String keyString = CardUtil.getCardZoneString("delvedCards", source.getSourceId(), game);
                @SuppressWarnings("unchecked")
                List<Card> delvedCards = (List<Card>) game.getState().getValue(keyString);
                if (delvedCards == null) {
                    game.getState().setValue(keyString, exiledCards);
                } else {
                    delvedCards.addAll(exiledCards);
                }
            }
            return true;
        }
        return false;
    }
}
