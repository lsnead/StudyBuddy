package com.snead.studybuddy;



/**
 * Created by snead on 4/29/2018.
 */

public class RoomReservation {
    String id;
    String room;
    String email;
    String date;
    int time;

    public RoomReservation(){

    }

    public RoomReservation(String id, String room, String email, String date, int time) {
        this.id = id;
        this.room = room;
        this.email = email;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
