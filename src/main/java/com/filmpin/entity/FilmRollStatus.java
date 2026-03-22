package com.filmpin.entity;

public enum FilmRollStatus {
    UNEXPOSED("Unexposed"),
    IN_USE("In Use"),
    DEVELOPED("Developed");

    private final String displayName;

    FilmRollStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
