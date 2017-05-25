package bip.controller;

public class UploadResult {
    private String imageUrl;

    public UploadResult(String imageUrl) {
        this.imageUrl = imageUrl;
        
    }

    public String getImageUrl() {
        return this.imageUrl;
    }
}