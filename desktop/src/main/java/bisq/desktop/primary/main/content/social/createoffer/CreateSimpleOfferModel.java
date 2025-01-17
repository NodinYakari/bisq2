/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.desktop.primary.main.content.social.createoffer;

import bisq.account.settlement.SettlementMethod;
import bisq.common.monetary.Market;
import bisq.common.monetary.Monetary;
import bisq.common.monetary.Quote;
import bisq.desktop.common.view.Model;
import bisq.offer.Offer;
import bisq.offer.spec.Direction;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

@Slf4j
@Getter
public class CreateSimpleOfferModel implements Model {
    @Setter
    private Market selectedMarket;
    @Setter
    private Direction direction;
    @Setter
    private Monetary baseSideAmount;
    @Setter
    private Monetary quoteSideAmount;
    @Setter
    private Quote fixPrice;

    private ObservableSet<SettlementMethod> selectedQuoteSideSettlementMethods = FXCollections.observableSet(new HashSet<>());

    private final ObjectProperty<Offer> offerProperty = new SimpleObjectProperty<>();
    private final BooleanProperty createOfferButtonVisibleProperty = new SimpleBooleanProperty(true);

    public CreateSimpleOfferModel() {
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public Offer getOffer() {
        return offerProperty.get();
    }

    public ReadOnlyBooleanProperty createOfferButtonVisibleProperty() {
        return createOfferButtonVisibleProperty;
    }

    public void setAllSelectedQuoteSideSettlementMethods(ObservableSet<SettlementMethod> set) {
        selectedQuoteSideSettlementMethods.clear();
        selectedQuoteSideSettlementMethods.addAll(set);
    }
}
