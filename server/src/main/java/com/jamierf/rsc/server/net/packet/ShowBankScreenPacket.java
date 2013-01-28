package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.jamierf.rsc.server.model.BankItem;
import com.jamierf.rsc.server.net.codec.packet.PacketCodecException;
import com.jamierf.rsc.server.net.codec.packet.RawPacket;

import java.util.Collection;

public class ShowBankScreenPacket extends RawPacket {

    private final int maxItems;
    private final Collection<BankItem> items;

    public ShowBankScreenPacket(int maxItems, Collection<BankItem> items) throws PacketCodecException {
        this.maxItems = maxItems;
        this.items = items;

        super.write((byte) items.size());
        super.write((byte) maxItems);

        for (BankItem item : items)
            super.write(item);
    }

    @Override
    protected void decode() throws Exception {
        // NOOP, outgoing only
        // TODO: Make IncomingPacket (IncomingRawPacket), and OutgoingPacket (OutgoingRawPacket)
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("maxItems", maxItems)
                .add("items", items)
                .toString();
    }
}
