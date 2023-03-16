import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Order;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderGetTest {

    private OrderClient orderClient;
    private Order order;


    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
//                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    public void orderCanBeGottenWithValidData() {
        order = new Order();
        order.setTrackId(orderClient.create(order).extract().path("track"));

        orderClient.get(order)
                .assertThat()
                .statusCode(SC_OK)
                .body("order", is(notNullValue()));
    }

    @Test
    public void orderCanNotBeGottenWithoutTrackId() {
        order = new Order();

        orderClient.get(order)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для поиска"));
    }

    @Test
    public void orderCanNotBeGottenWithInvalidTrackId() {
        order = new Order();
        order.setTrackId(0);

        orderClient.get(order)
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Заказ не найден"));
    }
}
