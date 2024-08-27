import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class RequestSpec {

    private RequestSpecification baseRequestSpec(){
        return new RequestSpecBuilder()
                .setBaseUri(URL.HOST)
                .addHeader("Content-Type", "application/json")
                .setRelaxedHTTPSValidation()
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new ErrorLoggingFilter())
                .build();
    }

    protected Response doGetRequest(String path){
        return given()
                .spec(baseRequestSpec())
                .get(path)
                .thenReturn();
    }

    protected Response doGetRequestWithToken(String path, String accessToken){
        return given()
                .spec(baseRequestSpec())
                .auth().oauth2(accessToken)
                .get(path)
                .thenReturn();
    }

    protected Response doPostRequest(String path, Object body){
        return given()
                .spec(baseRequestSpec())
                .body(body)
                .post(path)
                .thenReturn();
    }

    protected Response doPostRequestWithToken(String path, Object body, String accessToken){
        return given()
                .spec(baseRequestSpec())
                .auth().oauth2(accessToken)
                .body(body)
                .post(path)
                .thenReturn();
    }

    protected Response doDeleteRequest(String path, String accessToken){
        return given()
                .spec(baseRequestSpec())
                .auth().oauth2(accessToken)
                .delete(path)
                .thenReturn();
    }

    protected  Response doPatchRequestWithToken(String accessToken, Object body, String path){
        return given()
                .spec(baseRequestSpec())
                .auth().oauth2(accessToken)
                .body(body)
                .patch(path)
                .thenReturn();
    }

    protected  Response doPatchRequestWithoutToken(Object body, String path){
        return given()
                .spec(baseRequestSpec())
                .body(body)
                .patch(path)
                .thenReturn();
    }
}
