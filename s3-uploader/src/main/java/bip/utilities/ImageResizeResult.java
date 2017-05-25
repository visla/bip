package bip.utilities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResizeResult {

    private String large_path;
    private String square_small_path;

    public ImageResizeResult() {
    }

    public String getLargePath() {
        return large_path;
    }


    public String getSquareSmallPath() {
        return square_small_path;
    }

    public void setLargePath(String large_path) {
        this.large_path = large_path;
    }

    public void setSquareSmallPath(String square_small_path) {
        this.square_small_path = square_small_path;
    }

    @Override
    public String toString() {
        return "ImageResizeResult{" +
                "large_path='" + large_path + '\'' +
                ", square_small_path=" + square_small_path +
                '}';
    }
}