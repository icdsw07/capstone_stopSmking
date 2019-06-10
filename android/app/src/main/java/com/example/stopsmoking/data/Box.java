package com.example.stopsmoking.data;

import org.opencv.core.Mat;

public class Box {
    public float dist;
    public long image;
    public Box(){
        dist = -1;
    }

    public void setDistance(float distance) {
        this.dist = distance;
    }

    public void setImage(long image) {
        this.image = image;
    }

    public float getDistance() {
        return dist;
    }

    public long getImage() {
        return image;
    }
}
