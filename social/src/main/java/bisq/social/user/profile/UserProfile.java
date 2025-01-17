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

package bisq.social.user.profile;

import bisq.common.proto.Proto;
import bisq.identity.Identity;
import bisq.security.DigestUtil;
import bisq.social.user.ChatUser;
import bisq.social.user.Entitlement;
import bisq.social.user.UserNameGenerator;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Local user profile. Not shared over network.
 */
public record UserProfile(Identity identity, Set<Entitlement> entitlements) implements Proto {
    public ChatUser chatUser() {
        return new ChatUser(identity.networkId(), entitlements);
    }

    public bisq.social.protobuf.UserProfile toProto() {
        return bisq.social.protobuf.UserProfile.newBuilder()
                .setIdentity(identity.toProto())
                .addAllEntitlements(entitlements.stream().map(Entitlement::toProto).collect(Collectors.toList()))
                .build();
    }

    public static UserProfile fromProto(bisq.social.protobuf.UserProfile proto) {
        return new UserProfile(Identity.fromProto(proto.getIdentity()),
                proto.getEntitlementsList().stream().map(Entitlement::fromProto).collect(Collectors.toSet()));
    }

    public String userName() {
        return UserNameGenerator.fromHash(DigestUtil.hash(identity.networkId().getPubKey().publicKey().getEncoded()));
    }
}