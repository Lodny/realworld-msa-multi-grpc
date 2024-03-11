package com.lodny.rwproxy.entity.wrapper;


import com.lodny.rwproxy.entity.dto.CommentResponse;

import java.util.List;

public record WrapCommentResponses(List<CommentResponse> comments) { }
