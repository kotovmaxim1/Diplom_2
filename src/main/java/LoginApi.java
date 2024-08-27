import io.restassured.response.Response;

public class LoginApi extends RequestSpec {

    public Response postLoginRequest(Login login){
        return doPostRequest(URL.LOGIN_API, login);
    }
}
