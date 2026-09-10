package dev.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaFileInfoTest {

    @Test
    void testMediaFileInfoRecord() {
        MediaFileInfo info = new MediaFileInfo("song.mp3", "100 KB");

        assertEquals("song.mp3", info.name());
        assertEquals("song.mp3", info.getName());
        assertEquals("100 KB", info.sizeFormatted());
        assertEquals("100 KB", info.getSizeFormatted());
    }
}
