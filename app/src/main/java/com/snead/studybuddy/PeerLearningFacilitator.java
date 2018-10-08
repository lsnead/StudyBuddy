package com.snead.studybuddy;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by snead on 5/6/2018.
 */

public class PeerLearningFacilitator {
    private String department;
    private int numHours;

    private DatabaseReference databaseStudentLocations = FirebaseDatabase.getInstance().getReference("PeerLearningFacilitators");

    public PeerLearningFacilitator(){

    }

    public PeerLearningFacilitator(String department) {
        this.department = department;
        this.numHours = 2;
    }

    private String[] departments = {"Physics", "Psychology", "Sociology", "Accounting",
            "Political Science", "Computer Science", "Math & Statistics", "Writing Center",
            "Economics", "Japanese", "Finance", "Classics", "Career Services", "Biology", "Chinese",
            "French", "Italian", "Spanish", "Logic"};


    public void addFacilitators(){

        //Saving the PLFs
        for(int i=0; i<departments.length; i++){
            databaseStudentLocations.push().setValue(new PeerLearningFacilitator(departments[i]));
        }

    }

    public String getDepartment() {
        return department;
    }


    public int getNumHours() {
        return numHours;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


}
