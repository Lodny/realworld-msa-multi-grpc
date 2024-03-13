package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.grpc.tag.*;
import com.lodny.rwcommon.util.CommonGrpcUtil;
import com.lodny.rwproxy.entity.Tag;
import com.lodny.rwproxy.repository.TagRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TagGrpcService extends com.lodny.rwcommon.grpc.tag.TagGrpc.TagImplBase {

    private final TagRepository tagRepository;

    @Override
    public void registerTags(final GrpcRegisterTagsRequest request, final StreamObserver<Common.Empty> responseObserver) {
        long articleId = request.getArticleId();
        log.info("registerTags() : articleId={}", articleId);
        if (articleId == 0L)
            throw new IllegalArgumentException("articleId not found");

        tagRepository.saveAll(request.getTagStringList().stream()
                .map(tag -> new Tag(articleId, tag))
                .toList());

        CommonGrpcUtil.completeResponseObserver(responseObserver);
    }

    @Override
    public void getTopTagStrings(final GrpcTopTagStringsRequest request, final StreamObserver<GrpcTagStringsResponse> responseObserver) {
        PageRequest pageRequest = PageRequest.of(0, request.getCount());
        log.info("getTopTagStrings() : pageRequest={}", pageRequest);

        List<String> topTags = tagRepository
                .getTopTags(pageRequest).stream()
                .map(tag -> tag[0]).toList();
        log.info("getTopTagStrings() : topTags={}", topTags);

        GrpcTagStringsResponse response = GrpcTagStringsResponse.newBuilder()
                .addAllTagString(topTags)
                .build();
        log.info("getTopTagStrings() : response={}", response);

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
    }

    @Override
    public void getTagStringsByArticleId(final Common.GrpcArticleIdRequest request, final StreamObserver<GrpcTagStringsResponse> responseObserver) {
        List<String> tags = tagRepository.findAllByArticleId(request.getArticleId())
                .stream().map(Tag::getTag)
                .toList();
        log.info("getTagsByArticleId() : tags={}", tags);

        GrpcTagStringsResponse response = GrpcTagStringsResponse.newBuilder()
                .addAllTagString(tags)
                .build();
        log.info("getTagStringsByArticleId() : response={}", response);

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
    }

    @Override
    public void getArticleIdsByTagString(final GrpcArticleIdsByTagStringRequest request, final StreamObserver<GrpcArticleIdsByTagStringResponse> responseObserver) {
        List<Long> articleIds = tagRepository.findAllByTag(request.getTagString())
                .stream().map(Tag::getArticleId)
                .toList();
        log.info("getArticleIdsByTagString() : articleIds={}", articleIds);

        GrpcArticleIdsByTagStringResponse response = GrpcArticleIdsByTagStringResponse.newBuilder()
                .addAllArticleId(articleIds)
                .build();
        log.info("getArticleIdsByTagString() : response={}", response);

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
    }

    @Override
    public void deleteTagsByArticleId(final Common.GrpcArticleIdRequest request, final StreamObserver<Common.Empty> responseObserver) {
        log.info("deleteTagsByArticleId() :");

        tagRepository.deleteAllByArticleId(request.getArticleId());

        CommonGrpcUtil.completeResponseObserver(responseObserver);
    }
}
