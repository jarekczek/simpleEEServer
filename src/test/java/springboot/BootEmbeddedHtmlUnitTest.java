package springboot;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = MySpringBootConfig.class)
public class BootEmbeddedHtmlUnitTest {
  @LocalServerPort
  int port;

  @Test
  public void testView()
  throws InterruptedException, FailingHttpStatusCodeException,
         MalformedURLException, IOException {
    System.out.println("server port: " + port);
    WebClient wc = new WebClient();
    HtmlPage p = (HtmlPage)wc.getPage("http://localhost:" + port + "/h");
    assertEquals(200, p.getWebResponse().getStatusCode());
    String title = p.getElementsByTagName("title").item(0).getTextContent();
    assertEquals("tttt", title);
    wc.close();
  }
}
