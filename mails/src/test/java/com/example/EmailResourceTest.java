package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class EmailResourceTest {

   @Test
   public void shouldFailOnOddRequestsInFiftyFiftyMode() {
      given()
            .when().body("FIFTY_FIFTY").post("/application-state")
            .then()
            .statusCode(204);

      assertFailure();
      assertSuccess();
      assertFailure();
   }

   @Test
   public void shouldFailOnEveryRequestsInFailingMode() {
      given()
            .when().body("FAILING").post("/application-state")
            .then()
            .statusCode(204);

      assertFailure();
      assertFailure();
      assertFailure();
   }

   @Test
   public void shouldSucceedOnEveryRequestsInWorkingMode() {
      given()
            .when().body("WORKING").post("/application-state")
            .then()
            .statusCode(204);

      assertSuccess();
      assertSuccess();
      assertSuccess();
   }

   private void assertSuccess() {
      getPostEmailResponse()
            .statusCode(201);
   }

   private void assertFailure() {
      getPostEmailResponse()
            .statusCode(500);
   }

   private ValidatableResponse getPostEmailResponse() {

      return given()
            .when()
            .body("{\n" +
                  "  \"address\": \"michal@example.com\",\n" +
                  "  \"subject\": \"Test Email\",\n" +
                  "  \"content\": \"This is a test email to test if emails can be posted\"\n" +
                  "}")
            .contentType("application/json")
            .post("/emails")
            .then();
   }

}