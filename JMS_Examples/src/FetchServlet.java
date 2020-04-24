import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringJoiner;

/**
 * Name: soham chakraborti.
 *
 * @author sohamc.
 */

@WebServlet(name = "FetchServlet", urlPatterns = {"/fetchResponses"})
public class FetchServlet extends HttpServlet {

    // Lookup the ConnectionFactory using resource injection and assign to cf
    @Resource(name = "jms/myConnectionFactory")
    private ConnectionFactory cf;
    // lookup the Queue using resource injection and assign to q
    @Resource(name = "jms/myQueue3")
    private Queue q3;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            // With the ConnectionFactory, establish a Connection, and then a Session on that Connection
            Connection con = cf.createConnection();
            con.start();
            Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);


            StringJoiner stringJoiner = new StringJoiner("\n");
            while (true) {
                MessageConsumer reader = session.createConsumer(q3);
                Message m = reader.receive(1000);
                TextMessage tm = (TextMessage) m;
                if (tm == null) {
                    break;
                }
                stringJoiner.add(tm.getText());
            }
            // Close the connection
            con.close();
            if (stringJoiner.toString().isEmpty()) {
                out.println("<HTML><BODY><H1> Nothing present </H1>");
            } else {
                out.println("<HTML><BODY><H1>Got all messages from queue 3 ::" + stringJoiner.toString() + "</H1>");
            }
            out.println("</BODY></HTML>");
        } catch (Exception e) {
            System.out.println("Servlet through exception " + e);
        } finally {
            out.close();
        }
    }

}
