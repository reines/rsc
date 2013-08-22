package com.jamierf.rsc.server.net.packet;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.jamierf.rsc.server.model.BankItem;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.jamierf.rsc.server.net.codec.packet.PacketBuffer;

import java.io.IOException;
import java.util.Collection;

public class ShowBankScreenPacket extends Packet {

    private int maxItems;
    private Collection<BankItem> items;

    protected ShowBankScreenPacket() {}

    public ShowBankScreenPacket(int maxItems, Collection<BankItem> items) {
        this.maxItems = maxItems;
        this.items = items;
    }

    @Override
    protected void decode(PacketBuffer buffer) throws IOException {
        final int count = buffer.read(byte.class);
        this.maxItems = buffer.read(byte.class);

        items = Lists.newArrayListWithCapacity(count);
        for (int i = 0;i < count;i++ ) {
            items.add(buffer.read(BankItem.class));
        }
    }

    @Override
    protected void encode(PacketBuffer buffer) throws IOException {
        buffer.write((byte) items.size());
        buffer.write((byte) maxItems);

        for (BankItem item : items) {
            buffer.write(item);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("maxItems", maxItems)
                .add("items", items)
                .toString();
    }
}
