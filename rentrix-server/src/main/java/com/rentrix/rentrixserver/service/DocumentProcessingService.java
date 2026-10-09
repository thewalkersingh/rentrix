package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.exception.FileValidationException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

@Slf4j
@Service
public class DocumentProcessingService {
	
	private static final long MAX_SIZE_BYTES = 20L * 1024 * 1024;    // 20 MB
	private static final Set<String> ALLOWED_TYPES = Set.of(
		"application/pdf",
		"image/jpeg",
		"image/png",
		"image/webp"
	);
	
	/** Result of document processing — bytes + final content type. */
	public record ProcessedDocument(byte[] bytes, String contentType) {
	
	}
	
	public ProcessedDocument process(MultipartFile file) {
		validate(file);
		
		String contentType = file.getContentType();
		
		try {
			// PDFs pass through unchanged
			if ("application/pdf".equals(contentType)) {
				return new ProcessedDocument(file.getBytes(), "application/pdf");
			}
			
			// Images: re-encode through Thumbnailator to strip EXIF
			// Max dimension 2400px — higher than flat photos since documents need legibility
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Thumbnails.of(new ByteArrayInputStream(file.getBytes()))
						 .size(2400, 2400)
						 .outputQuality(0.9f)
						 .outputFormat("jpg")
						 .toOutputStream(out);
			
			byte[] processed = out.toByteArray();
			log.info("Document (image) processed: original={}B, processed={}B",
				file.getSize(), processed.length);
			return new ProcessedDocument(processed, "image/jpeg");
			
		} catch (IOException e) {
			log.warn("Failed to process document: {}", e.getMessage());
			throw new FileValidationException("Failed to process document");
		}
	}
	
	private void validate(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new FileValidationException("File is empty");
		}
		if (file.getSize() > MAX_SIZE_BYTES) {
			throw new FileValidationException(
				"File too large (max " + (MAX_SIZE_BYTES / 1024 / 1024) + " MB)");
		}
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
			throw new FileValidationException(
				"Unsupported file type. Allowed: PDF, JPG, PNG, WebP");
		}
	}
	
}