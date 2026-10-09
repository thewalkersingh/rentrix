package com.rentrix.rentrixserver.integration;

import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.repository.FlatImageRepository;
import com.rentrix.rentrixserver.service.StorageService;
import com.rentrix.rentrixserver.support.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FlatImageIntegrationTest extends BaseIntegrationTest {
	
	@Autowired
	private FlatImageRepository flatImageRepository;
	
	@MockitoBean
	private StorageService storageService;
	
	/** Minimal valid 1×1 JPEG (62 bytes). Thumbnailator will re-encode it. */
	private static final byte[] MINIMAL_JPEG =
		new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
			0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x00, 0x08, 0x06, 0x06,
			0x07, 0x06, 0x05, 0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D, 0x0C, 0x0B, 0x0B, 0x0C,
			0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F, 0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20,
			0x22, 0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30, 0x31, 0x34, 0x34, 0x34, 0x1F, 0x27, 0x39, 0x3D,
			0x38, 0x32, 0x3C, 0x2E, 0x33, 0x34, 0x32, (byte) 0xFF, (byte) 0xC0, 0x00, 0x0B, 0x08, 0x00, 0x01, 0x00, 0x01,
			0x01, 0x01, 0x11, 0x00, (byte) 0xFF, (byte) 0xC4, 0x00, 0x14, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, (byte) 0xFF, (byte) 0xC4, 0x00, 0x14, 0x10,
			0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			(byte) 0xFF, (byte) 0xDA, 0x00, 0x08, 0x01, 0x01, 0x00, 0x00, 0x3F, 0x00, 0x37, (byte) 0xFF, (byte) 0xD9};
	
	private MockMultipartFile jpegFile() {
		return new MockMultipartFile("file", "test.jpg", "image/jpeg", MINIMAL_JPEG);
	}
	
	// ── Upload ────────────────────────────────────────────────────────────
	
	@Test
	void upload_asOwner_returns201AndPersists() throws Exception {
		when(storageService.uploadPublicImage(anyString(), anyString(), any(), anyString())).thenReturn(
			"https://rentrix-media.s3.ap-south-1.amazonaws.com/flats/1/test.jpg");
		
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(
					 multipart("/flats/{id}/images", flat.getId()).file(jpegFile()).header("Authorization",
						 auth.bearer(landlord)))
				 .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
				 .andExpect(jsonPath("$.url").isNotEmpty()).andExpect(jsonPath("$.isPrimary").value(true))
				 .andExpect(jsonPath("$.displayOrder").value(0));
		
		long count = flatImageRepository.countByFlatIdAndDeletedFalse(flat.getId());
		assert count == 1;
	}
	
	@Test
	void upload_withoutAuth_returns401() throws Exception {
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(multipart("/flats/{id}/images", flat.getId()).file(jpegFile()))
				 .andExpect(status().isUnauthorized());
	}
	
	@Test
	void upload_asNonOwner_returns403() throws Exception {
		User landlord = testData.createLandlord();
		User stranger = testData.createTenant("stranger");
		Flat flat = testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(
					 multipart("/flats/{id}/images", flat.getId()).file(jpegFile()).header("Authorization",
						 auth.bearer(stranger)))
				 .andExpect(status().isForbidden());
	}
	
	@Test
	void upload_wrongMimeType_returns400() throws Exception {
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		MockMultipartFile txt = new MockMultipartFile("file", "notes.txt", "text/plain", "hello".getBytes());
		
		mockMvc.perform(
					 multipart("/flats/{id}/images", flat.getId()).file(txt).header("Authorization", auth.bearer(landlord)))
				 .andExpect(status().isBadRequest());
	}
	
	@Test
	void upload_oversizedFile_returns400() throws Exception {
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		byte[] big = new byte[11 * 1024 * 1024];
		MockMultipartFile file = new MockMultipartFile("file", "big.jpg", "image/jpeg", big);
		
		mockMvc.perform(
					 multipart("/flats/{id}/images", flat.getId()).file(file).header("Authorization",
						 auth.bearer(landlord)))
				 .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message", containsString("too large")));
	}
	
	@Test
	void upload_exceedsMaxImages_returns400() throws Exception {
		when(storageService.uploadPublicImage(anyString(), anyString(), any(), anyString())).thenReturn(
			"https://example.com/img.jpg");
		
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		// Upload 10 images
		for (int i = 0; i < 10; i++) {
			mockMvc.perform(multipart("/flats/{id}/images", flat.getId()).file(jpegFile())
																							 .header("Authorization", auth.bearer(landlord)))
					 .andExpect(status().isCreated());
		}
		
		// 11th should fail
		mockMvc.perform(
					 multipart("/flats/{id}/images", flat.getId()).file(jpegFile()).header("Authorization",
						 auth.bearer(landlord)))
				 .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message", containsString("Maximum")));
	}
	
	// ── List ──────────────────────────────────────────────────────────────
	
	@Test
	void listImages_isPublic() throws Exception {
		when(storageService.uploadPublicImage(anyString(), anyString(), any(), anyString())).thenReturn(
			"https://example.com/img.jpg");
		
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		mockMvc.perform(
					 multipart("/flats/{id}/images", flat.getId()).file(jpegFile()).header("Authorization",
						 auth.bearer(landlord)))
				 .andExpect(status().isCreated());
		
		// Public access — no auth header
		mockMvc.perform(get("/flats/{id}/images", flat.getId())).andExpect(status().isOk())
				 .andExpect(jsonPath("$", hasSize(1)));
	}
	
	// ── Delete ────────────────────────────────────────────────────────────
	
	@Test
	void deleteImage_byOwner_returns204() throws Exception {
		when(storageService.uploadPublicImage(anyString(), anyString(), any(), anyString())).thenReturn(
			"https://example.com/img.jpg");
		
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		String response = mockMvc.perform(
											 multipart("/flats/{id}/images", flat.getId()).file(jpegFile()).header("Authorization"
												 , auth.bearer(landlord)))
										 .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		
		Long imageId = Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));
		
		mockMvc.perform(
					 delete("/flats/{id}/images/{imageId}", flat.getId(), imageId).header("Authorization",
						 auth.bearer(landlord)))
				 .andExpect(status().isNoContent());
		
		assert flatImageRepository.findByIdAndDeletedFalse(imageId).isEmpty();
	}
	
	// ── Set primary ───────────────────────────────────────────────────────
	
	@Test
	void setPrimary_togglesCorrectly() throws Exception {
		when(storageService.uploadPublicImage(anyString(), anyString(), any(), anyString())).thenReturn(
			"https://example.com/img.jpg");
		
		User landlord = testData.createLandlord();
		Flat flat = testData.createVerifiedFlat(landlord);
		
		// Upload two images
		String r1 = mockMvc.perform(
									 multipart("/flats/{id}/images", flat.getId()).file(jpegFile()).header("Authorization",
										 auth.bearer(landlord)))
								 .andReturn().getResponse().getContentAsString();
		Long id1 = Long.parseLong(r1.replaceAll(".*\"id\":(\\d+).*", "$1"));
		
		String r2 = mockMvc.perform(
									 multipart("/flats/{id}/images", flat.getId()).file(jpegFile()).header("Authorization",
										 auth.bearer(landlord)))
								 .andReturn().getResponse().getContentAsString();
		Long id2 = Long.parseLong(r2.replaceAll(".*\"id\":(\\d+).*", "$1"));
		
		// id1 is primary (first upload)
		// Now set id2 as primary
		mockMvc.perform(patch("/flats/{id}/images/{imageId}/primary", flat.getId(), id2).header("Authorization",
					 auth.bearer(landlord))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id2))
				 .andExpect(jsonPath("$.isPrimary").value(true));
		
		// Verify only one primary remains
		var currentPrimary = flatImageRepository.findByFlatIdAndIsPrimaryTrueAndDeletedFalse(flat.getId());
		assert currentPrimary.isPresent() && currentPrimary.get().getId().equals(id2);
	}
	
}