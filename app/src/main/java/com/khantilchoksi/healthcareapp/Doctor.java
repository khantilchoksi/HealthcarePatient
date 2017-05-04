package com.khantilchoksi.healthcareapp;

import java.util.ArrayList;

/**
 * Created by khantilchoksi on 29/03/17.
 */

public class Doctor {
    private String doctorId;
    private String doctorName;
    private ArrayList<String> clinicsAreaList;
    private int experience;

    public int getExperience() {
        return experience;
    }

    public String getDoctorQualifications() {
        return doctorQualifications;
    }

    public String getDoctorSpecialities() {
        return doctorSpecialities;
    }

    private String doctorQualifications;
    private String doctorSpecialities;

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public ArrayList<String> getClinicsAreaList() {
        return clinicsAreaList;
    }

    public Doctor(String doctorId, String doctorName, int experience, String doctorQualifications,
                  String doctorSpecialities, ArrayList<String> clinicsAreaList) {

        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.clinicsAreaList = clinicsAreaList;
        this.doctorQualifications = doctorQualifications;
        this.doctorSpecialities = doctorSpecialities;

        this.experience = experience;
    }
}
