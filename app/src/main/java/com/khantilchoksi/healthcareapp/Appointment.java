package com.khantilchoksi.healthcareapp;

/**
 * Created by khantilchoksi on 29/03/17.
 */

public class Appointment {
    private String appointmentId;
    private String doctorName;

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentDay() {
        return appointmentDay;
    }

    public String getAppointmentStartTime() {
        return appointmentStartTime;
    }

    public String getAppointmentEndTime() {
        return appointmentEndTime;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public Appointment(String appointmentId, String doctorName, String appointmentDate, String appointmentDay, String appointmentStartTime, String appointmentEndTime, String clinicAddress) {

        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentDay = appointmentDay;
        this.appointmentStartTime = appointmentStartTime;
        this.appointmentEndTime = appointmentEndTime;
        this.clinicAddress = clinicAddress;
    }

    private String appointmentDate;
    private String appointmentDay;
    private String appointmentStartTime;
    private String appointmentEndTime;
    private String clinicAddress;

}
