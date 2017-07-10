package org.paasfinder.updater.models;

public class Author {
    private final String name;
    private final String email;

    public Author(String name, String email){
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
