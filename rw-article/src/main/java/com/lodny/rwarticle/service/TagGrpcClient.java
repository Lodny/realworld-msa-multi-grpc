package com.lodny.rwarticle.service;

import com.lodny.rwcommon.grpc.tag.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class TagGrpcClient {

    @GrpcClient("tag-grpc")
    private TagGrpc.TagBlockingStub tagBlockingStub;

    public void registerTags(final Set<String> tags, final Long articleId, final String token) {
        log.info("registerTags() : tags={}", tags);

        tagBlockingStub.registerTags(RegisterTagsRequest.newBuilder()
                        .addAllTags(tags)
                        .setArticleId(articleId)
                        .build());
    }

    public List<Long> getArticleIdsByTagString(final String tagString) {
        log.info("getArticleIdsByTag() : tagString={}", tagString);

        ArticleIdsByTagStringResponse articleIdsByTagString = tagBlockingStub.getArticleIdsByTagString(ArticleIdsByTagStringRequest.newBuilder()
                .setTagString(tagString)
                .build());
        log.info("getArticleIdsByTagString() : articleIdsByTagString={}", articleIdsByTagString);

        return articleIdsByTagString.getArticleIdList();
    }

    public Set<String> getTagStringsByArticleId(final Long articleId) {
        TagStringsByArticleIdResponse response = tagBlockingStub.getTagStringsByArticleId(TagStringsByArticleIdRequest.newBuilder()
                .setArticleId(articleId).build());
        log.info("getTagStringsByArticleId() : response={}", response);

        return new HashSet<>(response.getTagsList());
    }
}
