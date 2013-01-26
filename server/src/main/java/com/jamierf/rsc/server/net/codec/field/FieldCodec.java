package com.jamierf.rsc.server.net.codec.field;

import com.google.common.collect.ImmutableMap;
import org.jboss.netty.buffer.ChannelBuffer;

public abstract class FieldCodec<T> {

    private static final ImmutableMap<Class, FieldCodec> FIELD_CODECS = ImmutableMap.<Class, FieldCodec>builder()
            .put(boolean.class, new BooleanFieldCodec())
            .put(byte.class, new ByteFieldCodec())
            .put(short.class, new ShortFieldCodec())
            .put(int.class, new IntegerFieldCodec())
            .put(long.class, new LongFieldCodec())
            .put(Boolean.class, new BooleanFieldCodec())
            .put(Byte.class, new ByteFieldCodec())
            .put(Short.class, new ShortFieldCodec())
            .put(Integer.class, new IntegerFieldCodec())
            .put(Long.class, new LongFieldCodec())
            .put(String.class, new StringFieldCodec())
            .put(ChannelBuffer.class, new ChannelBufferFieldCodec())
            .put(byte[].class, new ByteArrayFieldCodec())
            .build();

    public static <U> FieldCodec<U> getInstance(Class<U> type) {
        return FIELD_CODECS.get(type);
    }

    public abstract T decode(ChannelBuffer buffer);
    public abstract void encode(T value, ChannelBuffer buffer);
}