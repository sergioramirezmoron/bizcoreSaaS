package com.bizcore.catalog.domain.port.out;

import java.io.InputStream;

public interface StoragePort {
    String upload(String key, String contentType, long sizeBytes, InputStream content);
    void delete(String key);
    String buildPublicUrl(String key);
}
