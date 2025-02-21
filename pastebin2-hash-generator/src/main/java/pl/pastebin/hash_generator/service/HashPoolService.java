package pl.pastebin.hash_generator.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.IntStream;

@Service

public final class HashPoolService {
    // Redis key for the hash pool
    private static final String HASH_POOL_KEY = "hash_pool";
    // Maximum hashes in the pool
    private final int POOL_SIZE = 1000;
    // Number of hashes generated per batch
    private static final int BATCH_SIZE = 100;

    private final StringRedisTemplate redisTemplate;

    public HashPoolService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Fills the Redis hash pool if it's below capacity.
     * Generates hashes in batches and waits until usage drops.
     */
    public void fillPoolAsync() {
        Long size = redisTemplate.opsForList().size(HASH_POOL_KEY);
        if (size == null || size < POOL_SIZE) {
            int toGenerate = POOL_SIZE - (size == null ? 0 : size.intValue());
            int batch = Math.min(toGenerate, BATCH_SIZE);

            List<String> hashes = IntStream.range(0, batch)
                    .mapToObj(i -> encodeUuidToBase64(UUID.randomUUID()))
                    .toList();

            redisTemplate.opsForList().leftPushAll(HASH_POOL_KEY, hashes);
            System.out.println("Generated " + hashes.size() + " hashes, added to Redis.");
        }

        // Wait until pool size drops to half of POOL_SIZE
        while (redisTemplate.opsForList().size(HASH_POOL_KEY) != null &&
                redisTemplate.opsForList().size(HASH_POOL_KEY) > POOL_SIZE / 2) {
            Thread.onSpinWait();
        }
    }

    /**
     * Encodes a UUID to a URL-safe Base64 string without padding.
     */
    private String encodeUuidToBase64(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }

    /**
     * Retrieves a hash from the Redis pool.
     * @throws RuntimeException if the pool is empty.
     */
    public String getHashFromPool() {
        String hash = redisTemplate.opsForList().rightPop(HASH_POOL_KEY);
        if (hash == null) {
            throw new RuntimeException("Hash pool is empty!");
        }
        return hash;
    }
}