package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("fullname")
        String fullname,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("photoSmall")
        String photoSmall,
        @JsonProperty("auth")
        AuthUserJson authUserJson) {

    public static UserJson fromAuthEntity(AuthUserEntity authUserJson) {
        return new UserJson(
                authUserJson.getId(),
                authUserJson.getUsername(),
                null,
                null,
                null,
                null,
                null,
                null,
                AuthUserJson.fromEntity(authUserJson)
        );
    }

    public static UserJson fromEntity(UserEntity userEntity) {
        return new UserJson(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getSurname(),
                userEntity.getFullname(),
                userEntity.getCurrency(),
                userEntity.getPhoto() != null && userEntity.getPhoto().length > 0 ? new String(userEntity.getPhoto(), StandardCharsets.UTF_8) : null,
                userEntity.getPhotoSmall() != null && userEntity.getPhotoSmall().length > 0 ? new String(userEntity.getPhotoSmall(), StandardCharsets.UTF_8) : null,
                null
        );
    }

}