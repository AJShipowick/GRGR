package com.airborne.mobileminutemen;

public class RepDetailInfo {

    public String id;
    public String state;
    public String party;
    public String title;
    public String firstName;
    public String lastName;
    public boolean isUserRepresentative;
    public String address;
    public String phone;
    public String website;
    public String twitter;
    public String youTube;
    public String email;

    public RepDetailInfo(String id, String state, String party, String title, String firstName, String lastName, boolean isUserRepresentative, String address, String phone, String website, String twitter, String youTube, String email){
        super();
        this.id = id;
        this.state = state;
        this.party = party;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isUserRepresentative = isUserRepresentative;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.twitter = twitter;
        this.youTube = youTube;
        this.email = email;

    }
}
