package com.mvopo.floatingsms.Model;

/**
 * Created by mvopo on 5/8/2018.
 */

public class Message {
    private String address, body, type, person, read;

    public Message(){}

    public Message(String address, String body, String type, String person, String read) {
        this.address = address;
        this.body = body;
        this.type = type;
        this.person = person;
        this.read = read;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}
