import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserUpdateTest {

    private String accessToken;

    @Before
    public void setUpUser(){
        RegisterApi registerApi = new RegisterApi();
        Register register = new Register("kotovma_35@yandex.ru", "qwe1234", "Maxim");
        Response response = registerApi.postRegisterRequest(register);

        accessToken = response.then().extract().path("accessToken").toString().replace("Bearer ", "");
    }

    @Test
    public void userUpdateNameTest(){
        Response response = sendPatchRequestUserWithToken("kotovma_35@yandex.ru", "Ivan");
        compareStatusCode200(response);
        compareBodySuccessTrue(response);
    }

    @Test
    public void userUpdateEmailTest(){
        Response response = sendPatchRequestUserWithToken("ivanov_53@yandex.ru", "Maxim");
        compareStatusCode200(response);
        compareBodySuccessTrue(response);
    }

    @Test
    public void userUpdateNameWithoutTokenTest(){
        Response response = sendPatchRequestUserWithoutToken("kotovma_35@yandex.ru", "Ivan");
        compareStatusCode401(response);
        compareMessageYouShouldBeAuthorised(response);
    }

    @Test
    public void userUpdateEmailWithoutTokenTest(){
        Response response = sendPatchRequestUserWithoutToken("ivanov_53@yandex.ru", "Maxim");
        compareStatusCode401(response);
        compareMessageYouShouldBeAuthorised(response);
    }

    @After
    public void deleteUser() {
        UserApi userApi = new UserApi();
        userApi.deleteUserRequest(accessToken);
    }

    @Step("Отправка PATH запроса /api/auth/user")
    public Response sendPatchRequestUserWithToken(String email, String name){
        UserApi userApi = new UserApi();
        User user = new User(email, name);
        Response response = userApi.patchUserRequestWithToken(accessToken, user);
        return  response;
    }

    @Step("Сравнение statusCode 200")
    public void compareStatusCode200(Response response){
        response.then().assertThat().statusCode(SC_OK);
    }

    @Step("Сравнение тела ответа на наличие succes: true")
    public void compareBodySuccessTrue(Response response){
        response.then().assertThat().body("success", equalTo(true));
    }

    @Step("Отправка PATH запроса /api/auth/user")
    public Response sendPatchRequestUserWithoutToken(String email, String name){
        UserApi userApi = new UserApi();
        User user = new User(email, name);
        Response response = userApi.patchUserRequestWithoutToken(user);
        return  response;
    }

    @Step("Сравнение statusCode 401")
    public void compareStatusCode401(Response response){
        response.then().assertThat().statusCode(SC_UNAUTHORIZED);
    }

    @Step("Сравнение message: You should be authorised")
    public void compareMessageYouShouldBeAuthorised(Response response){
        response.then().assertThat().body("message", equalTo("You should be authorised"));
    }
}
