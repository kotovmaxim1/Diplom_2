import io.restassured.response.Response;

public class OrdersApi extends RequestSpec {

    public Response postOrdersRequestWithoutToken(Orders orders){
        return doPostRequest(URL.ORDERS_API, orders);
    }

    public Response postOrdersRequestWithToken(String acceessToken, Orders orders){
        return doPostRequestWithToken(URL.ORDERS_API, orders, acceessToken);
    }

    public Response getOrdersRequestWithoutToken(){
        return doGetRequest(URL.ORDERS_API);
    }

    public Response getOrdersRequestWithToken(String accessToken){
        return doGetRequestWithToken(URL.ORDERS_API, accessToken);
    }
}
