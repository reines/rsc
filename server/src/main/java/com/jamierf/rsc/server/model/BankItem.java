package com.jamierf.rsc.server.model;

import com.google.common.base.Objects;

// TODO: Probably should be in some kind of api package
public class BankItem {

    private final int itemId;
    private final long count;

    public BankItem(int itemId, long count) {
        this.itemId = itemId;
        this.count = count;
    }

    public int getItemId() {
        return itemId;
    }

    public long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("itemId", itemId)
                .add("count", count)
                .toString();
    }
}
