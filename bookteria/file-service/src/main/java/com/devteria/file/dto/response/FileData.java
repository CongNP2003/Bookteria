package com.devteria.file.dto.response;

import org.springframework.core.io.Resource;

// loại này là kiểu mới , không thể set giá trị khác vào
public record FileData(String contenType, Resource resource) {
}
