package myee;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/*")
public class Controller extends HttpServlet {

  private final String TITLE = "1.0.001";

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
      if (path.startsWith("/jms")) {
        jms(req, resp);
        return;
      }
      if (path.startsWith("/jndi")) {
        jndi(req, resp);
        return;
      }
    } catch (NamingException | JMSException e) {
      throw new ServletException(e);
    }
    log.info("alive");
    resp.getWriter().println(hello());
  }

  private void html(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {
    resp.setContentType("text/html");
    resp.getWriter().println("<html>" + title("") + hello() + "</html>");
  }

  private String title(String addTitle) {
    return "<title>" + TITLE + " " + addTitle + "</title>";
  }

  private void jndi(HttpServletRequest req, HttpServletResponse resp)
          throws IOException, NamingException {
    String[] paths = { "jms/kolejka", "jms/kolejdef", "jms/connfacdef",
                       "jms/topicdef", "jms/topicconnfac" };
    InitialContext ctx = new InitialContext();
    resp.setContentType("text/html");
    resp.getWriter().println("<html>" + title("jndi"));
    for (String path: paths) {
      Object o = null;
      try {
        o = ctx.lookup(path);
      } catch (NamingException ne) {}
      printObjectInfo(resp, path, o);
    }
    resp.getWriter().println("</html>");
  }

  private void printObjectInfo(HttpServletResponse resp, String desc, Object o) throws IOException {
    resp.getWriter().print("object " + desc + ": " + o);
    if (o != null) {
      resp.getWriter().println(", typ: " + o.getClass() + "<br/>");

      resp.getWriter().println("interfaces:<br/>");
      Set<Object> seenIntfs = new HashSet<>();
      Class cl = o.getClass();
      while (cl != null) {
        for (Object intf : o.getClass().getInterfaces()) {
          if (seenIntfs.contains(intf))
            continue;
          resp.getWriter().println("interface: " + intf + "<br/>");
          seenIntfs.add(intf);
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

  private void jms(HttpServletRequest req, HttpServletResponse resp)
          throws IOException, NamingException, JMSException {
    InitialContext initCtx = new InitialContext();
    ConnectionFactory connFac;
    Destination dst;
    if (req.getParameter("topic") != null) {
      connFac = (ConnectionFactory) initCtx.lookup("jms/topicconnfac");
      dst = (Destination) initCtx.lookup("jms/topic");
    } else {
      connFac = (ConnectionFactory) initCtx.lookup("jms/connfac");
      dst = (Destination) initCtx.lookup("jms/kolejka");
    }
    Connection conn = connFac.createConnection("jarek", "");
    ObjectMessage msg = sendMessage(dst, conn);
    resp.getWriter().println("sent " + msg + "<br/>");
    if (req.getParameter("read") != null) {
      if (dst instanceof Topic)
        printTopicMessages((TopicConnection) conn, (Topic) dst, resp);
      else
        printMessages(conn, dst, resp);
    }
  }

  private ObjectMessage sendMessage(Destination dst, Connection conn)
  throws JMSException
  {
    Session qs = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
    ObjectMessage msg = qs.createObjectMessage("msg1");
    MessageProducer prod = qs.createProducer(dst);
    prod.send(msg);
    prod.close();
    qs.close();
    return msg;
  }

  private void printMessages(Connection conn, Destination q, HttpServletResponse resp)
          throws IOException, JMSException
  {
    resp.getWriter().println("messages in queue " + q + "<br/>");
    Session qs = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
    MessageConsumer cons = qs.createConsumer(q);
    conn.start();
    resp.getWriter().println("<ol>");
    while (true) {
      Message msg = cons.receiveNoWait();
      if (msg == null)
        break;
      resp.getWriter().println("<li>in dest: " + msg + "</li>");
    }
    resp.getWriter().println("</ol>");
    /*
    Enumeration enu = brow.getEnumeration();
    while (enu.hasMoreElements()) {
      resp.getWriter().println("in enum: " + enu.nextElement() + "<br/>");
    }
    */
    resp.getWriter().println("<br/>");
  }

  private void printTopicMessages(TopicConnection conn, Topic t, HttpServletResponse resp)
          throws IOException, JMSException
  {
    final String subName = "subname5";
    resp.getWriter().println("messages in topic " + t + "<br/>");
    Session s = conn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
    conn.start();
    Logger.getLogger("jarek").info("clientid: " + conn.getClientID() + ", subname: " + subName);
    TopicSubscriber sub = s.createDurableSubscriber(t, subName);
    try {
      resp.getWriter().println("<ol>");
      Logger.getLogger("jarek").info("nolocal: " + sub.getNoLocal());
      while (true) {
        Message msg = sub.receiveNoWait();
        if (msg == null)
          break;
        resp.getWriter().println("<li>in dest: " + msg + "</li>");
      }
    } finally {
      sub.close();
      //s.unsubscribe(subName);
    }
    //conn.stop();
    resp.getWriter().println("</ol>");
    /*
    Enumeration enu = brow.getEnumeration();
    while (enu.hasMoreElements()) {
      resp.getWriter().println("in enum: " + enu.nextElement() + "<br/>");
    }
    */
    resp.getWriter().println("<br/>");
  }
}
