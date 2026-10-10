package com.rentrix.rentrixserver.integration;

import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.Review;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.support.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminIntegrationTest extends BaseIntegrationTest {
	
	@Test
	void approveFirstReview_promotesHiddenFlatToVisible() throws Exception {
		User tenant = testData.createTenant();
		User admin = testData.createAdmin();
		Flat hidden = testData.createTenantFlat(tenant);
		Review review = testData.createReview(hidden, tenant, ReviewStatus.PENDING);
		
		// Sanity: flat is hidden
		mockMvc.perform(get("/flats").param("size", "200"))
				 .andExpect(jsonPath("$.content[*].id",
					 not(hasItem(hidden.getId().intValue()))));
		
		// Approve
		mockMvc.perform(patch("/admin/reviews/{id}", review.getId())
								 .header("Authorization", auth.bearer(admin))
								 .contentType(MediaType.APPLICATION_JSON)
								 .content("{\"status\":\"APPROVED\"}"))
				 .andExpect(status().isOk());
		
		// Flat is now visible
		mockMvc.perform(get("/flats").param("size", "200"))
				 .andExpect(jsonPath("$.content[*].id",
					 hasItem(hidden.getId().intValue())));
	}
	
	@Test
	void admin_canVerifyFlat() throws Exception {
		User tenant = testData.createTenant();
		User admin = testData.createAdmin();
		Flat flat = testData.createTenantFlat(tenant);
		
		mockMvc.perform(post("/admin/flats/{id}/verify", flat.getId())
								 .header("Authorization", auth.bearer(admin)))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.verified").value(true));
	}
	
	@Test
	void admin_canHideFlat() throws Exception {
		User landlord = testData.createLandlord();
		User admin = testData.createAdmin();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(patch("/admin/flats/{id}/visible", flat.getId())
								 .header("Authorization", auth.bearer(admin)))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.visible").value(false));
	}
	
	@Test
	void nonAdmin_cannotAccessAdminEndpoints() throws Exception {
		User tenant = testData.createTenant();
		
		mockMvc.perform(get("/admin/reviews")
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isForbidden());
	}
	
	@Test
	void unauthenticated_cannotAccessAdminEndpoints() throws Exception {
		mockMvc.perform(get("/admin/reviews"))
				 .andExpect(status().isUnauthorized());
	}
	
}