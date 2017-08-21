package request;

import org.junit.Test;
import org.mbtest.javabank.Client;
import stub.Mountebank;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class MountebankTest {

    @Test
    public void runTest() throws Exception {
        //GIVEN: mountebank is not running.
        Mountebank.stop();

        //WHEN
        Mountebank.run();

        //THEN
        Client client = new Client();
        boolean isRunning = client.isMountebankRunning();
        assertTrue(isRunning);
    }

}
