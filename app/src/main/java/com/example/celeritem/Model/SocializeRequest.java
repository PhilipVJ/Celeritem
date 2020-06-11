package com.example.celeritem.Model;

public class SocializeRequest {
    private String name;
    private int age;
    private Gender gender;
    private int phoneNumber;
    private String id;
    private Exercise wantsTo;
    private String city;

    public SocializeRequest(String name, int age, Gender gender, int phoneNumber, Exercise wantsTo, String city) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.wantsTo = wantsTo;
        this.city = city;
    }

    public String getCity(){
        return city;
    }

    public void addId(String id) {
        this.id = id;
    }

    public Exercise getWantsTo() {
        return wantsTo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Gender getGender() {
        return gender;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }
}
