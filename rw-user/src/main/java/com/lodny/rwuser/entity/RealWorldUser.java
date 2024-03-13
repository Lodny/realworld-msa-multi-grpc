package com.lodny.rwuser.entity;

import com.lodny.rwcommon.grpc.rwuser.GrpcRegisterUserRequest;
import com.lodny.rwcommon.grpc.rwuser.GrpcUpdateUserRequest;
import com.lodny.rwcommon.util.ImageUtil;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RealWorldUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;
    private String password;

    private String bio;
    private String image;


    public static RealWorldUser of(final GrpcRegisterUserRequest request) {
        return RealWorldUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public void update(final GrpcUpdateUserRequest grpcRequest) {
        if (StringUtils.hasText(grpcRequest.getEmail()))
            email = grpcRequest.getEmail();

        if (StringUtils.hasText(grpcRequest.getUsername()))
            username = grpcRequest.getUsername();

        if (StringUtils.hasText(grpcRequest.getPassword()))
            password = grpcRequest.getPassword();

        if (StringUtils.hasText(grpcRequest.getBio()))
            bio = grpcRequest.getBio();

        if (StringUtils.hasText(grpcRequest.getBio()))
            bio = grpcRequest.getBio();

        image = grpcRequest.getImage();
        image = ImageUtil.defaultImageToNull(image);
    }
}
