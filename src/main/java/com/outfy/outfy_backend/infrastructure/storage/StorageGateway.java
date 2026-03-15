package com.outfy.outfy_backend.infrastructure.storage;

public interface StorageGateway {
    String upload(byte[] data, String fileName, String contentType);
    void delete(String fileUrl);
    String getUrl(String fileName);
}

