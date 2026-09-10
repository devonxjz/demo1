package dev.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String type;
    private long sizeBytes;
    private String sizeFormatted;
    private String lastModified;
    private boolean isVideo;
    private boolean isAudio;
}
