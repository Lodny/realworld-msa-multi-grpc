package com.lodny.rwfavorite.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    public static Favorite of(final Long articleId, final Long userId) {
        return new Favorite(new FavoriteId(articleId, userId));
    }
}
