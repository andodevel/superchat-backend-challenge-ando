package de.superchat.openapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.Test;

@QuarkusTest
public class OpenApiTest {

    @Test
    public void testOpenApi() {
        given()
            .when().get("/q/openapi")
            .then()
            .statusCode(200)
            .body(containsString("openapi"));
    }
}
