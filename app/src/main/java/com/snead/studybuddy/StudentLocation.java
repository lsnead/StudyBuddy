package com.snead.studybuddy;

/**
 * Created by snead on 4/29/2018.
 */

public class StudentLocation {
    String id;
    String name;
    String message;
    float x;
    float y;
    int occupiedTime;
    int floor;

    public StudentLocation() {

    }

    public StudentLocation(String id, String name, String message, float x, float y, int occupiedTime, int floor) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.x = x;
        this.y = y;
        this.occupiedTime = occupiedTime;
        this.floor = floor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getOccupiedTime() {
        return occupiedTime;
    }

    public void setOccupiedTime(int occupiedTime) {
        this.occupiedTime = occupiedTime;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
