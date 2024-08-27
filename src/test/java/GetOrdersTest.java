import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrdersTest {

    private String accessToken;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUpUser(){
        if(testName.getMethodName().contains("WithToken")) {
            RegisterApi registerApi = new RegisterApi();
            Register register = new Register("kotovma_35@yandex.ru", "qwe1234", "Maxim");
            Response response = registerApi.postRegisterRequest(register);

            accessToken = response.then().extract().path("accessToken").toString().replace("Bearer ", "");
        }
    }

    @Test
    public void getOrdersWithoutTokenTest(){
        Response response = sendGetRequestOrdersWithoutToken();
        compareStatusCode401(response);
        compareMessageYouShouldBeAuthorised(response);
    }

    @Test
    public void getOrdersWithTokenTest(){
        Response response = sendGetRequestOrdersWithToken();
        compareStatusCode200(response);
        compareSuccessTrue(response);
    }

    @After
    public void deleteUser() {
        if(testName.getMethodName().contains("WithToken")) {
            UserApi userApi = new UserApi();
            userApi.deleteUserRequest(accessToken);
        }
    }

    @Step("Отправка GET запроса /api/orders без токена")
    public Response sendGetRequestOrdersWithoutToken(){
        OrdersApi ordersApi = new OrdersApi();
        Response response = ordersApi.getOrdersRequestWithoutToken();
        return response;
    }

    @Step("Сравнение statusCode 401")
    public void compareStatusCode401(Response response){
        response.then().assertThat().statusCode(SC_UNAUTHORIZED);
    }

    @Step("Сравнение тела ответа на наличие message: You should be authorised")
    public void compareMessageYouShouldBeAuthorised(Response response){
        response.then().assertThat().body("message",equalTo("You should be authorised"));
    }

    @Step("Отправка GET запроса /api/orders c токеном")
    public Response sendGetRequestOrdersWithToken(){
        OrdersApi ordersApi = new OrdersApi();
        Response response = ordersApi.getOrdersRequestWithToken(accessToken);
        return response;
    }

    @Step("Сравнение statucCode 200")
    public void compareStatusCode200(Response response){
        response.then().assertThat().statusCode(SC_OK);
    }

    @Step("Сравнение тела ответа success: true")
    public void compareSuccessTrue(Response response){
        response.then().assertThat().body("success",equalTo(true));
    }

}
