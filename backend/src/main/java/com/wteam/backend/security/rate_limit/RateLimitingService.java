package com.wteam.backend.security.rate_limit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitingService {
    private final Cache<String, Bucket> loginBuckets = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofHours(1))
            .build();

    private final Cache<String, Bucket> publicApiBuckets = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofHours(1))
            .build();


    public Bucket resolveLoginBucket(final String ip) {
        return loginBuckets.get(ip, this::newLoginBucket);
    }

    public Bucket resolvePublicApiBucket(final String ip) {
        return publicApiBuckets.get(ip, this::newPublicApiBucket);
    }


    private Bucket newLoginBucket(final String ip) {
        Bandwidth limit = Bandwidth.builder().capacity(5).refillIntervally(5, Duration.ofMinutes(1)).build();
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket newPublicApiBucket(final String ip) {
        Bandwidth limit = Bandwidth.builder().capacity(100).refillIntervally(100, Duration.ofMinutes(1)).build();
        return Bucket.builder().addLimit(limit).build();
    }
}
