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

package bisq.desktop.primary.main.content.social;

import bisq.desktop.NavigationTarget;
import bisq.desktop.common.view.NavigationModel;
import bisq.social.user.profile.UserProfile;
import bisq.social.user.profile.UserProfileService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

@Getter
public class SocialModel extends NavigationModel {

    private final UserProfileService userProfileService;
    private final ObjectProperty<UserProfile> selectedUserProfile = new SimpleObjectProperty<>();

    public SocialModel(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public NavigationTarget getDefaultNavigationTarget() {
        // return NavigationTarget.SETUP_INITIAL_USER_PROFILE;

        return userProfileService.isDefaultUserProfileMissing() ?
                NavigationTarget.SETUP_INITIAL_USER_PROFILE :
                NavigationTarget.CHAT;
    }
}
