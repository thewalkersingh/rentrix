package com.rentrix.rentrixserver.integration;

import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.support.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FlatIntegrationTest extends BaseIntegrationTest {
	
	@Test
	void listFlats_returnsPaginatedResult() throws Exception {
		User landlord = testData.createLandlord();
		testData.createVerifiedFlat(landlord);
		testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(get("/flats")).andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
				 .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(2))).andExpect(jsonPath("$.number").value(0))
				 .andExpect(jsonPath("$.size").value(9));
	}
	
	@Test
	void getFlatById_returnsFlatWithNestedAddress() throws Exception {
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(get("/flats/{id}", flat.getId())).andExpect(status().isOk())
				 .andExpect(jsonPath("$.id").value(flat.getId())).andExpect(jsonPath("$.address.city").value("Pune"))
				 .andExpect(jsonPath("$.verified").value(true)).andExpect(jsonPath("$.visible").value(true));
	}
	
	@Test
	void getFlatById_whenNotFound_returns404() throws Exception {
		mockMvc.perform(get("/flats/{id}", 999999L)).andExpect(status().isNotFound());
	}
	
	@Test
	void listFlats_filterByCity_returnsOnlyMatching() throws Exception {
		User landlord = testData.createLandlord();
		testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(get("/flats").param("city", "Pune")).andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[0].city").value("Pune"));
	}
	
	@Test
	void listFlats_filterByPropertyType_works() throws Exception {
		User landlord = testData.createLandlord();
		testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(get("/flats").param("propertyType", "APARTMENT")).andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[0].propertyType").value("APARTMENT"));
	}
	
	@Test
	void listFlats_sortByRentDesc_works() throws Exception {
		User landlord = testData.createLandlord();
		testData.createVerifiedFlat(landlord);
		testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(get("/flats").param("sort", "rent,desc")).andExpect(status().isOk())
				 .andExpect(jsonPath("$.content").isArray());
	}
	
	@Test
	void listFlats_populatesRatingsFromApprovedReviews() throws Exception {
		User landlord = testData.createLandlord();
		User tenant1 = testData.createTenant("r1");
		User tenant2 = testData.createTenant("r2");
		Flat flat = testData.createVerifiedFlat(landlord);
		
		testData.createReviewWithRating(flat, tenant1, ReviewStatus.APPROVED, 8);
		testData.createReviewWithRating(flat, tenant2, ReviewStatus.APPROVED, 6);
		testData.createReviewWithRating(flat, testData.createTenant("r3"),
			ReviewStatus.PENDING, 10);
		
		mockMvc.perform(get("/flats").param("size", "200"))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[0].id").value(flat.getId()))
				 .andExpect(jsonPath("$.content[0].averageRating").value(7.0))
				 .andExpect(jsonPath("$.content[0].reviewCount").value(2));
	}
	
	@Test
	void getFlatById_populatesRatings() throws Exception {
		User landlord = testData.createLandlord();
		User tenant = testData.createTenant("r");
		Flat flat = testData.createVerifiedFlat(landlord);
		testData.createReviewWithRating(flat, tenant, ReviewStatus.APPROVED, 9);
		
		mockMvc.perform(get("/flats/{id}", flat.getId())).andExpect(status().isOk())
				 .andExpect(jsonPath("$.averageRating").value(9.0)).andExpect(jsonPath("$.reviewCount").value(1));
	}
	
	@Test
	void listFlats_noReviews_leavesRatingsNull() throws Exception {
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(get("/flats").param("size", "200"))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[0].id").value(flat.getId()))
				 .andExpect(jsonPath("$.content[0].averageRating").doesNotExist())
				 .andExpect(jsonPath("$.content[0].reviewCount").doesNotExist());
	}
	
}