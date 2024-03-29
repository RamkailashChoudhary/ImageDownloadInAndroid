package com.app.simplify.imagedownload;

import android.graphics.Bitmap;


/**
 * BitmapCache  
 * 
 * @author zonghai@gmail.com
 */
public interface BitmapCache {

    /**
     * Test if a specified bitmap exists
     * @param key
     * @return
     */
    public boolean exists(String key);
    
    
    /**
     * Invalidate a bitmap, release any resource associated.
     * @param key
     */
    public void invalidate(String key);
    
    
    /**
     * Retrieve the bitmap, return null means cache miss
     * @param key
     */
    public Bitmap loadData(String key);
    
    
    /**
     * Store bitmap
     * @param key
     * @param data
     */
    public void storeData(String key, Object data);
    
    
    /**
     * Clear this bitmap cache, reclaim all resources assigned.
     */
    public void clear();
}
