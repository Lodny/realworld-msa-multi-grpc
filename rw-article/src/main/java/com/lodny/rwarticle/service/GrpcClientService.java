package com.lodny.rwarticle.service;

import com.lodny.springgrpc01.HelloRequest;
import com.lodny.springgrpc01.HelloResponse;
import com.lodny.springgrpc01.SimpleGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service

public class GrpcClientService {
    @GrpcClient("test")
    private SimpleGrpc.SimpleBlockingStub simpleBlockingStub;

    public String sendMessage(final String name) {
        log.info("sendMessage() : name={}", name);

        try{
            HelloResponse response = simpleBlockingStub.sayHello(HelloRequest.newBuilder().setName(name).build());
            return response.getMessage();
        } catch (StatusRuntimeException e) {
            return "FAILED with " + e.getStatus().getCode().name();
        }
    }
}
