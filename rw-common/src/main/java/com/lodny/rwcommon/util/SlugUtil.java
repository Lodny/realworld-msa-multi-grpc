package com.lodny.rwcommon.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

public class SlugUtil {

    public static String createSlug(final String input) {
        if (input == null)
            throw new IllegalArgumentException("Input string cannot be null");

        // Unicode 문자를 ASCII로 정규화
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // 특수 문자 제거
        String slug = normalized.replaceAll("[^\\w\\s-]", "");
        // 공백을 하이픈으로 대체
        slug = slug.replaceAll("\\s+", "-");
        // 중복된 하이픈 제거
        slug = slug.replaceAll("-{2,}", "-");
        // 소문자로 변환
        slug = slug.toLowerCase(Locale.ENGLISH);
        // 양 끝의 하이픈 제거
        slug = slug.replaceAll("^-|-$", "");

        return slug + "-" + UUID.randomUUID();
    }
}
