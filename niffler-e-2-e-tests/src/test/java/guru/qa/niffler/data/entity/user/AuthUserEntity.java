package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity implements Serializable {

    private UUID id;

    private String username;

    private String password;

    private Boolean enabled;

    private Boolean accountNonExpired;

    private Boolean accountNonLocked;

    private Boolean credentialsNonExpired;

    private List<AuthorityEntity> permissions = new ArrayList<>();

    public void addPermissions(AuthorityEntity... permissions) {
        for (AuthorityEntity permission : permissions) {
            this.permissions.add(permission);
            permission.setUser(this);
        }
    }

    public static AuthUserEntity fromJson(UserJson userJson) {
        AuthUserEntity authUserEntity = new AuthUserEntity();

        authUserEntity.setId(null);
        authUserEntity.setUsername(userJson.username());
        authUserEntity.setPassword(userJson.authUserJson().password());
        authUserEntity.setEnabled(userJson.authUserJson().enabled());
        authUserEntity.setAccountNonExpired(userJson.authUserJson().accountNonExpired());
        authUserEntity.setAccountNonLocked(userJson.authUserJson().accountNonLocked());
        authUserEntity.setCredentialsNonExpired(userJson.authUserJson().credentialsNonExpired());

        AuthorityEntity readPermissionEntity = new AuthorityEntity();
        readPermissionEntity.setAuthority(Permissions.read);
        AuthorityEntity writePermissionEntity = new AuthorityEntity();
        writePermissionEntity.setAuthority(Permissions.write);
        AuthorityEntity deletePermissionEntity = new AuthorityEntity();
        deletePermissionEntity.setAuthority(Permissions.delete);
        authUserEntity.addPermissions(readPermissionEntity, writePermissionEntity, deletePermissionEntity);

        return authUserEntity;
    }

}