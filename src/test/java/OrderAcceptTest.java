import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.CourierGenerator;
import ru.yandex.praktikum.model.Order;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;

public class OrderAcceptTest {

    private OrderClient orderClient;
    private CourierClient courierClient;
    private Courier courier;
    private Order order;


    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        courierClient = new CourierClient();
    }

    @After
    public void clearData() throws NullPointerException {
        try {
            courierClient.delete(courier);
        } catch (NullPointerException ignored) {}
    }

    @Test
    @DisplayName("Заказ может быть принят с валидными данными")
    public void orderCanBeAcceptedWithValidData() {
        order = new Order();
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courier.setId(courierClient.login(courier).extract().path("id"));
        order.setId(orderClient.getList().extract().path("orders[0].id"));

        orderClient.accept(courier, order)
                .assertThat()
                .statusCode(SC_OK)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Заказ не может быть принят без Id заказа")
    public void orderCanNotBeAcceptedWithoutOrderId() {
        order = new Order();
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courier.setId(courierClient.login(courier).extract().path("id"));

        orderClient.accept(courier, order)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Заказ не может быть принят без Id курьера")
    public void orderCanNotBeAcceptedWithoutCourierId() {
        order = new Order();
        courier = CourierGenerator.getRandom();
        order.setId(orderClient.getList().extract().path("orders[0].id"));

        orderClient.accept(courier, order)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Заказ не может быть принят с невалидным Id заказа")
    public void orderCanNotBeAcceptedWithInvalidOrderId() {
        order = new Order();
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courier.setId(courierClient.login(courier).extract().path("id"));
        order.setId(0);

        orderClient.accept(courier, order)
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Заказа с таким id не существует"));
    }

    @Test
    @DisplayName("Заказ не может быть принят с невалидным Id курьера")
    public void orderCanNotBeAcceptedWithInvalidCourierId() {
        order = new Order();
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courier.setId(0);
        order.setId(orderClient.getList().extract().path("orders[0].id"));

        orderClient.accept(courier, order)
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Курьера с таким id не существует"));
    }
}
