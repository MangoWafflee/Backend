package com.example.MangoWafflee.DTO;

import com.example.MangoWafflee.Entity.UserEntity;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {
    private Long id;
    private String uid;
    private String password;
    private String nickname;
    private String image;

    public static UserDTO entityToDto(UserEntity userEntity) {
        return new UserDTO(
                userEntity.getId(),
                userEntity.getUid(),
                userEntity.getPassword(),
                userEntity.getNickname(),
                userEntity.getImage()
        );
    }

    public UserEntity dtoToEntity() {
        return new UserEntity(id, uid, password, nickname, image);
    }
}

