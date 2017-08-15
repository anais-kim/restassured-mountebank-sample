package request;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import stub.StubService;
import vo.CustomerVO;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class SendTest {

    private static List<CustomerVO> customers;

    @BeforeClass
    public static void setUp() {
        //GIVEN: there are testdatas and api stubs.
        testdata();
        stub();
    }

    @Test
    public void create_customer_test() {
        //WHEN
        Response res = Send.to(Server.test).
                withBody(customers.get(0)).
                toward(API.create_customer);
        //THEN
        res.then().
                log().all().
                statusCode(HttpStatus.SC_CREATED).
                body("id", equalTo(customers.get(0).getId())).
                body("name", equalTo(customers.get(0).getName())).
                body("phone", equalTo(customers.get(0).getPhone())).
                body("email", equalTo(customers.get(0).getEmail()));
    }

    @Test
    public void get_customer_test() {
        //WHEN
        Response res = Send.to(Server.test).
                withPathParam("customerId", "1").
                toward(API.get_customer);
        //THEN
        res.then().
                log().all().
                statusCode(HttpStatus.SC_OK).
                body("id", equalTo(customers.get(0).getId())).
                body("name", equalTo(customers.get(0).getName())).
                body("phone", equalTo(customers.get(0).getPhone())).
                body("email", equalTo(customers.get(0).getEmail()));
    }

    @Test
    public void list_customer_test() {
        //WHEN
        Response res = Send.to(Server.test).
                toward(API.list_customers);
        //THEN
        res.then().
                log().all().
                statusCode(HttpStatus.SC_OK).
                body("", hasSize(customers.size())).
                body("id[0]", equalTo(customers.get(0).getId())).
                body("name[1]", equalTo(customers.get(1).getName())).
                body("phone[2]", equalTo(customers.get(2).getPhone()));
    }

    @Test
    public void delete_customer_test() {
        //WHEN
        Response res = Send.to(Server.test).
                withQueryParam("customerId", "1").
                toward(API.delete_customer);
        //THEN
        res.then().
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
