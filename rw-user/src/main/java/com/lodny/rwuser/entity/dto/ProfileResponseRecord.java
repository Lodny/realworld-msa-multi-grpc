package com.lodny.rwuser.entity.dto;

import com.lodny.rwuser.entity.RealWorldUser;
import com.lodny.rwcommon.util.ImageUtil;
import lombok.Builder;

@Builder
public record ProfileResponseRecord(
    String username,
    String bio,
    String image,
    Boolean following
) {
    public ProfileResponseRecord {
        image = ImageUtil.nullToDefaultImage(image);
    }

    public static ProfileResponseRecord of(final RealWorldUser user, final Boolean following) {
        if (user == null)
            return null;

        return new ProfileResponseRecord(
                user.getUsername(),
                user.getBio(),
                user.getImage(),
                following);
    }
}
