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
package mage.sets.commander2015;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfEndStepTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.filter.common.FilterCreatureCard;
import mage.filter.predicate.other.OwnerIdPredicate;
import mage.game.Game;
import mage.players.Player;
import mage.target.common.TargetCardInGraveyard;
import mage.target.common.TargetCardInOpponentsGraveyard;
import mage.target.common.TargetOpponent;
import mage.util.MessageToClient;

/**
 *
 * @author LevelX2
 */
public class DawnbreakReclaimer extends CardImpl {

    public DawnbreakReclaimer(UUID ownerId) {
        super(ownerId, 2, "Dawnbreak Reclaimer", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{4}{W}{W}");
        this.expansionSetCode = "C15";
        this.subtype.add("Angel");
        this.power = new MageInt(5);
        this.toughness = new MageInt(5);

        // Flying
        this.addAbility(FlyingAbility.getInstance());
        // At the beginning of your end step, choose a creature card in an opponent's graveyard, then that player chooses a creature card in your graveyard.
        // You may return those cards to the battlefield under their owners' control.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(new DawnbreakReclaimerEffect(), TargetController.YOU, false));
    }

    public DawnbreakReclaimer(final DawnbreakReclaimer card) {
        super(card);
    }

    @Override
    public DawnbreakReclaimer copy() {
        return new DawnbreakReclaimer(this);
    }
}

class DawnbreakReclaimerEffect extends OneShotEffect {

    public DawnbreakReclaimerEffect() {
        super(Outcome.Detriment);
        this.staticText = "choose a creature card in an opponent's graveyard, then that player chooses a creature card in your graveyard. You may return those cards to the battlefield under their owners' control";
    }

    public DawnbreakReclaimerEffect(final DawnbreakReclaimerEffect effect) {
        super(effect);
    }

    @Override
    public DawnbreakReclaimerEffect copy() {
        return new DawnbreakReclaimerEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        /**
         * 04.11.2015 If any opponent has a creature card in his or her
         * graveyard as Dawnbreak Reclaimer’s ability resolves, then you must
         * choose one of those cards. You can’t choose a different opponent with
         * no creature cards in his or her graveyard to avoid returning one of
         * those cards.
         *
         * 04.11.2015 If there are no creature cards in any opponent’s graveyard
         * as Dawnbreak Reclaimer’s ability resolves, you’ll still have the
         * option to return a creature card from your graveyard to the
         * battlefield. You choose which opponent will choose a creature card in
         * your graveyard.
         */
        Player controller = game.getPlayer(source.getControllerId());
        MageObject sourceObject = source.getSourceObject(game);
        if (controller != null && sourceObject != null) {
            TargetCardInOpponentsGraveyard targetOpponentGraveyard = new TargetCardInOpponentsGraveyard(new FilterCreatureCard("a creature card in an opponent's graveyard"));
            Player opponent = null;
            Card cardOpponentGraveyard = null;
            if (targetOpponentGraveyard.canChoose(source.getSourceId(), source.getControllerId(), game)) {
                controller.choose(Outcome.Detriment, targetOpponentGraveyard, source.getSourceId(), game);
                cardOpponentGraveyard = game.getCard(targetOpponentGraveyard.getFirstTarget());
                if (cardOpponentGraveyard != null) {
                    opponent = game.getPlayer(cardOpponentGraveyard.getOwnerId());
                    game.informPlayers(sourceObject.getLogName() + ": " + controller.getLogName() + " has chosen " + cardOpponentGraveyard.getIdName() + " of " + opponent.getLogName());
                }
            }
            if (opponent == null) {
                // if no card from opponent was available controller has to chose an opponent to select a creature card in controllers graveyard
                TargetOpponent targetOpponent = new TargetOpponent(true);
                controller.choose(outcome, targetOpponent, source.getSourceId(), game);
                opponent = game.getPlayer(targetOpponent.getFirstTarget());
                if (opponent != null) {
                    game.informPlayers(sourceObject.getLogName() + ": " + controller.getLogName() + " has chosen " + opponent.getLogName() + " to select a creature card from his or her graveyard");
                }
            }
            if (opponent != null) {
                FilterCreatureCard filter = new FilterCreatureCard("a creature card in " + controller.getName() + "'s the graveyard");
                filter.add(new OwnerIdPredicate(controller.getId()));
                TargetCardInGraveyard targetControllerGaveyard = new TargetCardInGraveyard(filter);
                targetControllerGaveyard.setNotTarget(true);
                Card controllerCreatureCard = null;
                if (targetControllerGaveyard.canChoose(source.getSourceId(), opponent.getId(), game)
                        && opponent.choose(outcome, targetControllerGaveyard, source.getSourceId(), game)) {
                    controllerCreatureCard = game.getCard(targetControllerGaveyard.getFirstTarget());
                    if (controllerCreatureCard != null) {
                        game.informPlayers(sourceObject.getLogName() + ": " + opponent.getLogName() + " has chosen " + controllerCreatureCard.getIdName() + " of " + controller.getLogName());
                    }
                }
                Set<Card> cards = new HashSet<>();
                if (cardOpponentGraveyard != null) {
                    cards.add(cardOpponentGraveyard);
                }
                if (controllerCreatureCard != null) {
                    cards.add(controllerCreatureCard);
                }
                if (!cards.isEmpty()) {
                    MessageToClient message = new MessageToClient("Return those cards to the battlefield under their owners' control?",
                            "Opponent's creature card: " + (cardOpponentGraveyard == null ? "none" : cardOpponentGraveyard.getLogName())
                            + ", your creature card: " + (controllerCreatureCard == null ? "none" : controllerCreatureCard.getLogName()));
                    if (controller.chooseUse(outcome, message, source, game)) {
                        controller.moveCards(cards, Zone.BATTLEFIELD, source, game, false, false, true, null);
                    }
                }
            }

            return true;
        }
        return false;

    }
}
