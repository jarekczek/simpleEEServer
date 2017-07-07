package myee;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/*")
public class Controller extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
  throws ServletException, IOException
  {
    Logger log = Logger.getLogger("bs");
    String path = Optional.ofNullable(req.getPathInfo())
      .orElse("/");
    if (path.startsWith("/h")) {
      html(req, resp);
      return;
    }
    log.info("alive");
    resp.getWriter().println("hello");
  }

  private void html(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {
    resp.setContentType("text/html");
    resp.getWriter().println("<html><title>tttt</title>hello</html>");
  }

}
