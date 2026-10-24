package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
	
	private final Map<Long, Bucket> buckets = new ConcurrentHashMap<>();
	
	public void check(Long userId, String action, int maxRequests, Duration window) {
		Bucket bucket = buckets.computeIfAbsent(userId, k -> new Bucket());
		if (!bucket.allow(action, maxRequests, window)) {
			throw new ApiException("Rate limit exceeded for " + action + ". Try again later.",
				HttpStatus.TOO_MANY_REQUESTS);
		}
	}
	
	private static class Bucket {
		
		private final Map<String, Counter> counters = new ConcurrentHashMap<>();
		
		boolean allow(String action, int max, Duration window) {
			Counter c = counters.computeIfAbsent(action, k -> new Counter());
			synchronized (c) {
				Instant now = Instant.now();
				if (c.windowStart == null || now.isAfter(c.windowStart.plus(window))) {
					c.windowStart = now;
					c.count = 0;
				}
				if (c.count >= max) return false;
				c.count++;
				return true;
			}
		}
		
	}
	
	private static class Counter {
		
		Instant windowStart;
		int count;
		
	}
	
}