package guru.qa.niffler.data.projection;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.FriendshipStatus;

import java.util.UUID;

public record UserWithStatus(
        UUID id,
        String username,
        CurrencyValues currency,
        String fullname,
        byte[] photoSmall,
        FriendshipStatus status
) {
    public static UserForBulkResponse toUserForBulkResponse(UserWithStatus userWithStatus) {
        return UserForBulkResponse.newBuilder()
                .setId(userWithStatus.id().toString())
                .setUsername(userWithStatus.username())
                .setFullname(userWithStatus.fullname() != null ? userWithStatus.fullname() : "")
                .setCurrency(guru.qa.niffler.grpc.CurrencyValues.valueOf(userWithStatus.currency().name()))
                .setPhotoSmall(userWithStatus.photoSmall() != null && userWithStatus.photoSmall().length > 0 ? new String(userWithStatus.photoSmall(), StandardCharsets.UTF_8) : "")
                .setFriendshipStatus(guru.qa.niffler.grpc.FriendshipStatus.UNDEFINED)
                .build();
    }
}
