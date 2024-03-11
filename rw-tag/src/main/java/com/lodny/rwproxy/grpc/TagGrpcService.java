package com.lodny.rwproxy.grpc;

import com.lodny.rwcommon.grpc.tag.*;
import com.lodny.rwproxy.entity.Tag;
import com.lodny.rwproxy.repository.TagRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TagGrpcService extends com.lodny.rwcommon.grpc.tag.TagGrpc.TagImplBase {

    private final TagRepository tagRepository;

    @Override
    public void registerTags(final RegisterTagsRequest request, final StreamObserver<Empty> responseObserver) {
        long articleId = request.getArticleId();
        log.info("registerTags() : articleId={}", articleId);

        tagRepository.saveAll(
                request.getTagsList().stream()
                        .map(tag -> new Tag(articleId, tag))
                        .toList());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getTopTagStrings(final TopTagStringsRequest request, final StreamObserver<TopTagStringsResponse> responseObserver) {
        PageRequest pageRequest = PageRequest.of(0, request.getCount());
        log.info("getTopTagStrings() : pageRequest={}", pageRequest);

        List<String> topTags = tagRepository
                .getTopTags(pageRequest).stream()
                .map(tag -> tag[0]).toList();
        log.info("getTopTagStrings() : topTags={}", topTags);

        TopTagStringsResponse response = TopTagStringsResponse.newBuilder()
                .addAllTags(topTags)
                .build();
        log.info("getTopTagStrings() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTagStringsByArticleId(final TagStringsByArticleIdRequest request, final StreamObserver<TagStringsByArticleIdResponse> responseObserver) {
        Set<String> tags = tagRepository.findAllByArticleId(request.getArticleId())
                .stream().map(Tag::getTag)
                .collect(Collectors.toSet());
        log.info("getTagsByArticleId() : tags={}", tags);

        TagStringsByArticleIdResponse response = TagStringsByArticleIdResponse.newBuilder()
                .addAllTags(tags)
                .build();
        log.info("getTagStringsByArticleId() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getArticleIdsByTagString(final ArticleIdsByTagStringRequest request, final StreamObserver<ArticleIdsByTagStringResponse> responseObserver) {
        List<Long> articleIds = tagRepository.findAllByTag(request.getTagString())
                .stream().map(Tag::getArticleId)
                .toList();
        log.info("getArticleIdsByTagString() : articleIds={}", articleIds);

        ArticleIdsByTagStringResponse response = ArticleIdsByTagStringResponse.newBuilder()
                .addAllArticleId(articleIds)
                .build();
        log.info("getArticleIdsByTagString() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteTagsByArticleId(final TagsByArticleIdRequest request, final StreamObserver<Empty> responseObserver) {
        log.info("deleteTagsByArticleId() :");

        tagRepository.deleteAllByArticleId(request.getArticleId());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
