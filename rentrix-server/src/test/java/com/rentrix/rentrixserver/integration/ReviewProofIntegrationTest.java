package com.rentrix.rentrixserver.integration;

import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.Review;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.repository.ReviewProofRepository;
import com.rentrix.rentrixserver.repository.ReviewRepository;
import com.rentrix.rentrixserver.service.StorageService;
import com.rentrix.rentrixserver.support.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewProofIntegrationTest extends BaseIntegrationTest {
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private ReviewProofRepository reviewProofRepository;
	
	@MockitoBean
	private StorageService storageService;
	
	private static final byte[] FAKE_PDF = new byte[]{
		0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x34  // %PDF-1.4
	};
	
	private MockMultipartFile pdfFile(String name) {
		return new MockMultipartFile("file", name, "application/pdf", FAKE_PDF);
	}
	
	// ── Upload ────────────────────────────────────────────────────────────
	
	@Test
	void uploadProof_asOwner_returns201() throws Exception {
		when(storageService.uploadPrivate(anyString(), anyString(), any(), anyString(), anyString()))
			.thenReturn("reviews/1/test.pdf");
		
		User tenant = testData.createTenant();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
								 .file(pdfFile("agreement.pdf"))
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isCreated())
				 .andExpect(jsonPath("$.id").isNumber())
				 .andExpect(jsonPath("$.originalFilename").value("agreement.pdf"))
				 .andExpect(jsonPath("$.contentType").value("application/pdf"))
				 .andExpect(jsonPath("$.verified").value(false));
		
		assert reviewProofRepository.countByReviewIdAndDeletedFalse(review.getId()) == 1;
	}
	
	@Test
	void uploadProof_asNonOwner_returns403() throws Exception {
		User owner = testData.createTenant("owner");
		User stranger = testData.createTenant("stranger");
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, owner, ReviewStatus.PENDING);
		
		mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
								 .file(pdfFile("a.pdf"))
								 .header("Authorization", auth.bearer(stranger)))
				 .andExpect(status().isForbidden());
	}
	
	@Test
	void uploadProof_withoutAuth_returns401() throws Exception {
		User tenant = testData.createTenant();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
								 .file(pdfFile("a.pdf")))
				 .andExpect(status().isUnauthorized());
	}
	
	@Test
	void uploadProof_wrongMimeType_returns400() throws Exception {
		User tenant = testData.createTenant();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		MockMultipartFile txt = new MockMultipartFile(
			"file", "notes.txt", "text/plain", "hello".getBytes());
		
		mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
								 .file(txt)
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isBadRequest());
	}
	
	@Test
	void uploadProof_exceedsMax_returns400() throws Exception {
		when(storageService.uploadPrivate(anyString(), anyString(), any(), anyString(), anyString()))
			.thenReturn("reviews/1/test.pdf");
		
		User tenant = testData.createTenant();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		// Upload 3 proofs (max)
		for (int i = 0; i < 3; i++) {
			mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
									 .file(pdfFile("proof" + i + ".pdf"))
									 .header("Authorization", auth.bearer(tenant)))
					 .andExpect(status().isCreated());
		}
		
		// 4th fails
		mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
								 .file(pdfFile("extra.pdf"))
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isBadRequest())
				 .andExpect(jsonPath("$.message", containsString("Maximum")));
	}
	
	@Test
	void uploadProof_afterApproval_returns400() throws Exception {
		User tenant = testData.createTenant();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.APPROVED);
		
		mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
								 .file(pdfFile("a.pdf"))
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isBadRequest())
				 .andExpect(jsonPath("$.message", containsString("moderation")));
	}
	
	// ── List ──────────────────────────────────────────────────────────────
	
	@Test
	void listProofs_asOwner_returnsMetadata() throws Exception {
		when(storageService.uploadPrivate(anyString(), anyString(), any(), anyString(), anyString()))
			.thenReturn("reviews/1/test.pdf");
		
		User tenant = testData.createTenant();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
								 .file(pdfFile("a.pdf"))
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isCreated());
		
		mockMvc.perform(get("/reviews/{id}/proofs", review.getId())
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$", hasSize(1)))
				 .andExpect(jsonPath("$[0].originalFilename").value("a.pdf"));
	}
	
	@Test
	void listProofs_asNonOwner_returns403() throws Exception {
		User owner = testData.createTenant("owner");
		User stranger = testData.createTenant("stranger");
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, owner, ReviewStatus.PENDING);
		
		mockMvc.perform(get("/reviews/{id}/proofs", review.getId())
								 .header("Authorization", auth.bearer(stranger)))
				 .andExpect(status().isForbidden());
	}
	
	// ── URL ───────────────────────────────────────────────────────────────
	
	@Test
	void getProofUrl_asOwner_returnsSignedUrl() throws Exception {
		when(storageService.uploadPrivate(anyString(), anyString(), any(), anyString(), anyString()))
			.thenReturn("reviews/1/test.pdf");
		when(storageService.presignedPrivateUrl(anyString(), anyInt()))
			.thenReturn("https://rentrix-private.s3.ap-south-1.amazonaws.com/signed");
		
		User tenant = testData.createTenant();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		String response = mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
														 .file(pdfFile("a.pdf"))
														 .header("Authorization", auth.bearer(tenant)))
										 .andReturn().getResponse().getContentAsString();
		
		Long proofId = Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));
		
		mockMvc.perform(get("/reviews/{id}/proofs/{proofId}/url", review.getId(), proofId)
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.url").value(
					 "https://rentrix-private.s3.ap-south-1.amazonaws.com/signed"));
	}
	
	@Test
	void getProofUrl_asAdmin_returns200() throws Exception {
		when(storageService.uploadPrivate(anyString(), anyString(), any(), anyString(), anyString()))
			.thenReturn("reviews/1/test.pdf");
		when(storageService.presignedPrivateUrl(anyString(), anyInt()))
			.thenReturn("https://example.com/signed");
		
		User tenant = testData.createTenant();
		User admin = testData.createAdmin();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		String response = mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
														 .file(pdfFile("a.pdf"))
														 .header("Authorization", auth.bearer(tenant)))
										 .andReturn().getResponse().getContentAsString();
		
		Long proofId = Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));
		
		mockMvc.perform(get("/reviews/{id}/proofs/{proofId}/url", review.getId(), proofId)
								 .header("Authorization", auth.bearer(admin)))
				 .andExpect(status().isOk());
	}
	
	@Test
	void getProofUrl_asNonOwner_returns403() throws Exception {
		when(storageService.uploadPrivate(anyString(), anyString(), any(), anyString(), anyString()))
			.thenReturn("reviews/1/test.pdf");
		
		User owner = testData.createTenant("owner");
		User stranger = testData.createTenant("stranger");
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, owner, ReviewStatus.PENDING);
		
		String response = mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
														 .file(pdfFile("a.pdf"))
														 .header("Authorization", auth.bearer(owner)))
										 .andReturn().getResponse().getContentAsString();
		
		Long proofId = Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));
		
		mockMvc.perform(get("/reviews/{id}/proofs/{proofId}/url", review.getId(), proofId)
								 .header("Authorization", auth.bearer(stranger)))
				 .andExpect(status().isForbidden());
	}
	
	// ── Delete ────────────────────────────────────────────────────────────
	
	@Test
	void deleteProof_asOwner_returns204() throws Exception {
		when(storageService.uploadPrivate(anyString(), anyString(), any(), anyString(), anyString()))
			.thenReturn("reviews/1/test.pdf");
		
		User tenant = testData.createTenant();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		String response = mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
														 .file(pdfFile("a.pdf"))
														 .header("Authorization", auth.bearer(tenant)))
										 .andReturn().getResponse().getContentAsString();
		
		Long proofId = Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));
		
		mockMvc.perform(delete("/reviews/{id}/proofs/{proofId}", review.getId(), proofId)
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isNoContent());
		
		verify(storageService, times(1)).deletePrivate("reviews/1/test.pdf");
		assert reviewProofRepository.findByIdAndDeletedFalse(proofId).isEmpty();
	}
	
	// ── Verified stay badge ───────────────────────────────────────────────
	
	@Test
	void approveReview_marksAllProofsVerifiedAndBadgeTrue() throws Exception {
		when(storageService.uploadPrivate(anyString(), anyString(), any(), anyString(), anyString()))
			.thenReturn("reviews/1/test.pdf");
		
		User tenant = testData.createTenant();
		User admin = testData.createAdmin();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		// Upload 2 proofs
		mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
								 .file(pdfFile("a.pdf"))
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isCreated());
		mockMvc.perform(multipart("/reviews/{id}/proofs", review.getId())
								 .file(pdfFile("b.pdf"))
								 .header("Authorization", auth.bearer(tenant)))
				 .andExpect(status().isCreated());
		
		// Approve
		mockMvc.perform(patch("/admin/reviews/{id}", review.getId())
								 .header("Authorization", auth.bearer(admin))
								 .contentType("application/json")
								 .content("{\"status\":\"APPROVED\"}"))
				 .andExpect(status().isOk());
		
		// Public review shows verifiedStay=true
		mockMvc.perform(get("/flats/{id}/reviews", flat.getId()))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[0].hasProof").value(true))
				 .andExpect(jsonPath("$.content[0].verifiedStay").value(true));
	}
	
	@Test
	void approvedReviewWithoutProof_verifiedStayIsFalse() throws Exception {
		User tenant = testData.createTenant();
		User admin = testData.createAdmin();
		Flat flat = testData.createVerifiedFlat(testData.createLandlord());
		Review review = testData.createReview(flat, tenant, ReviewStatus.PENDING);
		
		mockMvc.perform(patch("/admin/reviews/{id}", review.getId())
								 .header("Authorization", auth.bearer(admin))
								 .contentType("application/json")
								 .content("{\"status\":\"APPROVED\"}"))
				 .andExpect(status().isOk());
		
		mockMvc.perform(get("/flats/{id}/reviews", flat.getId()))
				 .andExpect(status().isOk())
				 .andExpect(jsonPath("$.content[0].hasProof").value(false))
				 .andExpect(jsonPath("$.content[0].verifiedStay").value(false));
	}
	
}