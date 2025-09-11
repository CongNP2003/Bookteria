package com.devteria.file.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FileInfo {
    String name;
    String contenType;
    long size;
    String md5Checksum;
    String path;
    String url;
}
