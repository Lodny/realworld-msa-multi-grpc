package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.grpc.tag.*;
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

        tagRepository.saveAll(request.getTagsList().stream()
                .map(tag -> new Tag(articleId, tag))
                .toList());

        responseObserver.onNext(Common.Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getTopTagStrings(final GrpcTopTagStringsRequest request, final StreamObserver<GrpcTopTagStringsResponse> responseObserver) {
        PageRequest pageRequest = PageRequest.of(0, request.getCount());
        log.info("getTopTagStrings() : pageRequest={}", pageRequest);

        List<String> topTags = tagRepository
                .getTopTags(pageRequest).stream()
                .map(tag -> tag[0]).toList();
        log.info("getTopTagStrings() : topTags={}", topTags);

        GrpcTopTagStringsResponse response = GrpcTopTagStringsResponse.newBuilder()
                .addAllTags(topTags)
                .build();
        log.info("getTopTagStrings() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTagStringsByArticleId(final Common.GrpcArticleIdRequest request, final StreamObserver<GrpcTagStringsByArticleIdResponse> responseObserver) {
        List<String> tags = tagRepository.findAllByArticleId(request.getArticleId())
                .stream().map(Tag::getTag)
                .toList();
        log.info("getTagsByArticleId() : tags={}", tags);

        GrpcTagStringsByArticleIdResponse response = GrpcTagStringsByArticleIdResponse.newBuilder()
                .addAllTags(tags)
                .build();
        log.info("getTagStringsByArticleId() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
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

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteTagsByArticleId(final GrpcTagsByArticleIdRequest request, final StreamObserver<Common.Empty> responseObserver) {
        log.info("deleteTagsByArticleId() :");

        tagRepository.deleteAllByArticleId(request.getArticleId());

        responseObserver.onNext(Common.Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
