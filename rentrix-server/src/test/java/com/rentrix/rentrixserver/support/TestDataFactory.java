package com.rentrix.rentrixserver.support;

import com.rentrix.rentrixserver.dto.common.AddressDto;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.entity.Address;
import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.Review;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.entity.constants.PropertyType;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.entity.constants.Role;
import com.rentrix.rentrixserver.repository.FlatRepository;
import com.rentrix.rentrixserver.repository.ReviewRepository;
import com.rentrix.rentrixserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Creates test entities with sane defaults. Every method hits the DB directly,
 * bypassing the HTTP layer.
 */
@Component
public class TestDataFactory {
	
	public static final String DEFAULT_PASSWORD = "password123";
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FlatRepository flatRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// ── Users ────────────────────────────────────────────────────────────────
	public User createTenant() {
		return createUser("tenant", Role.TENANT);
	}
	
	public User createTenant(String suffix) {
		return createUser("tenant-" + suffix, Role.TENANT);
	}
	
	public User createLandlord() {
		return createUser("landlord", Role.LANDLORD);
	}
	
	public User createLandlord(String suffix) {
		return createUser("landlord-" + suffix, Role.LANDLORD);
	}
	
	public User createAdmin() {
		return createUser("admin", Role.ADMIN);
	}
	
	public User createUser(String prefix, Role role) {
		User user = new User();
		user.setEmail(prefix + "-" + System.nanoTime() + "@rentrix.test");
		user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
		user.setRole(role);
		User saved = userRepository.save(user);
		userRepository.flush();  // ✅ force INSERT
		return saved;
	}
	
	// ── Flats ────────────────────────────────────────────────────────────────
	
	/** Creates a landlord-owned, verified, visible flat. */
	public Flat createVerifiedFlat(User owner) {
		Flat flat = buildFlat();
		flat.setOwner(owner);
		flat.setCreatedBy(owner);
		flat.setVerified(true);
		flat.setVisible(true);
		Flat saved = flatRepository.save(flat);
		flatRepository.flush();  // ✅
		return saved;
	}
	
	/** Creates a tenant-created flat (unverified, hidden). */
	public Flat createTenantFlat(User tenant) {
		Flat flat = buildFlat();
		flat.setCreatedBy(tenant);
		flat.setVerified(false);
		flat.setVisible(false);
		Flat saved = flatRepository.save(flat);
		flatRepository.flush();  // ✅
		return saved;
	}
	
	private Flat buildFlat() {
		Flat flat = new Flat();
		flat.setRent(BigDecimal.valueOf(15000));
		flat.setNumberOfRooms(2);
		flat.setArea(BigDecimal.valueOf(700));
		flat.setFurnished(true);
		flat.setBathrooms(1);
		flat.setParking(true);
		flat.setAvailableFrom(LocalDate.now().plusDays(30));
		flat.setPropertyType(PropertyType.APARTMENT);
		flat.setDescription("Test flat created by TestDataFactory");
		flat.setAvailable(true);
		
		Address addr = new Address();
		addr.setAddressLine("Test Line");
		addr.setCity("Pune");
		addr.setState("Maharashtra");
		addr.setZipCode("411001");
		flat.setAddress(addr);
		
		return flat;
	}
	
	/** Builds a CreateFlatRequest for HTTP-level tests. */
	public CreateFlatRequest buildCreateFlatRequest() {
		CreateFlatRequest req = new CreateFlatRequest();
		req.setRent(BigDecimal.valueOf(15000));
		req.setNumberOfRooms(2);
		req.setArea(BigDecimal.valueOf(700));
		req.setFurnished(true);
		req.setBathrooms(1);
		req.setParking(true);
		req.setAvailableFrom(LocalDate.now().plusDays(30));
		req.setPropertyType(PropertyType.APARTMENT);
		req.setDescription("Test flat via HTTP");
		req.setAvailable(true);
		
		AddressDto addr = AddressDto.builder()
											 .addressLine("HTTP Test Lane")
											 .city("Pune")
											 .state("Maharashtra")
											 .zipCode("411001")
											 .build();
		req.setAddress(addr);
		
		return req;
	}
	
	// ── Reviews ──────────────────────────────────────────────────────────────
	public Review createReview(Flat flat, User user, ReviewStatus status) {
		Review review = new Review();
		review.setFlat(flat);
		review.setUser(user);
		review.setTitle("Test review");
		review.setContent("This is a test review body long enough to pass validation.");
		review.setRating(8);
		review.setStatus(status);
		Review saved = reviewRepository.save(review);
		reviewRepository.flush();  // ✅
		return saved;
	}
	
	// ── Flush helper ─────────────────────────────────────────────────────────
	public void flush() {
		userRepository.flush();
		flatRepository.flush();
		reviewRepository.flush();
	}
	
	public Review createReviewWithRating(Flat flat, User user, ReviewStatus status, int rating) {
		Review review = new Review();
		review.setFlat(flat);
		review.setUser(user);
		review.setTitle("Test review " + rating);
		review.setContent("This is a test review body long enough to pass validation.");
		review.setRating(rating);
		review.setStatus(status);
		Review saved = reviewRepository.save(review);
		reviewRepository.flush();
		return saved;
	}
	
}