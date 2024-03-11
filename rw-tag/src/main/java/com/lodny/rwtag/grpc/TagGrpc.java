package com.lodny.rwtag.grpc;

import com.google.protobuf.ProtocolStringList;
import com.lodny.rwcommon.grpc.tag.Empty;
import com.lodny.rwcommon.grpc.tag.RegisterTagsRequest;
import com.lodny.rwtag.entity.Tag;
import com.lodny.rwtag.repository.TagRepository;
import com.lodny.rwtag.service.TagService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TagGrpc extends com.lodny.rwcommon.grpc.tag.TagGrpc.TagImplBase {

    private final TagRepository tagRepository;

    @Override
    public void registerTags(final RegisterTagsRequest request, final StreamObserver<Empty> responseObserver) {
        ProtocolStringList tagsList = request.getTagsList();
        long articleId = request.getArticleId();
        log.info("registerTags() : tagsList={}", tagsList);
        log.info("registerTags() : articleId={}", articleId);

        tagRepository.saveAll(tagsList.stream()
                .map(tag -> new Tag(articleId, tag))
                .toList());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    //    @Override
//    public void getTop10Tags(final Empty request, final StreamObserver<Top10TagsResponse> responseObserver) {
//        List<String> top10Tags = tagService.getTop10Tags();
//        log.info("getTop10Tags() : top10Tags={}", top10Tags);
//
//        Top10TagsResponse top10TagsResponse = Top10TagsResponse.newBuilder()
//                .addAllTags(top10Tags)
//                .build();
//        log.info("getTop10Tags() : top10TagsResponse={}", top10TagsResponse);
//
//        responseObserver.onNext(top10TagsResponse);
//        responseObserver.onCompleted();
//    }
}
