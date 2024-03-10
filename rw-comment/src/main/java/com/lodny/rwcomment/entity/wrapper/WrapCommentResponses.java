package com.lodny.rwcomment.entity.wrapper;

import com.lodny.rwcomment.entity.dto.CommentResponse;

import java.util.List;

public record WrapCommentResponses(List<CommentResponse> comments) { }
