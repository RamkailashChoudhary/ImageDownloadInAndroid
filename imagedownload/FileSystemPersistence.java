package com.app.simplify.imagedownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.util.Log;


/**
 * File system implementation of persistent storage for downloaded images.
 * 
 * @author zonghai@gmail.com
 */
public class FileSystemPersistence implements BitmapCache{

    private static String TAG = "FileSystemPersistence";
    private static boolean DEBUG = true;
    
    
    private String mBaseDir;
    
    
    public FileSystemPersistence ( String baseDir ) {
        mBaseDir = baseDir;
    }
    
    
    @Override
    public void clear() {
        try {
            this.removeDir(new File(mBaseDir));
        } 
        catch (IOException e) {
            throw new RuntimeException ( e );
        }
    }

    
    @Override
    public boolean exists(String key) {
        File file = new File( new File(mBaseDir), key) ;
        return file.exists();
    }

    
    @Override
    public void invalidate(String key) {
    }

    
    @Override
    public Bitmap loadData(String key) {
        if( !exists(key) ) {
            return null;
        }
        
        File file = new File( new File(mBaseDir), key) ;
        return BitmapUtil.decodeFile(file.getAbsolutePath(), HttpImageManager.DECODING_MAX_PIXELS_DEFAULT);
    }

    
    @Override
    public void storeData(String key, Object data) {
        File file = new File( new File(mBaseDir), key) ;
        FileOutputStream outputStream = null;
        
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            outputStream = new FileOutputStream(file);
            
            outputStream.write((byte[])data);
            outputStream.flush();
        }
        catch (IOException e) {
            if(DEBUG) Log.d(TAG, "error storing bitmap", e);
        }
        finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {}
            }
        }
    }
    
    
    /**
     * Delete a directory
     *
     * @param d the directory to delete
     */
    private void removeDir(File d) throws IOException{
        // to see if this directory is actually a symbolic link to a directory,
        // we want to get its canonical path - that is, we follow the link to
        // the file it's actually linked to
        File candir = d.getCanonicalFile();
  
        // a symbolic link has a different canonical path than its actual path,
        // unless it's a link to itself
        if (!candir.equals(d.getAbsoluteFile())) {
            // this file is a symbolic link, and there's no reason for us to
            // follow it, because then we might be deleting something outside of
            // the directory we were told to delete
            return;
        }
  
        // now we go through all of the files and subdirectories in the
        // directory and delete them one by one
        File[] files = candir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
  
                // in case this directory is actually a symbolic link, or it's
                // empty, we want to try to delete the link before we try
                // anything
                boolean deleted = file.delete();
                if (!deleted) {
                    // deleting the file failed, so maybe it's a non-empty
                    // directory
                    if (file.isDirectory()) removeDir(file);
  
                    // otherwise, there's nothing else we can do
                }
            }
        }
  
        // now that we tried to clear the directory out, we can try to delete it
        // again
        d.delete();  
    }    
}
