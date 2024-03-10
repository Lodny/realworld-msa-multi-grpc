package com.lodny.rwarticle.entity.dto;

import org.springframework.util.StringUtils;

public record ArticleParam(
        String tag,
        String author,
        String favorited,
        Integer offset,
        Integer limit,
        String type
) {
    public ArticleParam {
        if (offset == null) offset = 0;
        if (limit == null) limit = 20;

        if (StringUtils.hasText(tag))
            type = "tag";
        else if (StringUtils.hasText(author))
            type = "author";
        else if (StringUtils.hasText(favorited))
            type = "favorited";
        else
            type = "default";
    }
}
