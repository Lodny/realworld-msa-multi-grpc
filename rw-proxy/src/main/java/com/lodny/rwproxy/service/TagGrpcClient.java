package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.tag.Empty;
import com.lodny.rwcommon.grpc.tag.RegisterTagsRequest;
import com.lodny.rwcommon.grpc.tag.TagGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
//@RequiredArgsConstructor
public class TagGrpcClient {

    @GrpcClient("tag-grpc")
    private TagGrpc.TagBlockingStub tagBlockingStub;

    public void registerTags(final Set<String> tags, final Long articleId, final String token) {
        log.info("registerTags() : tags={}", tags);

//        CallOptions.Key<String> metaDataKey = CallOptions.Key.create("X-Service-Type");
        Empty tag = tagBlockingStub.registerTags(RegisterTagsRequest.newBuilder()
                        .addAllTags(tags)
                        .setArticleId(articleId)
                        .build());
//        } catch (StatusRuntimeException e) {
//            return "FAILED with " + e.getStatus().getCode().name();
//        }
    }
}
