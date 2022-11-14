/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musala.drone.service.dal;

/**
 *
 * @author ADMIN
 */
public interface CacheService {
    
    public CacheService defaultInstance = new DefaultCacheService();

    public void store(String key, Object value);

    public Object get(String key);
    
    
}
