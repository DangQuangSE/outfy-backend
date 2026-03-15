package com.outfy.outfy_backend.infrastructure.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockStorageGateway implements StorageGateway {
    
    private static final Logger logger = LoggerFactory.getLogger(MockStorageGateway.class);
    private static final String MOCK_BASE_URL = "/mock/storage/";

    @Override
    public String upload(byte[] data, String fileName, String contentType) {
        logger.info("Mock upload: fileName={}, size={}", fileName, data.length);
        return MOCK_BASE_URL + fileName;
    }

    @Override
    public void delete(String fileUrl) {
        logger.info("Mock delete: fileUrl={}", fileUrl);
    }

    @Override
    public String getUrl(String fileName) {
        return MOCK_BASE_URL + fileName;
    }
}

