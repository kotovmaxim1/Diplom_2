import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class OrdersTest {

    private String accessToken;
    private List <String> ingredients;
    private static final String INGREDIENT_1 = "61c0c5a71d1f82001bdaaa6d";
    private static final String INGREDIENT_2 = "61c0c5a71d1f82001bdaaa71";
    private static final String INGREDIENT_BAD_1 = "99c0c5a71d1f82001bdaaa6d";
    private static final String INGREDIENT_BAD_2 = "99c0c5a71d1f82001bdaaa71";

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

        ingredients = new ArrayList<>();
    }

    @Test
    public void makeOrdersWithOutTokenWithIngredientsTest() {
        ingredients.add(INGREDIENT_1);
        ingredients.add(INGREDIENT_2);
        Response response = sendPostRequestOrdersWithoutToken(ingredients);
        compareStatusCode200(response);
        compareBodySuccessTrue(response);
    }

    @Test
    public void makeOrdersWithOutTokenWithoutIngredientsTest(){
        Response response = sendPostRequestOrdersWithoutToken(ingredients);
        compareStatusCode400(response);
        compareMessageIngredientIdsMustBeProvided(response);
    }

    @Test
    public void makeOrdersWithOutTokenWithBadIngredientsTest(){
        ingredients.add(INGREDIENT_BAD_1);
        ingredients.add(INGREDIENT_BAD_2);
        Response response = sendPostRequestOrdersWithoutToken(ingredients);
        compareStatusCode400(response);
        compareMessageOneOrMoreIdsProvidedAreIncorrect(response);
    }

    @Test
    public void makerOrdersWithTokenWithIngredientsTest(){
        ingredients.add(INGREDIENT_1);
        ingredients.add(INGREDIENT_2);
        Response response = sendPostRequestOrdersWithToken(ingredients);
        compareStatusCode200(response);
        compareStatusDone(response);
    }

    @Test
    public void makerOrdersWithTokenWithBadIngredients(){
        ingredients.add(INGREDIENT_BAD_1);
        ingredients.add(INGREDIENT_BAD_2);
        Response response = sendPostRequestOrdersWithToken(ingredients);
        compareStatusCode400(response);
        compareMessageOneOrMoreIdsProvidedAreIncorrect(response);
    }

    @Test
    public void makeOrdersWithTokenWithoutIngredients(){
        Response response = sendPostRequestOrdersWithToken(ingredients);
        compareStatusCode400(response);
        compareMessageIngredientIdsMustBeProvided(response);
    }

    @After
    public void deleteUser() {
        if(testName.getMethodName().contains("WithToken")) {
            UserApi userApi = new UserApi();
            userApi.deleteUserRequest(accessToken);
        }
    }

    @Step("Отправка POST запроса /api/orders без токена")
    public Response sendPostRequestOrdersWithoutToken(List<String> ingredients){
        OrdersApi ordersApi = new OrdersApi();
        Orders orders = new Orders(this.ingredients);
        Response response = ordersApi.postOrdersRequestWithoutToken(orders);
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

    @Step("Сравнение statucCode 400")
    public void compareStatusCode400(Response response){
        response.then().assertThat().statusCode(SC_BAD_REQUEST);
    }

    @Step("Сравнение тела ответа на наличие message: Ingredient ids must be provided")
    public void compareMessageIngredientIdsMustBeProvided(Response response){
        response.then().assertThat().body("message",equalTo("Ingredient ids must be provided"));
    }

    @Step("Сравнение тела ответа на наличие message: One or more ids provided are incorrect")
    public void compareMessageOneOrMoreIdsProvidedAreIncorrect(Response response){
        response.then().assertThat().body("message",equalTo("One or more ids provided are incorrect"));
    }

    @Step("Отправка POST запроса /api/orders с токеном")
    public Response sendPostRequestOrdersWithToken(List<String> ingredients){
        OrdersApi ordersApi = new OrdersApi();
        Orders orders = new Orders(this.ingredients);
        Response response = ordersApi.postOrdersRequestWithToken(accessToken, orders);
        return response;
    }

    @Step("Сравнение тела ответа на наличие status: done")
    public void compareStatusDone(Response response){
        response.then().assertThat().body("order.status", equalTo("done"));
    }
}
