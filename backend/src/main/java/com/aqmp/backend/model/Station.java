package com.aqmp.backend.model;

public class Station {

    private final Long id;
    private String name;
    private String location;

    public Station(Long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
