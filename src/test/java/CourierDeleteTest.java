import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.CourierGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;

public class CourierDeleteTest {

    private CourierClient courierClient;
    private Courier courier;


    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
//                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void clearData() throws NullPointerException {
        try {
            courierClient.delete(courier);
        } catch (NullPointerException ignored) {}
    }

    @Test
    public void courierCanBeDeletedWithValidData() {
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courier.setId(courierClient.login(courier).extract().path("id"));
        courierClient.delete(courier)
                .assertThat()
                .statusCode(SC_OK)
                .body("ok", is(true));
    }

    @Test
    public void courierCanNotBeDeletedWithoutId() {
        courier = CourierGenerator.getRandom();

        courierClient.delete(courier)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для удаления курьера"));
    }

    @Test
    public void courierCanNotBeDeletedWithInvalidId() {
        courier = CourierGenerator.getRandom();
        courier.setId(0);

        courierClient.delete(courier)
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Курьера с таким id нет."));
    }
}
