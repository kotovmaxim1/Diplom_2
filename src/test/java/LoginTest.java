import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginTest {

    String accessToken;

    @Before
    public void setUpUser(){
        RegisterApi registerApi = new RegisterApi();
        Register register = new Register("kotovma_35@yandex.ru", "qwe1234", "Maxim");
        Response response = registerApi.postRegisterRequest(register);

        accessToken = response.then().extract().path("accessToken").toString().replace("Bearer ", "");
    }

    @Test
    public void loginUserTest(){
        Response response = senPostRequestLogin("kotovma_35@yandex.ru", "qwe1234");
        compareStatusCode200(response);
        compareBodySuccessTrue(response);
    }

    @Test
    public void loginUserWithWrongPassword(){
        Response response = senPostRequestLogin("kotovma_35@yandex.ru", "k746cx");
        compareStatusCode401(response);
        compareMessageEmailorPasswordAreIncorrect(response);
    }

    @Test
    public void loginUserWithWrongEmail(){
        Response response = senPostRequestLogin("kotov999@yandex.ru", "qwe1234");
        compareStatusCode401(response);
        compareMessageEmailorPasswordAreIncorrect(response);
    }


    @After
    public void deleteUser() {
        UserApi userApi = new UserApi();
        userApi.deleteUserRequest(accessToken);
    }

    @Step("Отправка POST запроса /api/auth/login")
    public Response senPostRequestLogin(String email, String password){
        LoginApi loginApi = new LoginApi();
        Login login = new Login(email, password);
        Response response = loginApi.postLoginRequest(login);
        return response;
    }

    @Step("Сравнение statusCode 200")
    public void compareStatusCode200(Response response){
        response.then().assertThat().statusCode(SC_OK);
    }

    @Step("Сравнение тела ответа на наличие succes: true")
    public void compareBodySuccessTrue(Response response){
        response.then().assertThat().body("success", equalTo(true));
    }

    @Step("Сравнение statusCode 401")
    public void compareStatusCode401(Response response){
        response.then().assertThat().statusCode(SC_UNAUTHORIZED);
    }

    @Step("Сравнение тела ответа на наличие message: email or password are incorrect")
    public void compareMessageEmailorPasswordAreIncorrect(Response response){
        response.then().assertThat().body("message", equalTo("email or password are incorrect"));
    }
}
