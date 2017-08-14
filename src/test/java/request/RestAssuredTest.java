package request;

import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import stub.StubService;
import vo.CustomerVO;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RestAssuredTest {

    private static List<CustomerVO> customers;

    @BeforeClass
    public static void setUp() {
        //GIVEN: there are testdatas and api stubs.
        testdata();
        stub();
    }

    @Test
    public void create_customer_test() {
        given().
                log().all().
                baseUri(Server.test.getHost()).
                port(Server.test.getPort()).
                basePath(Server.test.getBasePath()).
                contentType(API.create_customer.getContentType()).
                body(customers.get(0)).
        when().
                post(API.create_customer.getPath()).
        then().
                log().all().
                statusCode(HttpStatus.SC_CREATED).
                body("id", equalTo(customers.get(0).getId())).
                body("name", equalTo(customers.get(0).getName())).
                body("phone", equalTo(customers.get(0).getPhone())).
                body("email", equalTo(customers.get(0).getEmail()));
    }

    @Test
    public void get_customer_test() {
        given().
                log().all().
                baseUri(Server.test.getHost()).
                port(Server.test.getPort()).
                basePath(Server.test.getBasePath()).
                contentType(API.get_customer.getContentType()).
                pathParam("customerId", "1").
        when().
                get(API.get_customer.getPath()).
        then().
                log().all().
                statusCode(HttpStatus.SC_OK).
                body("id", equalTo(customers.get(0).getId())).
                body("name", equalTo(customers.get(0).getName())).
                body("phone", equalTo(customers.get(0).getPhone())).
                body("email", equalTo(customers.get(0).getEmail()));
    }

    @Test
    public void list_customer_test() {
        given().
                log().all().
                baseUri(Server.test.getHost()).
                port(Server.test.getPort()).
                basePath(Server.test.getBasePath()).
                contentType(API.list_customers.getContentType()).
        when().
                get(API.list_customers.getPath()).
        then().
                log().all().
                statusCode(HttpStatus.SC_OK).
                body("", hasSize(customers.size())).
                body("id[0]", equalTo(customers.get(0).getId())).
                body("name[1]", equalTo(customers.get(1).getName())).
                body("phone[2]", equalTo(customers.get(2).getPhone()));
    }

    @Test
    public void delete_customer_test() {
        given().
                log().all().
                baseUri(Server.test.getHost()).
                port(Server.test.getPort()).
                basePath(Server.test.getBasePath()).
                contentType(API.delete_customer.getContentType()).
                queryParam("customerId", "1").
        when().
                delete(API.delete_customer.getPath()).
        then().
                log().all().
                statusCode(HttpStatus.SC_OK);
    }

    private static void testdata() {
        customers = new ArrayList<CustomerVO>();
        customers.add(new CustomerVO(1, "Customer One", "010-1111-1111", "one@customer.com"));
        customers.add(new CustomerVO(2, "Customer Two", "010-2222-2222", "two@customer.com"));
        customers.add(new CustomerVO(3, "Customer Three", "010-3333-3333", "three@customer.com"));
    }

    private static void stub() {
        StubService.to(Server.test).delete();

        StubService.to(Server.test)
                .when(API.create_customer)
                .withBody(customers.get(0))
                .thenStatus(201)
                .thenBody(customers.get(0))
                .create();

        StubService.to(Server.test)
                .when(API.get_customer)
                .withPathParam("customerId", "1")
                .thenStatus(200)
                .thenBody(customers.get(0))
                .create();

        StubService.to(Server.test)
                .when(API.list_customers)
                .thenStatus(200)
                .thenBody(customers)
                .create();

        StubService.to(Server.test)
                .when(API.delete_customer)
                .withQueryParam("customerId", "1")
                .thenStatus(200)
                .create();
    }

}
