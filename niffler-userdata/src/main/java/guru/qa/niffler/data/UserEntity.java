package guru.qa.niffler.data;

import guru.qa.niffler.grpc.UserResponse;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyValues currency;

    @Column()
    private String firstname;

    @Column()
    private String surname;

    @Column(name = "full_name")
    private String fullname;

    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;

    @Column(name = "photo_small", columnDefinition = "bytea")
    private byte[] photoSmall;

    @OneToMany(mappedBy = "requester", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendshipEntity> friendshipRequests = new ArrayList<>();

    @OneToMany(mappedBy = "addressee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendshipEntity> friendshipAddressees = new ArrayList<>();

    public void addFriends(FriendshipStatus status, UserEntity... friends) {
        List<FriendshipEntity> friendsEntities = Stream.of(friends)
                .map(f -> {
                    FriendshipEntity fe = new FriendshipEntity();
                    fe.setRequester(this);
                    fe.setAddressee(f);
                    fe.setStatus(status);
                    fe.setCreatedDate(new Date());
                    return fe;
                }).toList();
        this.friendshipRequests.addAll(friendsEntities);
    }

    public void addInvitations(UserEntity... invitations) {
        List<FriendshipEntity> invitationsEntities = Stream.of(invitations)
                .map(i -> {
                    FriendshipEntity fe = new FriendshipEntity();
                    fe.setRequester(i);
                    fe.setAddressee(this);
                    fe.setStatus(FriendshipStatus.PENDING);
                    fe.setCreatedDate(new Date());
                    return fe;
                }).toList();
        this.friendshipAddressees.addAll(invitationsEntities);
    }

    public void removeFriends(UserEntity... friends) {
        List<UUID> idsToBeRemoved = Arrays.stream(friends).map(UserEntity::getId).toList();
        for (Iterator<FriendshipEntity> i = getFriendshipRequests().iterator(); i.hasNext(); ) {
            FriendshipEntity friendsEntity = i.next();
            if (idsToBeRemoved.contains(friendsEntity.getAddressee().getId())) {
                friendsEntity.setAddressee(null);
                i.remove();
            }
        }
    }

    public void removeInvites(UserEntity... invitations) {
        List<UUID> idsToBeRemoved = Arrays.stream(invitations).map(UserEntity::getId).toList();
        for (Iterator<FriendshipEntity> i = getFriendshipAddressees().iterator(); i.hasNext(); ) {
            FriendshipEntity friendsEntity = i.next();
            if (idsToBeRemoved.contains(friendsEntity.getRequester().getId())) {
                friendsEntity.setRequester(null);
                i.remove();
            }
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserEntity that = (UserEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static @Nonnull UserResponse toUserResponse(UserEntity userEntity) {
        return toUserResponse(userEntity, FriendshipStatus.UNDEFINED);
    }

    public static @Nonnull UserResponse toUserResponse(UserEntity userEntity, guru.qa.niffler.grpc.FriendshipStatus friendshipStatus) {
        return UserResponse.newBuilder()
                .setId(userEntity.getId().toString())
                .setUsername(userEntity.getUsername())
                .setFirstname(userEntity.getFirstname() != null ? userEntity.getFirstname() : "")
                .setSurname(userEntity.getSurname() != null ? userEntity.getSurname() : "")
                .setFullname(userEntity.getFullname() != null ? userEntity.getFullname() : "")
                .setCurrency(guru.qa.niffler.grpc.CurrencyValues.valueOf(userEntity.getCurrency().name()))
                .setPhoto(userEntity.getPhoto() != null && userEntity.getPhoto().length > 0 ? new String(userEntity.getPhoto(), StandardCharsets.UTF_8) : "")
                .setPhotoSmall(userEntity.getPhotoSmall() != null && userEntity.getPhotoSmall().length > 0 ? new String(userEntity.getPhotoSmall(), StandardCharsets.UTF_8) : "")
                .setFriendshipStatus(friendshipStatus)
                .build();
    }
}