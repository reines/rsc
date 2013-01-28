package com.jamierf.rsc.server.net.codec.field;

import com.jamierf.rsc.server.net.codec.packet.PacketCodecException;

public class FieldCodecException extends PacketCodecException {

    public FieldCodecException(String message) {
        super(message);
    }

    public FieldCodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
