import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class RegisterTest {

    @Rule
    public TestName testName = new TestName();

    @Test
    public void registerUserTest() {
        Response response = sendPostRequestRegister("kotovma_35@yandex.ru", "qwe1234", "Maxim");
        compareStatusCode200(response);
        compareBodySuccessTrue(response);
    }

    @Test
    public void registerIsAlreadyRegisteredUserTest(){
        sendPostRequestRegister("kotovma_35@yandex.ru", "qwe1234", "Maxim");
        Response response = sendPostRequestRegister("kotovma_35@yandex.ru", "qwe1234", "Maxim");
        compareStatusCode403(response);
        compareBodyMessageUserAlreadyExists(response);
    }

    @Test
    public void registerUserWithoutEmailTest(){
        Response response = sendPostRequestRegister("", "qwe1234", "Maxim");
        compareStatusCode403(response);
        compareBodyMessageEmailPasswordAndNameAreRequiredFields(response);
    }

    @Test
    public void registerUserWithoutPasswordTest(){
        Response response = sendPostRequestRegister("kotovma_35@yandex.ru", "", "Maxim");
        compareStatusCode403(response);
        compareBodyMessageEmailPasswordAndNameAreRequiredFields(response);
    }

    @Test
    public void registerUserWithoutNameTest(){
        Response response = sendPostRequestRegister("kotovma_35@yandex.ru", "qwe1234", "");
        compareStatusCode403(response);
        compareBodyMessageEmailPasswordAndNameAreRequiredFields(response);
    }

    @After
    public void deleteUser(){
        if(testName.getMethodName().equals("registerUserTest") || testName.getMethodName().equals("registerIsAlreadyRegisteredUserTest")) {
            LoginApi loginApi = new LoginApi();
            Login login = new Login("kotovma_35@yandex.ru", "qwe1234");
            Response response = loginApi.postLoginRequest(login);

            String accessToken = response.then().extract().path("accessToken").toString().replace("Bearer ", "");
            UserApi userApi = new UserApi();
            userApi.deleteUserRequest(accessToken);
        }
    }


    @Step("Отправка POST запроса /api/auth/register")
    public Response sendPostRequestRegister(String email, String password, String name){
        RegisterApi registerApi = new RegisterApi();
        Register register = new Register(email, password, name);
        Response response = registerApi.postRegisterRequest(register);
        return response;
    }

    @Step("Сравнение statucCode 200")
    public void compareStatusCode200(Response response){
        response.then().assertThat().statusCode(SC_OK);
    }

    @Step("Сравнение тела ответа на наличие success: true")
    public void compareBodySuccessTrue(Response response){
        response.then().assertThat().body("success", equalTo(true));
    }

    @Step("Compare statusCode 403")
    public void compareStatusCode403(Response response){
        response.then().assertThat().statusCode(SC_FORBIDDEN);
    }

    @Step("Сравнение тела ответа на наличие message: User already exists")
    public void compareBodyMessageUserAlreadyExists(Response response){
        response.then().assertThat().body("message", equalTo("User already exists"));
    }

    @Step("Сравнение тела ответа на наличие message: Email, password and name are required fields")
    public void compareBodyMessageEmailPasswordAndNameAreRequiredFields(Response response){
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }
}
