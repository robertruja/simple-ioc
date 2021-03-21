package org.crumbs.core.logging;

public enum Level {
    DEBUG(0), INFO(1), WARN(2), ERROR(3);

    int ordinal;

    Level(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return this.ordinal;
    }
}
