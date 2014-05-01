package com.jamierf.rsc.client.ui;

import com.jamierf.rsc.client.loader.GameClient;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame {

    private final GameClient client;

    public ClientFrame(GameClient client, String title) {
        super(title);

        this.client = client;

        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setResizable(false);
        super.add(client, BorderLayout.CENTER);
        super.pack();
    }
}
