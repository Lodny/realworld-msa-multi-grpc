package com.lodny.rwcommon.util;

import org.springframework.util.StringUtils;

public class ImageUtil {
    private static final String defaultImage = "https://api.realworld.io/images/demo-avatar.png";
    public static String nullToDefaultImage(final String image) {
        return StringUtils.hasText(image) ? image : defaultImage;
    }

    public static String defaultImageToNull(final String image) {
        return defaultImage.equals(image) ? null : image;
    }
}
