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

package bisq.desktop.primary.main.content.swap.create.components;

import bisq.common.monetary.Market;
import bisq.desktop.common.view.Controller;
import bisq.desktop.common.view.Model;
import bisq.desktop.common.view.View;
import bisq.desktop.components.controls.BisqLabel;
import bisq.desktop.components.table.BisqTableColumn;
import bisq.desktop.components.table.BisqTableView;
import bisq.desktop.components.table.TableItem;
import bisq.i18n.Res;
import bisq.offer.protocol.ProtocolSpecifics;
import bisq.offer.protocol.SwapProtocolType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class ProtocolSelection {
    public static class ProtocolController implements Controller {
        private final ProtocolModel model;
        @Getter
        private final ProtocolView view;
        private final ChangeListener<Market> selectedMarketListener;

        public ProtocolController(OfferPreparationModel offerPreparationModel) {
            model = new ProtocolModel(offerPreparationModel);
            view = new ProtocolView(model, this);

            selectedMarketListener = (observable, oldValue, newValue) -> {
                if (newValue == null) return;
                model.fillObservableList(ProtocolSpecifics.getProtocols(newValue));
            };
        }

        public void onSelectProtocol(SwapProtocolType value) {
            model.setSelectedProtocolType(value);
            model.selectListItem(value);
        }

        public void onViewAttached() {
            model.selectedMarketProperty().addListener(selectedMarketListener);
        }

        public void onViewDetached() {
            model.selectedMarketProperty().removeListener(selectedMarketListener);
        }
    }

    @Getter
    public static class ProtocolModel implements Model {
        @Delegate
        private final OfferPreparationModel offerPreparationModel;
        private final ObservableList<ProtocolItem> observableList = FXCollections.observableArrayList();
        private final SortedList<ProtocolItem> sortedList = new SortedList<>(observableList);
        private final ObjectProperty<ProtocolItem> selectedProtocolItem = new SimpleObjectProperty<>();

        public ProtocolModel(OfferPreparationModel offerPreparationModel) {
            this.offerPreparationModel = offerPreparationModel;
        }

        private void fillObservableList(List<SwapProtocolType> protocols) {
            observableList.setAll(protocols.stream().map(ProtocolItem::new).collect(Collectors.toList()));
        }

        public void selectListItem(SwapProtocolType value) {
            observableList.stream().filter(item -> item.protocolType.equals(value)).findAny()
                    .ifPresent(selectedProtocolItem::set);
        }
    }

    @Getter
    private static class ProtocolItem implements TableItem {
        private final SwapProtocolType protocolType;
        private final String protocolName;

        public ProtocolItem(SwapProtocolType protocolType) {
            this.protocolType = protocolType;
            protocolName = Res.offerbook.get(protocolType.name());
        }

        @Override
        public void activate() {
        }

        @Override
        public void deactivate() {
        }
    }

    public static class ProtocolView extends View<VBox, ProtocolModel, ProtocolController> {
        private final BisqTableView<ProtocolItem> tableView;
        private final ChangeListener<ProtocolItem> selectedProtocolItemListener;
        private final ChangeListener<ProtocolItem> selectedTableItemListener;

        public ProtocolView(ProtocolModel model,
                            ProtocolController controller) {
            super(new VBox(), model, controller);

            Label headline = new BisqLabel(Res.offerbook.get("createOffer.selectProtocol"));
            headline.getStyleClass().add("titled-group-bg-label-active");

            tableView = new BisqTableView<>(model.getSortedList());
            tableView.setFixHeight(130);
            configTableView();

            root.getChildren().addAll(headline, tableView);

            // Listener on table row selection
            selectedTableItemListener = (o, old, newValue) -> {
                if (newValue == null) return;
                controller.onSelectProtocol(newValue.protocolType);
            };

            // Listeners on model change
            selectedProtocolItemListener = (o, old, newValue) -> tableView.getSelectionModel().select(newValue);
        }

        public void onViewAttached() {
            tableView.getSelectionModel().selectedItemProperty().addListener(selectedTableItemListener);
            model.selectedProtocolItem.addListener(selectedProtocolItemListener);
        }

        public void onViewDetached() {
            tableView.getSelectionModel().selectedItemProperty().removeListener(selectedTableItemListener);
            model.selectedProtocolItem.removeListener(selectedProtocolItemListener);
        }

        private void configTableView() {
            tableView.getColumns().add(new BisqTableColumn.Builder<ProtocolItem>()
                    .title(Res.offerbook.get("createOffer.protocol.names"))
                    .minWidth(120)
                    .valueSupplier(ProtocolItem::getProtocolName)
                    .build());
            //todo there will be more info about the protocols
        }
    }
}