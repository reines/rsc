package com.jamierf.rsc.server.net.codec.packet;

import java.io.IOException;

public class PacketCodecException extends IOException {

    public PacketCodecException(String message) {
        super(message);
    }

    public PacketCodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
