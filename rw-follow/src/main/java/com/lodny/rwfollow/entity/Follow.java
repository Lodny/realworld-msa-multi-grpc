package com.lodny.rwfollow.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Follow {

    @EmbeddedId
    private FollowId id;

    public static Follow of(final Long followerId, final Long followeeId) {
        return new Follow(new FollowId(followerId, followeeId));
    }
}
