package com.rentrix.rentrixserver.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(
	scripts = "/cleanup.sql",
	executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public abstract class BaseIntegrationTest {
	
	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	protected TestDataFactory testData;
	
	@Autowired
	protected AuthTestHelper auth;
	
}