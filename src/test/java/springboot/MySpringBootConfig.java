package springboot;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import myee.Controller;

@EnableAutoConfiguration
@Configuration
public class MySpringBootConfig {
  @Bean public ServletRegistrationBean srb()
  {
    ServletRegistrationBean srb = new ServletRegistrationBean();
    srb.setServlet(new Controller());
    return srb;
  }
}
