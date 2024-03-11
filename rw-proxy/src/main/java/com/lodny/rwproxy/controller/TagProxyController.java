package com.lodny.rwproxy.controller;

import com.lodny.rwproxy.entity.wrapper.WrapTag10Response;
import com.lodny.rwproxy.service.TagGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TagProxyController {

    private final TagGrpcClient tagGrpcClient;

//    @JwtTokenRequired
//    @PostMapping("/articles/{articleId}/tags/list")
//    public ResponseEntity<?> registerTags(@PathVariable final Long articleId,
//                                          @RequestBody final Set<String> tags) {
//        log.info("registerTags() : articleId={}", articleId);
//        log.info("registerTags() : tags={}", tags);
//        int count = proxyService.registerTags(articleId, tags);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(count);
//    }
//
//    @GetMapping("/articles/{articleId}/tags")
//    public ResponseEntity<?> getTags(@PathVariable final Long articleId) {
//        log.info("getTags() : articleId={}", articleId);
//        Set<Tag> tags = proxyService.getTags(articleId);
//        log.info("getTags() : tags={}", tags);
//
//        return ResponseEntity.ok(tags.stream().map(Tag::getTag).collect(Collectors.toSet()));
//    }

    @GetMapping("/tags")
    public ResponseEntity<?> getTop10Tags() {
        log.info("getTop10Tags() : 1={}", 1);

        List<String> top10Tags = tagGrpcClient.getTop10Tags();
        log.info("getTop10Tags() : top10Tags={}", top10Tags);

        return ResponseEntity.ok(new WrapTag10Response(top10Tags));
    }

//    @GetMapping("/tags/{tag}/article-ids")
//    public ResponseEntity<?> getArticleIdsByTag(@PathVariable String tag) {
//        log.info("getArticleIdsByTag() : tag={}", tag);
//
//        List<Long> articleIds = proxyService.getArticleIdsByTag(tag);
//        log.info("getArticleIdsByTag() : articleIds={}", articleIds);
//
//        return ResponseEntity.ok(articleIds);
//    }
//
//    @JwtTokenRequired
//    @DeleteMapping("/tags/{articleId}")
//    public ResponseEntity<?> deleteTagsByArticleId(@PathVariable Long articleId) {
//        log.info("deleteTagsByArticleId() : articleId={}", articleId);
//
//        List<Tag> tags = proxyService.deleteTagsByArticleId(articleId);
//
//        return ResponseEntity.ok(tags.stream().map(Tag::getId).toList());
//    }
}
