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

package network.misq.desktop.main.content.networkinfo.transport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import network.misq.application.DefaultServiceProvider;
import network.misq.common.data.Pair;
import network.misq.desktop.common.threading.UIThread;
import network.misq.desktop.common.view.Model;
import network.misq.i18n.Res;
import network.misq.network.NetworkService;
import network.misq.network.p2p.NetworkId;
import network.misq.network.p2p.ServiceNode;
import network.misq.network.p2p.message.Message;
import network.misq.network.p2p.node.CloseReason;
import network.misq.network.p2p.node.Connection;
import network.misq.network.p2p.node.Node;
import network.misq.network.p2p.node.transport.Transport;
import network.misq.security.KeyPairService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Getter
public class TransportTypeModel implements Model {
    private final NetworkService networkService;
    private final KeyPairService keyPairService;
    private final Transport.Type transportType;
    private final ObservableList<ConnectionListItem> connectionListItems = FXCollections.observableArrayList();
    private final FilteredList<ConnectionListItem> filteredConnectionListItems = new FilteredList<>(connectionListItems);
    private final SortedList<ConnectionListItem> sortedConnectionListItems = new SortedList<>(filteredConnectionListItems);
    private final StringProperty myDefaultNodeAddress = new SimpleStringProperty(Res.common.get("na"));
    private final StringProperty nodeIdString = new SimpleStringProperty();
    private final StringProperty messageReceiver = new SimpleStringProperty();
    private final StringProperty receivedMessages = new SimpleStringProperty("");
    private final Collection<Node> allNodes;
    private final Node defaultNode;
    private final Optional<NetworkId> selectedNetworkId = Optional.empty();
    private final Map<String, Node.Listener> nodeListenersByNodeId = new HashMap<>();


    public TransportTypeModel(DefaultServiceProvider serviceProvider, Transport.Type transportType) {
        networkService = serviceProvider.getNetworkService();
        keyPairService = serviceProvider.getKeyPairService();
        this.transportType = transportType;

        Optional<ServiceNode> serviceNode = networkService.findServiceNode(transportType);
        checkArgument(serviceNode.isPresent(), "ServiceNode must be present");
        defaultNode = serviceNode.get().getDefaultNode();
        defaultNode.findMyAddress().ifPresent(e -> myDefaultNodeAddress.set(e.getFullAddress()));
        allNodes = serviceNode.get().getNodesById().getAllNodes();

        connectionListItems.setAll(allNodes.stream()
                .flatMap(node -> node.getAllConnections().map(c -> new Pair<>(c, node.getNodeId())))
                .map(pair -> new ConnectionListItem(pair.first(), pair.second()))
                .collect(Collectors.toList()));
    }


    public void activate() {
        allNodes.forEach(node -> {
            Node.Listener nodeListener = new Node.Listener() {
                @Override
                public void onMessage(Message message, Connection connection, String nodeId) {
                    UIThread.run(() -> maybeUpdateMyAddress(node));
                }

                @Override
                public void onConnection(Connection connection) {
                    UIThread.run(() -> {
                        connectionListItems.add(new ConnectionListItem(connection, node.getNodeId()));
                        maybeUpdateMyAddress(node);
                    });
                }

                @Override
                public void onDisconnect(Connection connection, CloseReason closeReason) {
                    UIThread.run(() -> {
                        connectionListItems.remove(new ConnectionListItem(connection, node.getNodeId()));
                        maybeUpdateMyAddress(node);
                    });

                }
            };
            node.addListener(nodeListener);
            nodeListenersByNodeId.put(node.getNodeId(), nodeListener);
        });
    }

    private void maybeUpdateMyAddress(Node node) {
        if (node.getNodeId().equals(defaultNode.getNodeId())) {
            defaultNode.findMyAddress().ifPresent(e -> myDefaultNodeAddress.set(e.getFullAddress()));
        }
    }

    public void deactivate() {
        allNodes.forEach(node -> node.removeListener(nodeListenersByNodeId.get(node.getNodeId())));
    }
}