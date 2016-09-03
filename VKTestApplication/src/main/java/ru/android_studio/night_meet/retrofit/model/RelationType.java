package ru.android_studio.night_meet.retrofit.model;

/**
 * Created by andre on 03.09.2016.
 */
public enum RelationType {
    LIKE(1),
    CONNECT(2),
    VIEWED(3);

    private final int id;
    RelationType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
