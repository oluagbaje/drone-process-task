/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musala.drone.service.dal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author ADMIN
 */
public class DefaultCacheService implements CacheService {
    
     static final Map<String, Object> cache = new ConcurrentHashMap<>(); 

    @Override
    public void store(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }
    
}
