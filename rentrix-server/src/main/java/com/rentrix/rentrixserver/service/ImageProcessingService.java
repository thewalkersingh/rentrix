package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.config.StorageProperties;
import com.rentrix.rentrixserver.exception.FileValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessingService {
	
	/** Max dimension (longest edge) after processing */
	private static final int MAX_DIMENSION = 1920;
	/** JPEG quality for output (0.85 keeps photos sharp but small) */
	private static final float JPEG_QUALITY = 0.85f;
	
	private final StorageProperties props;
	
	/**
	 * Validates and processes an uploaded image.
	 * Returns a sanitized byte array — EXIF stripped, resized if needed.
	 */
	public byte[] processImage(MultipartFile file) {
		validate(file);
		
		try {
			byte[] original = file.getBytes();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			// Thumbnailator strips EXIF when outputting — this is intentional.
			Thumbnails.of(new ByteArrayInputStream(original)).size(MAX_DIMENSION, MAX_DIMENSION)
						 .outputQuality(JPEG_QUALITY).outputFormat("jpg").toOutputStream(out);
			
			byte[] processed = out.toByteArray();
			log.info("Image processed: original={}B, processed={}B", original.length, processed.length);
			return processed;
			
		} catch (IOException e) {
			log.warn("Failed to process image: {}", e.getMessage());
			throw new FileValidationException("Failed to process image");
		}
	}
	
	/**
	 * Returns the actual image dimensions from an image stream.
	 * Used for validation without decoding the full image.
	 */
	public int[] readDimensions(byte[] bytes) throws IOException {
		try (ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (!readers.hasNext()) {
				throw new FileValidationException("Unsupported image format");
			}
			ImageReader reader = readers.next();
			reader.setInput(in);
			return new int[]{reader.getWidth(0), reader.getHeight(0)};
		}
	}
	
	private void validate(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new FileValidationException("File is empty");
		}
		if (file.getSize() > props.getMaxImageSizeBytes()) {
			throw new FileValidationException(
				"File too large (max " + (props.getMaxImageSizeBytes() / 1024 / 1024) + " MB)");
		}
		String contentType = file.getContentType();
		if (contentType == null || !props.getAllowedImageTypes().contains(contentType)) {
			throw new FileValidationException("Unsupported file type. Allowed: JPG, PNG, WebP");
		}
	}
	
}