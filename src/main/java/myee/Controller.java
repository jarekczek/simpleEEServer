package myee;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/*")
public class Controller extends HttpServlet {

  private String hello()
  {
    return "hello3";
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
  throws ServletException, IOException
  {
    Logger log = Logger.getLogger("bs");
    String path = req.getPathInfo();
    if (path == null)
      path = "/";
    try {
      if (path.startsWith("/h")) {
        html(req, resp);
        return;
      }
      if (path.startsWith("/jndi")) {
        jndi(req, resp);
        return;
      }
    } catch (NamingException ne) {
      throw new ServletException(ne);
    }
    log.info("alive");
    resp.getWriter().println(hello());
  }

  private void html(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {
    resp.setContentType("text/html");
    resp.getWriter().println("<html><title>tttt</title>" + hello() + "</html>");
  }

  private void jndi(HttpServletRequest req, HttpServletResponse resp)
          throws IOException, NamingException {
    String[] paths = { "jms/kolejka", "jms/kolejdef", "jms/connfacdef",
                       "jms/topicdef", "jms/topicconnfac" };
    InitialContext ctx = new InitialContext();
    resp.setContentType("text/html");
    resp.getWriter().println("<html><title>jndi</title>");
    for (String path: paths) {
      Object o = null;
      try {
        o = ctx.lookup(path);
      } catch (NamingException ne) {}
      resp.getWriter().print("jndi " + path + ": " + o);
      if (o != null) {
        resp.getWriter().println(", typ: " + o.getClass() + "<br/>");

        resp.getWriter().println("interfaces:<br/>");
        Class cl = o.getClass();
        while (cl != null) {
          for (Object intf : o.getClass().getInterfaces()) {
            resp.getWriter().println("interface: " + intf + "<br/>");
          }
          cl = cl.getSuperclass();
        }

        if (o instanceof Queue) {
          resp.getWriter().println("  it is a queue<br/>");
        }
        if (o instanceof ConnectionFactory) {
          resp.getWriter().println("  it is a connection factory<br/>");
        }

      }
      resp.getWriter().println("<br/>");
    }
    resp.getWriter().println("</html>");
  }
}
