package com.lodny.rwuser.entity;

import com.lodny.rwuser.entity.dto.RegisterUserRequest;
import com.lodny.rwuser.entity.dto.UpdateUserRequest;
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


    public static RealWorldUser of(final RegisterUserRequest request) {
        return RealWorldUser.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .build();
    }

    public void update(final UpdateUserRequest updateUserRequest) {
        if (StringUtils.hasText(updateUserRequest.email())) {
            email = updateUserRequest.email();
        }
        if (StringUtils.hasText(updateUserRequest.username())) {
            username = updateUserRequest.username();
        }
        if (StringUtils.hasText(updateUserRequest.bio())) {
            bio = updateUserRequest.bio();
        }
        this.image = ImageUtil.defaultImageToNull(image);
//        final String image = updateUserRequest.image();
//        if (StringUtils.hasText(image)) {
//        }
        if (StringUtils.hasText(updateUserRequest.password())) {
            password = updateUserRequest.password();
        }
    }
}
