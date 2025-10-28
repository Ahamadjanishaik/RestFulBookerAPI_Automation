package bookings;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.*;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import static io.restassured.RestAssured.given;

public class Bookings {

    int bookingID;
    String token;
    private static ExtentReports extent;
    private ExtentTest test;

    @BeforeClass
    public void setupReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("target/BookingReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    @Test(priority = 0)
    public void createBooking() {
        test = extent.createTest("Create Booking");

        try {
            JSONObject bookingDates = new JSONObject();
            bookingDates.put("checkin", "2018-01-01");
            bookingDates.put("checkout", "2019-01-01");

            JSONObject requestBody = new JSONObject();
            requestBody.put("firstname", "Jani");
            requestBody.put("lastname", "Shaik");
            requestBody.put("totalprice", 117);
            requestBody.put("depositpaid", true);
            requestBody.put("bookingdates", bookingDates);
            requestBody.put("additionalneeds", "Breakfast");

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody.toString())
                    .when()
                    .post("/booking")
                    .then()
                    .statusCode(200).log().all()
                    .extract().response();

            bookingID = response.jsonPath().getInt("bookingid");
            test.pass("Booking created successfully. BookingID: " + bookingID);

        } catch (Exception e) {
            test.fail("Create booking failed: " + e.getMessage());
        }
    }

    @Test(priority = 1)
    public void createToken() {
        test = extent.createTest("Create Token");

        try {
            JSONObject js = new JSONObject();
            js.put("username", "admin");
            js.put("password", "password123");

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(js.toString())
                    .when()
                    .post("/auth")
                    .then()
                    .statusCode(200).log().all()
                    .extract().response();

            token = response.jsonPath().getString("token");
            test.pass("Token generated successfully: " + token);

        } catch (Exception e) {
            test.fail("Token generation failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void updateBooking() {
        test = extent.createTest("Update Booking");

        try {
            JSONObject bookingDates = new JSONObject();
            bookingDates.put("checkin", "2018-01-01");
            bookingDates.put("checkout", "2019-01-01");

            JSONObject requestBody = new JSONObject();
            requestBody.put("firstname", "Janu");
            requestBody.put("lastname", "Shaik");
            requestBody.put("totalprice", 117);
            requestBody.put("depositpaid", true);
            requestBody.put("bookingdates", bookingDates);
            requestBody.put("additionalneeds", "Lunch");

            given()
                    .contentType(ContentType.JSON)
                    .header("Accept", "application/json")
                    .header("Cookie", "token=" + token)
                    .body(requestBody.toString())
                    .when()
                    .put("/booking/" + bookingID)
                    .then()
                    .statusCode(200).log().all();

            test.pass("Booking updated successfully for BookingID: " + bookingID);

        } catch (Exception e) {
            test.fail("Update booking failed: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void partialUpdateBooking() {
        test = extent.createTest("Partial Update Booking");

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("firstname", "Ahamad");
            requestBody.put("lastname", "Shaik");

            given()
                    .contentType(ContentType.JSON)
                    .header("Accept", "application/json")
                    .header("Cookie", "token=" + token)
                    .body(requestBody.toString())
                    .when()
                    .patch("/booking/" + bookingID)
                    .then()
                    .statusCode(200).log().all();

            test.pass("Booking partially updated successfully for BookingID: " + bookingID);

        } catch (Exception e) {
            test.fail("Partial update failed: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void deleteBooking() {
        test = extent.createTest("Delete Booking");

        try {
            given()
                    .contentType(ContentType.JSON)
                    .header("Accept", "application/json")
                    .header("Cookie", "token=" + token)
                    .when()
                    .delete("/booking/" + bookingID)
                    .then()
                    .statusCode(201).log().all();

            test.pass("Booking deleted successfully for BookingID: " + bookingID);

        } catch (Exception e) {
            test.fail("Delete booking failed: " + e.getMessage());
        }
    }

    @AfterMethod
    public void teardown() {
        RestAssured.reset();
    }

    @AfterClass
    public void flushReport() {
        extent.flush();
    }
}


