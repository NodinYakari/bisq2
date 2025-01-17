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

package bisq.common.monetary;

import bisq.common.proto.Proto;
import lombok.Setter;

public record Market(String baseCurrencyCode, String quoteCurrencyCode) implements Proto {
    public bisq.common.protobuf.Market toProto() {
        return bisq.common.protobuf.Market.newBuilder()
                .setBaseCurrencyCode(baseCurrencyCode)
                .setQuoteCurrencyCode(quoteCurrencyCode)
                .build();
    }

    public static Market fromProto(bisq.common.protobuf.Market proto) {
        return new Market(proto.getBaseCurrencyCode(), proto.getQuoteCurrencyCode());
    }

    @Setter
    private static String QUOTE_SEPARATOR = "/";

    @Override
    public String toString() {
        return baseCurrencyCode + QUOTE_SEPARATOR + quoteCurrencyCode;
    }
}