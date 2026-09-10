package dev.dtos;

import java.io.Serializable;

public record MediaFileInfo(String name, String sizeFormatted) implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getName() {
        return name;
    }

    public String getSizeFormatted() {
        return sizeFormatted;
    }
}
