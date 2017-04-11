package com.naveen.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "/Users/naveen/Documents/upload-dir/";
    
    /**
     * Folder location for storing files
     */
    private String locationDock = "/Users/naveen/Documents/temp/";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getlocationDockLocation() {
        return locationDock;
    }

    public void setlocationDockLocation(String location) {
        this.locationDock = location;
    }
}