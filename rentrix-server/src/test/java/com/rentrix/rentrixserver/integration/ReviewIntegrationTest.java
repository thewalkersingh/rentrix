package com.rentrix.rentrixserver.integration;

import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.support.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewIntegrationTest extends BaseIntegrationTest {
	
	@Test
	void createReview_statusPending() throws Exception {
		User tenant = testData.createTenant();
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		String body = """
			{"title":"My review","content":"This is a valid review body.","rating":8}
			""";
		
		mockMvc.perform(post("/flats/{id}/reviews",
					 flat.getId()).header("Authorization",
										  auth.bearer(tenant))
									  .contentType(MediaType.APPLICATION_JSON).content(body))
				 .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDING"));
	}
	
	@Test
	void createReview_withoutAuth_returns401() throws Exception {
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		String body = """
			{"title":"Anon review","content":"Trying anonymously.","rating":5}
			""";
		
		mockMvc.perform(post("/flats/{id}/reviews",
					 flat.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
				 .andExpect(status().isUnauthorized());
	}
	
	@Test
	void publicReviews_excludePending() throws Exception {
		User landlord = testData.createLandlord();
		User tenantA = testData.createTenant("a");
		User tenantB = testData.createTenant("b");
		Flat flat = testData.createVerifiedFlat(landlord);
		
		testData.createReview(flat, tenantA, ReviewStatus.PENDING);
		testData.createReview(flat, tenantB, ReviewStatus.APPROVED);
		
		mockMvc.perform(get("/flats/{id}/reviews", flat.getId()))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[*].status",
					 everyItem(equalTo("APPROVED"))));
	}
	
	@Test
	void myReviews_returnsAllStatuses() throws Exception {
		User tenant = testData.createTenant();
		Flat flat1 = testData.createVerifiedFlat(testData.createLandlord("a"));
		Flat flat2 = testData.createVerifiedFlat(testData.createLandlord("b"));
		
		testData.createReview(flat1, tenant, ReviewStatus.PENDING);
		testData.createReview(flat2, tenant, ReviewStatus.APPROVED);
		
		mockMvc.perform(get("/users/me/reviews")
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.content", hasSize(2)));
	}
	
	@Test
	void admin_rejectsPendingReview() throws Exception {
		User tenant = testData.createTenant();
		User admin = testData.createAdmin();
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		var review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		mockMvc.perform(patch("/admin/reviews/{id}", review.getId())
								 .header("Authorization", auth.bearer(admin))
								 .contentType(MediaType.APPLICATION_JSON)
								 .content("{\"status\":\"REJECTED\"}"))
				 .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("REJECTED"));
	}
	
}