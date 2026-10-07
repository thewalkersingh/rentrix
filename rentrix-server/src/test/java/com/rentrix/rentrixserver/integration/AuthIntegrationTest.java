package com.rentrix.rentrixserver.integration;

import com.rentrix.rentrixserver.dto.LoginRequest;
import com.rentrix.rentrixserver.dto.SignupRequest;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.entity.constants.Role;
import com.rentrix.rentrixserver.support.BaseIntegrationTest;
import com.rentrix.rentrixserver.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends BaseIntegrationTest {
	
	@Test
	void signup_asTenant_returns201WithTokens() throws Exception {
		SignupRequest req = new SignupRequest();
		req.setName("New Tenant");
		req.setEmail("new-tenant@rentrix.test");
		req.setPassword("password123");
		req.setRole(Role.TENANT);
		
		mockMvc.perform(post("/auth/signup")
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(req)))
				 .andExpect(status().isCreated())
				 .andExpect(jsonPath("$.user.email").value("new-tenant@rentrix.test"))
				 .andExpect(jsonPath("$.user.role").value("TENANT"))
				 .andExpect(jsonPath("$.accessToken").isNotEmpty())
				 .andExpect(jsonPath("$.refreshToken").isNotEmpty());
	}
	
	@Test
	void signup_withAdminRole_returns400() throws Exception {
		SignupRequest req = new SignupRequest();
		req.setName("Hacker");
		req.setEmail("hacker@rentrix.test");
		req.setPassword("password123");
		req.setRole(Role.ADMIN);
		
		mockMvc.perform(post("/auth/signup")
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(req)))
				 .andExpect(status().isBadRequest())
				 .andExpect(jsonPath("$.message", containsString("Cannot self-register")));
	}
	
	@Test
	void signup_withDuplicateEmail_returns409() throws Exception {
		testData.createTenant();
		
		SignupRequest req = new SignupRequest();
		req.setName("Dup");
		req.setEmail(testData.createTenant().getEmail()); // reuse same email
		req.setPassword("password123");
		req.setRole(Role.TENANT);
		
		// NOTE: createTenant generates a unique email per call, so this needs
		// to be done differently. Use a direct approach:
		User existing = testData.createTenant();
		req.setEmail(existing.getEmail());
		
		mockMvc.perform(post("/auth/signup")
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(req)))
				 .andExpect(status().isConflict());
	}
	
	@Test
	void login_withValidCredentials_returns200() throws Exception {
		User user = testData.createTenant();
		
		LoginRequest req = new LoginRequest();
		req.setEmail(user.getEmail());
		req.setPassword(TestDataFactory.DEFAULT_PASSWORD);
		
		mockMvc.perform(post("/auth/login")
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(req)))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.accessToken").isNotEmpty())
				 .andExpect(jsonPath("$.user.role").value("TENANT"));
	}
	
	@Test
	void login_withWrongPassword_returns401() throws Exception {
		User user = testData.createTenant();
		
		LoginRequest req = new LoginRequest();
		req.setEmail(user.getEmail());
		req.setPassword("wrong-password");
		
		mockMvc.perform(post("/auth/login")
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(req)))
				 .andExpect(status().isUnauthorized());
	}
	
	@Test
	void me_withValidToken_returns200() throws Exception {
		User tenant = testData.createTenant();
		
		mockMvc.perform(get("/auth/me")
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.email").value(tenant.getEmail()));
	}
	
	@Test
	void me_withoutToken_returns401() throws Exception {
		mockMvc.perform(get("/auth/me"))
				 .andExpect(status().isUnauthorized());
	}
	
	@Test
	void me_withBadToken_returns401() throws Exception {
		mockMvc.perform(get("/auth/me")
								 .header("Authorization", "Bearer garbage"))
				 .andExpect(status().isUnauthorized())
				 .andExpect(jsonPath("$.message").value("Invalid or expired token"));
	}
	
}