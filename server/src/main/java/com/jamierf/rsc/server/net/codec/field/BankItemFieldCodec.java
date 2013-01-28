package com.jamierf.rsc.server.net.codec.field;

import com.jamierf.rsc.server.model.BankItem;
import org.jboss.netty.buffer.ChannelBuffer;

public class BankItemFieldCodec extends FieldCodec<BankItem> {
    @Override
    public BankItem doDecode(ChannelBuffer buffer) throws FieldCodecException {
        final short id = FieldCodec.decode(short.class, buffer);
        final int count = FieldCodec.decode(int.class, buffer);

        return new BankItem(id, count);
    }

    @Override
    public void doEncode(BankItem value, ChannelBuffer buffer) throws FieldCodecException {
        FieldCodec.encode((short) value.getItemId(), buffer);
        FieldCodec.encode((int) value.getCount(), buffer);
    }
}
