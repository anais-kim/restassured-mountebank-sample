package stub;

import io.restassured.path.json.JsonPath;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mbtest.javabank.http.imposters.Imposter;
import request.API;
import request.Server;

import static io.restassured.path.json.JsonPath.*;
import static org.hamcrest.Matchers.*;

public class StubServiceTest {

    @Before
    public void setUp() {
        //GIVEN: Mountebank is running and there's no imposter.
        StubService.to(Server.test).delete();
    }

    @Test
    public void create_empty_imposter_test() {
        //WHEN
        StubService.to(Server.test).create();

        //THEN
        Imposter imposter = StubService.get(Server.test);
        assertThat(imposter.getPort(), is(Server.test.getPort()));
        print(imposter);
    }

    @Test
    public void create_basic_imposter_test() {
        //WHEN
        StubService.to(Server.test)
                .when(API.list_customers)
                .thenStatus(200)
                .thenBodyString("{ \"message\": \"success\" }")
                .create();

        //THEN
        Imposter imposter = StubService.get(Server.test);
        JsonPath jp = from(imposter.toString());

        assertThat(jp.getInt("port"), is(Server.test.getPort()));
        assertThat(jp.getString("stubs.predicates.equals.path[0][0]"), is(API.list_customers.getPath()));
        assertThat(jp.getString("stubs.predicates.equals.method[0][0]"), is(API.list_customers.getMethod()));
        assertThat(jp.getInt("stubs.responses.is.statusCode[0][0]"), is(200));
        assertThat(jp.getString("stubs.responses.is.body[0][0]"), containsString("success"));
        print(imposter);
    }

    @Test
    public void create_imposter_with_path_param_test() {
        //WHEN
        StubService.to(Server.test)
                .when(API.get_customer)
                .withPathParam("customerId", "1")
                .thenStatus(200)
                .thenBodyString("{ \"message\": \"success\" }")
                .create();

        //THEN
        Imposter imposter = StubService.get(Server.test);
        JsonPath jp = from(imposter.toString());

        assertThat(jp.getInt("port"), is(Server.test.getPort()));
        assertThat(jp.getString("stubs.predicates.equals.path[0][0]"), is("/customers/1"));
        assertThat(jp.getString("stubs.predicates.equals.method[0][0]"), is(API.get_customer.getMethod()));
        assertThat(jp.getInt("stubs.responses.is.statusCode[0][0]"), is(200));
        assertThat(jp.getString("stubs.responses.is.body[0][0]"), containsString("success"));
        print(imposter);
    }

    @Test
    public void create_imposter_with_query_param_test() {
        //WHEN
        StubService.to(Server.test)
                .when(API.delete_customer)
                .withQueryParam("customerId", "1")
                .thenStatus(200)
                .thenBodyString("{ \"message\": \"success\" }")
                .create();

        //THEN
        Imposter imposter = StubService.get(Server.test);
        JsonPath jp = from(imposter.toString());

        assertThat(jp.getInt("port"), is(Server.test.getPort()));
        assertThat(jp.getString("stubs.predicates.equals.path[0][0]"), is(API.delete_customer.getPath()));
        assertThat(jp.getString("stubs.predicates.equals.method[0][0]"), is(API.delete_customer.getMethod()));
        assertThat(jp.getString("stubs.predicates.equals.query[0][0]"), containsString("customerId:1"));
        assertThat(jp.getInt("stubs.responses.is.statusCode[0][0]"), is(200));
        assertThat(jp.getString("stubs.responses.is.body[0][0]"), containsString("success"));
        print(imposter);
    }

    private void print(Imposter imposter) {
        new JsonPath(imposter.toString()).prettyPrint();
    }
}
