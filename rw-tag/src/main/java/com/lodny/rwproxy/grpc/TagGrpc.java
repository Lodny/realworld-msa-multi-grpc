package com.lodny.rwproxy.grpc;

import com.lodny.rwcommon.grpc.tag.Empty;
import com.lodny.rwcommon.grpc.tag.RegisterTagsRequest;
import com.lodny.rwcommon.grpc.tag.Top10TagsResponse;
import com.lodny.rwproxy.entity.Tag;
import com.lodny.rwproxy.repository.TagRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TagGrpc extends com.lodny.rwcommon.grpc.tag.TagGrpc.TagImplBase {

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
    public void getTop10TagString(final Empty request, final StreamObserver<Top10TagsResponse> responseObserver) {
        List<String> top10Tags = tagRepository.getTop10Tags().stream()
                .map(tag -> tag[0]).toList();
        log.info("getTop10Tags() : top10Tags={}", top10Tags);

        Top10TagsResponse top10TagsResponse = Top10TagsResponse.newBuilder()
                .addAllTags(top10Tags)
                .build();
        log.info("getTop10Tags() : top10TagsResponse={}", top10TagsResponse);

        responseObserver.onNext(top10TagsResponse);
        responseObserver.onCompleted();
    }
}
