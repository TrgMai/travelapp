package com.example.travelapp.model;

public class Itinerary {
    private String id;
    private String tourId;
    private Integer dayNo;
    private String title;
    private String place;
    private String activity;
    private String note;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTourId() { return tourId; }
    public void setTourId(String tourId) { this.tourId = tourId; }
    public Integer getDayNo() { return dayNo; }
    public void setDayNo(Integer dayNo) { this.dayNo = dayNo; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }
    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
