package guru.qa.niffler.api;

import jaxb.userdata.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserdataSoapApi {


    @Headers(value= {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> currentUser(@Body CurrentUserRequest currentUserRequest);


    @Headers(value= {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> allUsersPage(@Body AllUsersPageRequest allUsersPageRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> friendsPage(@Body FriendsPageRequest friendsPageRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> friends(@Body FriendsRequest friendsRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<Void> removeFriend(@Body RemoveFriendRequest removeFriendRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> acceptFriend(@Body AcceptInvitationRequest acceptInvitationRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> declineFriend(@Body DeclineInvitationRequest declineInvitationRequest);


    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> addFriend(@Body SendInvitationRequest sendInvitationRequest);

}