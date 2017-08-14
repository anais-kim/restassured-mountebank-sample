package stub;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.json.simple.parser.ParseException;
import org.mbtest.javabank.Client;
import org.mbtest.javabank.fluent.ImposterBuilder;
import org.mbtest.javabank.http.core.Stub;
import org.mbtest.javabank.http.imposters.Imposter;
import org.mbtest.javabank.http.predicates.Predicate;
import org.mbtest.javabank.http.predicates.PredicateType;
import org.mbtest.javabank.http.responses.Is;
import request.API;
import request.Server;
import util.MapperUtil;

import java.util.Arrays;

public class StubService {

    private static Server server;
    private static Client client;
    private static Imposter imposter;
    private static Stub stub;
    private static Predicate predicate;
    private static Is response;

    private StubService(Server server) {
        this.server = server;
        this.client = new Client();
        if (!client.isMountebankRunning()) {
            throw new StubServiceException("Mountebank is not running.");
        }
        imposter = getImposter();
        stub = new Stub();
        predicate = new Predicate(PredicateType.EQUALS);
        response = new Is();

        // set default content-type to application/json.
        response.addHeader("Content-Type", "application/json");
    }

    public static StubService to(Server server) {
        return new StubService(server);
    }

    public static Imposter get(Server server) {
        return new StubService(server).getImposter();
    }

    public StubService when(API api) {
        predicate.withPath(server.getBasePath() + api.getPath());
        predicate.withMethod(api.getMethod());
//        predicate.addHeader("Content-Type", api.getContentType()); //TODO
        return this;
    }

    public StubService withBody(Object body) {
        String bodyString = MapperUtil.writeValueAsString(body);
        return withBodyString(bodyString);
    }

    public StubService withBodyString(String bodyString) {
        predicate.withBody(bodyString);
        return this;
    }

    public StubService withHeader(String name, String value) {
        predicate.addHeader(name, value);
        return this;
    }

    public StubService withQueryParam(String name, String value) {
        predicate.addQueryParameter(name, value);
        return this;
    }

    public StubService withPathParam(String name, String value) {
        String path = predicate.getPath();
        if (path.contains("{"+name+"}")) {
            path = path.replace("{"+name+"}", value);
            predicate.withPath(path);
        }
        return this;
    }

    public StubService thenStatus(int statusCode) {
        response.withStatusCode(statusCode);
        return this;
    }

    public StubService thenBody(Object body) {
        String bodyString = MapperUtil.writeValueAsString(body);
        return thenBodyString(bodyString);
    }

    public StubService thenBodyString(String bodyString) {
        response.withBody(bodyString);
        return this;
    }

    public StubService thenContentType(String contentType) {
        return thenHeader("Content-Type", contentType);
    }

    public StubService thenHeader(String name, String value) {
        response.addHeader(name, value);
        return this;
    }

    public StubService and() {
        return this;
    }

    public void create() {
        if (imposter != null) {
            delete(); // delete existing imposter first.
        } else {
            // if there's no imposter, build new one.
            imposter = new ImposterBuilder().onPort(server.getPort()).build();
        }
        stub.addPredicates(Arrays.asList(predicate));
        stub.addResponse(response);
        imposter.addStub(stub);

        int statusCode = client.createImposter(imposter);
        if (statusCode != HttpStatus.SC_CREATED) {
            throw new StubServiceException("Mountebank fail to create imposter. \n"
                    +new JsonPath(imposter.toString()).prettify());
        }
    }

    public void delete() {
        if (imposter != null) {
            String result = client.deleteImposter(server.getPort());
            if (!result.contains(String.valueOf(server.getPort()))) {
                throw new StubServiceException("Mountebank fail to delete imposter. \n"
                        +new JsonPath(result).prettify());
            }
        }
    }

    private Imposter getImposter() {
        try {
            Imposter imposter = client.getImposter(server.getPort());
            return imposter.containsKey("port")? imposter : null;
        } catch (ParseException e) {
            throw new StubServiceException("Mountebank is fail to parse imposter.", e.getCause());
        }
    }
}
