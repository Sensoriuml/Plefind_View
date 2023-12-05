package com.android.plefind.plefindview;

import java.util.Date;
import java.util.Vector;

public class TableRow {
    private Date birthdate;
    private String diagnosis;
    private String hardware;
    private String healthy;
    private String height;
    private String lung;
    private String notes;
    private String sex;
    private String software;
    private String weight;
    private String objectID;
    private String name;
    private String measurement;
    private String mood;
    private String thoraco;
    private Date dateMeasurement;


    public TableRow(Date birthdate, String diagnosis, String hardware, String healthy, String height,
                    String lung, String notes, String sex, String software, String weight,
                    String objectID, String name, String measurement, String mood, String thoraco,
                    Date dateMeasurement) {
        this.birthdate = birthdate;
        this.diagnosis = diagnosis;
        this.hardware = hardware;
        this.healthy = healthy;
        this.height = height;
        this.lung = lung;
        this.notes = notes;
        this.sex = sex;
        this.software = software;
        this.weight = weight;
        this.objectID = objectID;
        this.name = name;
        this.measurement = measurement;
        this.mood = mood;
        this.thoraco = thoraco;
        this.dateMeasurement = dateMeasurement;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getHealthy() {
        return healthy;
    }

    public void setHealthy(String healthy) {
        this.healthy = healthy;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLung() {
        return lung;
    }

    public void setLung(String lung) {
        this.lung = lung;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getThoraco() {
        return thoraco;
    }

    public void setThoraco(String thoraco) {
        this.thoraco = thoraco;
    }

    public Date getDateMeasurement() {
        return dateMeasurement;
    }

    public void setDateMeasurement(Date dateMeasurement) {
        this.dateMeasurement = dateMeasurement;
    }
}
