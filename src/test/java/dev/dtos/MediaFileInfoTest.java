package dev.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaFileInfoTest {

    @Test
    void testMediaFileInfoProperties() {
        MediaFileInfo info = MediaFileInfo.builder()
                .name("song.mp3")
                .type("audio/mpeg")
                .sizeBytes(102400)
                .sizeFormatted("100 KB")
                .lastModified("2026-09-10")
                .isAudio(true)
                .isVideo(false)
                .build();

        assertEquals("song.mp3", info.getName());
        assertEquals("audio/mpeg", info.getType());
        assertEquals(102400, info.getSizeBytes());
        assertEquals("100 KB", info.getSizeFormatted());
        assertTrue(info.isAudio());
        assertFalse(info.isVideo());
    }
}
