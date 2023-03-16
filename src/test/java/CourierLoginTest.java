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
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierLoginTest {

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
            courier.setId(courierClient.login(courier).extract().path("id"));
            courierClient.delete(courier);
        } catch (NullPointerException ignored) {}
    }

    @Test
    public void courierCanLoginWithValidData() {
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);

        courierClient.login(courier)
                .assertThat()
                .statusCode(SC_OK)
                .body("id", is(notNullValue()));
    }

    @Test
    public void courierCanNotLoginWithInvalidLogin() {
        courier = CourierGenerator.getRandom();

        courierClient.login(courier)
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Учетная запись не найдена"));
    }

    @Test
    public void courierCanNotLoginWithInvalidPassword() {
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        String password = courier.getPassword();
        courier.setPassword("invalid_password");

        courierClient.login(courier)
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Учетная запись не найдена"));

        courier.setPassword(password);
    }

    @Test
    public void courierCanNotLoginWithoutLogin() {
        courier = new Courier("", "password");

        courierClient.login(courier)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    public void courierCanNotLoginWithoutPassword() {
        courier = new Courier("login", "");

        courierClient.login(courier)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }
}
