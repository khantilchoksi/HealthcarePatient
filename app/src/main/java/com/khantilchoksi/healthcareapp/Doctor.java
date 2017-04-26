package com.khantilchoksi.healthcareapp;

import java.util.ArrayList;

/**
 * Created by khantilchoksi on 29/03/17.
 */

public class Doctor {
    private String doctorId;
    private String doctorName;
    private ArrayList<String> clinicsAreaList;

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public ArrayList<String> getClinicsAreaList() {
        return clinicsAreaList;
    }

    public Doctor(String doctorId, String doctorName, ArrayList<String> clinicsAreaList) {

        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.clinicsAreaList = clinicsAreaList;
    }
}
