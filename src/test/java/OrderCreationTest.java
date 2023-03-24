import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Order;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private OrderClient orderClient;
    private Order order;


    public OrderCreationTest(Order order) {
        this.order = order;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", List.of("BLACK", "GRAY"))},
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", List.of("BLACK"))},
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", List.of("GRAY"))},
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", List.of())}
        };
    }


    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }


    @Test
    @DisplayName("Заказ может быть создан с валидными данными")
    public void orderCanBeCreatedWithValidData() {
        orderClient.create(order)
                .assertThat()
                .statusCode(SC_CREATED)
                .body("track", is(notNullValue()));
    }


}
