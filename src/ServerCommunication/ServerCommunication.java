package ServerCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomek
 */
public final class ServerCommunication {

    URL url;
    URLConnection connection;
    OutputStreamWriter out;

    private final static Logger logger = Logger.getLogger(ServerCommunication.class.getName());

    public ServerCommunication() {
    }

    public void openConnection(String path) {
        try {
            url = new URL("http://192.168.1.7:8080/" + path); // http://192.168.1.7:8080/
            connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Connection with the server could not be established. {0}", e.getLocalizedMessage());
        }
    }

    public boolean send(String msg, String servicePath) {
        boolean result;
        try {
            openConnection(servicePath);
            out = new OutputStreamWriter(connection.getOutputStream());
            out.write(msg);
            out.close();
            result = verifySending();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Sending msg: \"{0}\" to server failed. {1}", new Object[]{msg.trim(), e.getMessage()});
            result = false;
        }
        return result;
    }

    private boolean verifySending() {
        boolean result = true;
        try {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                while (in.readLine() != null) {
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "No response came from server after sending message. {0}.",
                    new Object[]{e.getMessage()});
            result = false;
        }
        return result;
    }
}
