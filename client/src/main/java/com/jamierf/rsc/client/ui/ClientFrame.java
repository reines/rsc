package com.jamierf.rsc.client.ui;

import com.jamierf.rsc.client.loader.client.GameClient;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame {

    public ClientFrame(GameClient client) {
        super(client.getName());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        add(client, BorderLayout.CENTER);
        pack();
    }
}
