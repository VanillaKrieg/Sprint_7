import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class OrdersListTest {

    private OrderClient orderClient;

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
    public void ordersListCanBeGotten() {
        orderClient.getList()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", hasSize(greaterThan(0)));
    }
}
