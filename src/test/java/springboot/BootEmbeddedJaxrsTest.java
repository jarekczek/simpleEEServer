package springboot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
  webEnvironment = WebEnvironment.RANDOM_PORT,
  classes = MySpringBootConfig.class
)
@ComponentScan
public class BootEmbeddedJaxrsTest {
  @LocalServerPort int port;
  
  @Test
  public void testRoot() {
    System.out.println("server port: " + port);
    Response resp = ClientBuilder.newClient()
      .target("http://localhost:" + port + "/")
      .request()
      .get();
    String respText = resp.readEntity(String.class);
    System.out.println("resp: " + resp);
    assertEquals(200, resp.getStatus());
    assertThat(respText).contains("hello");
  }

}
