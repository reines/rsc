package com.jamierf.rsc.server.net.codec.field;

import com.google.common.collect.ImmutableMap;
import com.jamierf.rsc.server.model.BankItem;
import org.jboss.netty.buffer.ChannelBuffer;

public abstract class FieldCodec<T> {

    // TODO: Writing of unsigned stuff
    private static final ImmutableMap<Class, FieldCodec> FIELD_CODECS = ImmutableMap.<Class, FieldCodec>builder()
            // primitives
            .put(boolean.class, new BooleanFieldCodec())
            .put(byte.class, new ByteFieldCodec())
            .put(short.class, new ShortFieldCodec())
            .put(int.class, new IntegerFieldCodec())
            .put(long.class, new LongFieldCodec())

            // primitive class equivalents
            .put(Boolean.class, new BooleanFieldCodec())
            .put(Byte.class, new ByteFieldCodec())
            .put(Short.class, new ShortFieldCodec())
            .put(Integer.class, new IntegerFieldCodec())
            .put(Long.class, new LongFieldCodec())

            // primitive arrays
            .put(byte[].class, new ByteArrayFieldCodec())

            // classes
            .put(String.class, new StringFieldCodec())
            .put(ChannelBuffer.class, new ChannelBufferFieldCodec())

            // models
            .put(BankItem.class, new BankItemFieldCodec())

            .build();

    public static <U> U decode(Class<U> type, ChannelBuffer buffer) throws FieldCodecException {
        final FieldCodec<U> codec = FIELD_CODECS.get(type);
        if (codec == null)
            throw new FieldCodecException("Unable to decode unknown type: " + type);

        return codec.doDecode(buffer);
    }

    public static void encode(Object value, ChannelBuffer buffer) throws FieldCodecException {
        final Class type = value.getClass();
        final FieldCodec codec = FIELD_CODECS.get(type);
        if (codec == null)
            throw new FieldCodecException("Unable to encode unknown type: " + type);

        codec.doEncode(value, buffer);
    }

    protected abstract T doDecode(ChannelBuffer buffer) throws FieldCodecException;
    protected abstract void doEncode(T value, ChannelBuffer buffer) throws FieldCodecException;
}
