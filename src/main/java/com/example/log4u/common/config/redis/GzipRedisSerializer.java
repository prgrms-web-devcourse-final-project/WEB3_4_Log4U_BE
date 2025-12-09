package com.example.log4u.common.config.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GzipRedisSerializer<T> implements RedisSerializer<T> {

    private static final byte[] GZIP_MAGIC_BYTES = new byte[]{
            (byte) (GZIPInputStream.GZIP_MAGIC & 0xFF),
            (byte) ((GZIPInputStream.GZIP_MAGIC >> 8) & 0xFF)
    };

    private final ObjectMapper objectMapper;
    private final TypeReference<T> typeReference;
    private final int minCompressionSize;
    private final int bufferSize;

    public GzipRedisSerializer(ObjectMapper objectMapper, TypeReference<T> typeReference) {
        this(objectMapper, typeReference, 1024, 4096);
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return null;
        }
        try {
            if (minCompressionSize == -1) {
                return encodeGzip(t);
            }
            byte[] bytes = objectMapper.writeValueAsBytes(t);
            if (bytes.length <= minCompressionSize) {
                return bytes;
            }
            return encodeGzip(t);

        } catch (Exception e) {
            throw new SerializationException("Failed to serialize", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        try {
            if (isGzipCompressed(bytes)) {
                byte[] decompressed = decodeGzip(bytes);
                return objectMapper.readValue(decompressed, 0, decompressed.length, typeReference);
            }
            return objectMapper.readValue(bytes, 0, bytes.length, typeReference);

        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize", e);
        }
    }

    private byte[] encodeGzip(byte[] original) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferSize);
             GZIPOutputStream gos = new GZIPOutputStream(bos, bufferSize) {
                 {
                     def.setLevel(Deflater.BEST_SPEED);
                 }
             }
        ) {
            FileCopyUtils.copy(original, gos);
            return bos.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode GZIP", e);
        }
    }

    private byte[] encodeGzip(T t) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferSize);
             GZIPOutputStream gos = new GZIPOutputStream(bos, bufferSize) {
                 {
                     def.setLevel(Deflater.BEST_SPEED);
                 }
             }
        ) {
            objectMapper.writeValue(gos, t);
            return bos.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode GZIP", e);
        }
    }

    private byte[] decodeGzip(byte[] encoded) {
        try (
                ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
                GZIPInputStream gis = new GZIPInputStream(bis, bufferSize);
                ExposedBufferByteArrayOutputStream out = new ExposedBufferByteArrayOutputStream(bufferSize);
        ) {
            FileCopyUtils.copy(gis, out);
            return out.getRawByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to decode GZIP", e);
        }
    }

    private boolean isGzipCompressed(byte[] bytes) {
        return bytes.length > 2 && bytes[0] == GZIP_MAGIC_BYTES[0] && bytes[1] == GZIP_MAGIC_BYTES[1];
    }

    static class ExposedBufferByteArrayOutputStream extends ByteArrayOutputStream {

        ExposedBufferByteArrayOutputStream(int size) {
            super(size);
        }

        public byte[] getRawByteArray() {
            return this.buf;
        }
    }
}
