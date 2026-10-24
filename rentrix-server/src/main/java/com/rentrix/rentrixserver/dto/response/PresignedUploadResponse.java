package com.rentrix.rentrixserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUploadResponse {
	
	/** The URL to POST the file to (S3). */
	private String uploadUrl;
	
	/** The S3 key the object will have. Client sends this back at confirm. */
	private String key;
	
	/** Public URL (for images) — null for private uploads (proofs). */
	private String publicUrl;
	
	/** When the presigned URL expires (ISO-8601). */
	private String expiresAt;
	
}