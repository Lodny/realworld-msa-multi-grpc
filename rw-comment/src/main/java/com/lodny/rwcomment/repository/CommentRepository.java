package com.lodny.rwcomment.repository;

import com.lodny.rwcomment.entity.Comment;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends Repository<Comment, Long> {
    Comment save(Comment comment);
    Optional<Comment> findById(Long commentId);
    void delete(Comment foundComment);
    List<Comment> findAllByArticleIdOrderByCreatedAtDesc(Long articleId);
}
