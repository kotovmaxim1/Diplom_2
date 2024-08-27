import io.restassured.response.Response;

public class RegisterApi extends RequestSpec {

    public Response postRegisterRequest(Register register){
        return doPostRequest(URL.REGISTER_API, register);
    }

}
