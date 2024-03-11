package com.lodny.rwproxy.repository;

import com.lodny.rwproxy.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Set<Tag> findAllByArticleId(Long articleId);

    @Query("SELECT t.tag, COUNT(t) AS tagCount FROM Tag t GROUP BY t.tag ORDER BY tagCount DESC")
    List<String[]> getTopTags(final Pageable pageable);

    List<Tag> findAllByTag(String tag);

    List<Tag> deleteAllByArticleId(Long articleId);
}
