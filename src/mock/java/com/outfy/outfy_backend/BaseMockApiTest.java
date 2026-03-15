package com.outfy.outfy_backend.mock;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Base class for Mock API tests
 * Provides common MockMvc setup for testing APIs without database
 */
public abstract class BaseMockApiTest {

    protected MockMvc mockMvc;
    protected WebApplicationContext webApplicationContext;

    protected void setUp(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // Common test utilities
    protected String getAuthHeader(String token) {
        return "Bearer " + token;
    }

    protected String getMockToken() {
        return "mock_jwt_token_for_testing";
    }
}

