import com.jayway.jsonpath.JsonPath;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

public class Test {

    private static final Logger log = LoggerFactory.getLogger(Test.class);

    private static ExecutorService executor = Executors.newFixedThreadPool(5);;

    private static List<String> listBrokerConnections() {

        final CloseableHttpClient client = HttpClientBuilder.create().build();
        try {
            final HttpGet request = new HttpGet(format("http://%s:%s@localhost:15672/api/connections",
                    "guest", "guest"));
            final HttpResponse response = client.execute(request);
            final String json = EntityUtils.toString(response.getEntity());
            return JsonPath.read(json, "$..name");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("Eror closing HttpClient", e);
            }
        }
    }

    private static List<String> initialConnections;

    private static void showConnections() {
        final List<String> current = listBrokerConnections();
        // Hide connections we already had when started
        current.removeAll(initialConnections);
        log.info("Current connections: {}", current);
    }

    public static void main(String[] args) {

        try {
            final ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPort(5672);

            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.useNio();
            factory.setAutomaticRecoveryEnabled(false);

            // Remember what connection we had initially so we suppress them from further output
            initialConnections = listBrokerConnections();

            log.info("Connecting...");
            final Connection connection = factory.newConnection(executor);

            showConnections();

            log.info("Closing connection...");
            connection.close();

            showConnections();

        } catch (Exception e) {
            log.error("Unexpected exception", e);
        }

        System.exit(0);
    }

}
