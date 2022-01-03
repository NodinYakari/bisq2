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

package network.misq.desktop.main.content;

import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import network.misq.desktop.common.view.Controller;
import network.misq.desktop.common.view.Model;
import network.misq.desktop.common.view.View;
import network.misq.desktop.components.controls.AutoTooltipLabel;

public class Dummy {

    public static class DummyModel implements Model {
    }

    public static class DummyController implements Controller {
        @Override
        public View<Parent, Model, Controller> getView() {
            return null;
        }
    }

    public static class DummyView extends View<StackPane, DummyModel, DummyController> {
        public DummyView(String label) {
            super(new StackPane(), new DummyModel(), new DummyController());
            root.getChildren().add(new AutoTooltipLabel(label));
        }
    }
}
