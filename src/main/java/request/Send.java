package request;

import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Send {

    private static RequestSpecification req;

    private Send(Server server) {
        RestAssured.baseURI = server.getHost();
        RestAssured.port = server.getPort();
        RestAssured.basePath = server.getBasePath();

        req = given().spec(new RequestSpecBuilder().build());
        req = req.log().all();
    }

    public static Send to(Server server) {
        return new Send(server);
    }

    public Send withQueryParam(String name, String value) {
        req = req.queryParam(name, value);
        return this;
    }

    public Send withPathParam(String name, String value) {
        req = req.pathParam(name, value);
        return this;
    }

    public Send withBody(Object body) {
        req = req.body(body);
        return this;
    }

    public Response toward(API api) {
        req.contentType(api.getContentType());

        switch (api.getMethod().toUpperCase()) {
            case "GET":
                return req.get(api.getPath());
            case "POST":
                return req.post(api.getPath());
            case "PUT":
                return req.put(api.getPath());
            case "DELETE":
                return req.delete(api.getPath());
            case "HEAD":
                return req.head(api.getPath());
            case "PATCH":
                return req.patch(api.getPath());
            case "OPTIONS":
                return req.options(api.getPath());
            default:
                throw new SendException("No supported method type: "+api.getMethod());
        }
    }
}
