package com.lodny.rwtag.service;

import com.lodny.springgrpc01.HelloRequest;
import com.lodny.springgrpc01.HelloResponse;
import com.lodny.springgrpc01.SimpleGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class GrpcServerService extends SimpleGrpc.SimpleImplBase {
    @Override
    public void sayHello(final HelloRequest request, final StreamObserver<HelloResponse> responseObserver) {
        log.info("sayHello() : ");

        HelloResponse helloResponse = HelloResponse.newBuilder()
                .setMessage("Hello ==> " + request.getName())
                .build();
        log.info("sayHello() : helloResponse={}", helloResponse);

        responseObserver.onNext(helloResponse);
        responseObserver.onCompleted();
    }
}
