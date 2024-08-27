import io.restassured.response.Response;

public class UserApi extends RegisterApi {

    public Response deleteUserRequest(String accessToken){
        return doDeleteRequest(URL.USER_API, accessToken);
    }

    public Response patchUserRequestWithToken(String accessToken, User user){
        return doPatchRequestWithToken(accessToken, user, URL.USER_API);
    }

    public Response patchUserRequestWithoutToken(User user){
        return doPatchRequestWithoutToken(user, URL.USER_API);
    }
}
