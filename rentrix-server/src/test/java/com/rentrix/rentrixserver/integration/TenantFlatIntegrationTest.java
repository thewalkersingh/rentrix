package com.rentrix.rentrixserver.integration;

import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.support.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TenantFlatIntegrationTest extends BaseIntegrationTest {
	
	// ── Creation ────────────────────────────────────────────────────────────
	
	@Test
	void tenant_createsFlat_verifiedFalse_visibleFalse() throws Exception {
		User tenant = testData.createTenant();
		CreateFlatRequest req = testData.buildCreateFlatRequest();
		
		mockMvc.perform(post("/flats")
								 .header("Authorization", auth.bearer(tenant))
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(req)))
				 .andExpect(status().isCreated())
				 .andExpect(jsonPath("$.verified").value(false))
				 .andExpect(jsonPath("$.visible").value(false))
				 .andExpect(jsonPath("$.ownerId").doesNotExist())
				 .andExpect(jsonPath("$.createdByName").isNotEmpty());
	}
	
	@Test
	void landlord_createsFlat_verifiedTrue_visibleTrue() throws Exception {
		User landlord = testData.createLandlord();
		CreateFlatRequest req = testData.buildCreateFlatRequest();
		
		mockMvc.perform(post("/flats")
								 .header("Authorization", auth.bearer(landlord))
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(req)))
				 .andExpect(status().isCreated())
				 .andExpect(jsonPath("$.verified").value(true))
				 .andExpect(jsonPath("$.visible").value(true))
				 .andExpect(jsonPath("$.ownerId").isNumber());
	}
	
	@Test
	void unauthenticated_createsFlat_returns401() throws Exception {
		CreateFlatRequest req = testData.buildCreateFlatRequest();
		
		mockMvc.perform(post("/flats")
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(req)))
				 .andExpect(status().isUnauthorized());
	}
	
	// ── Visibility ──────────────────────────────────────────────────────────
	
	@Test
	void publicList_excludesHiddenFlat() throws Exception {
		User tenant = testData.createTenant();
		Flat hidden = testData.createTenantFlat(tenant);
		
		mockMvc.perform(get("/flats").param("size", "200"))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[*].id", not(hasItem(hidden.getId().intValue()))));
	}
	
	@Test
	void publicList_includesVerifiedFlat() throws Exception {
		User landlord = testData.createLandlord();
		Flat visible = testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(get("/flats").param("size", "200"))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[*].id", hasItem(visible.getId().intValue())));
	}
	
	// ── /flats/me ───────────────────────────────────────────────────────────
	
	@Test
	void myFlats_includesHiddenOwnFlat() throws Exception {
		User tenant = testData.createTenant();
		Flat hidden = testData.createTenantFlat(tenant);
		
		mockMvc.perform(get("/flats/me")
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[*].id", hasItem(hidden.getId().intValue())));
	}
	
	@Test
	void myFlats_withoutToken_returns401() throws Exception {
		mockMvc.perform(get("/flats/me"))
				 .andExpect(status().isUnauthorized());
	}
	
	// ── Ownership rules ─────────────────────────────────────────────────────
	
	@Test
	void tenant_canPatchOwnFlat() throws Exception {
		User tenant = testData.createTenant();
		Flat flat = testData.createTenantFlat(tenant);
		
		mockMvc.perform(patch("/flats/{id}", flat.getId())
								 .header("Authorization", auth.bearer(tenant))
								 .contentType(MediaType.APPLICATION_JSON)
								 .content("{\"rent\": 14000}"))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.rent").value(14000));
	}
	
	@Test
	void tenant_cannotPatchOthersFlat_returns403() throws Exception {
		User tenant1 = testData.createTenant("a");
		User tenant2 = testData.createTenant("b");
		Flat flat = testData.createTenantFlat(tenant2);
		
		mockMvc.perform(patch("/flats/{id}", flat.getId())
								 .header("Authorization", auth.bearer(tenant1))
								 .contentType(MediaType.APPLICATION_JSON)
								 .content("{\"rent\": 1}"))
				 .andExpect(status().isForbidden());
	}
	
	@Test
	void tenant_cannotDeleteOthersFlat_returns403() throws Exception {
		User tenant1 = testData.createTenant("a");
		User tenant2 = testData.createTenant("b");
		Flat flat = testData.createTenantFlat(tenant2);
		
		mockMvc.perform(delete("/flats/{id}", flat.getId())
								 .header("Authorization", auth.bearer(tenant1)))
				 .andExpect(status().isForbidden());
	}
	
	@Test
	void admin_canDeleteAnyFlat() throws Exception {
		User landlord = testData.createLandlord();
		User admin = testData.createAdmin();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(delete("/flats/{id}", flat.getId())
								 .header("Authorization", auth.bearer(admin)))
				 .andExpect(status().isNoContent());
	}
	
}