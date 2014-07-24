package com.vijaysharma.gezzoo.service.helpers;

import com.google.common.base.Strings;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.helpers.ProfileHelper;
import com.vijaysharma.gezzoo.response.ProfileResponse;

public class ProfileResourceHelper {
	private ProfileHelper profileHelper;

	public ProfileResourceHelper(ProfileHelper profileHelper) {
		this.profileHelper = profileHelper;
	}
	
	public ProfileResponse login(String id) {
		Profile profile = null;
		if ( Strings.isNullOrEmpty(id) ) {
			profile = profileHelper.createUserProfile();
		} else {
			profile = profileHelper.loadUserProfile(id);
			if ( profile == null ) {
				profile = profileHelper.createUserProfile();
			}
		}
		
		return ProfileResponse.from(profile);
	}
}
