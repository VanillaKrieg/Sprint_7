package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.client.base.ScooterRestClient;
import ru.yandex.praktikum.model.Courier;

import static io.restassured.RestAssured.given;

public class CourierClient extends ScooterRestClient {

    private static final String COURIER_URI = "courier/";

    @Step("Create {courier}")
    public ValidatableResponse create(Courier courier) {
        return given()
                .spec(getBaseReqSpec())
                .body(courier)
                .when()
                .post(COURIER_URI)
                .then();
    }

    @Step("Login as {courier}")
    public ValidatableResponse login(Courier courier) {
        return given()
                .spec(getBaseReqSpec())
                .body(courier)
                .when()
                .post(COURIER_URI + "login/")
                .then();
    }

    @Step("Delete {courier}")
    public ValidatableResponse delete(Courier courier) {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .delete(COURIER_URI + (courier.getId() == null ? "" : courier.getId()))
                .then();
    }
}
