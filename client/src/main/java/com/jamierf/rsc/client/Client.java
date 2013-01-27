package com.jamierf.rsc.client;

import com.jamierf.rsc.client.loader.GameClient;
import com.jamierf.rsc.client.ui.ClientFrame;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;

public class Client {

    private static final String SERVER_HOST = "localhost";
    private static final boolean SERVER_MEMBERS = true;
    private static final String CLIENT_TITLE = "Test";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static URL buildResourceURL() throws MalformedURLException {
        return new URL("http", "classic2.runescape.com", "/");
    }

    public static void main(String[] args) throws Exception {
        new Client(SERVER_HOST, CLIENT_TITLE);
    }

    private final GameClient client;
    private final ClientFrame frame;

    public Client(String serverHost, String title) throws Exception {
        final RSAPublicKey key = (RSAPublicKey) new PEMReader(new InputStreamReader(Client.class.getResourceAsStream("key.pem"))).readObject();

        client = new GameClient(Client.buildResourceURL(), serverHost, key, SERVER_MEMBERS);
        frame = new ClientFrame(client, title);

        client.run();
        frame.setVisible(true);
    }
}
