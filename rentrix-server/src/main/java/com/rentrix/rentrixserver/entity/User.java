package com.rentrix.rentrixserver.entity;

import com.rentrix.rentrixserver.entity.constants.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
public class User extends BaseEntity implements UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private UserDetail userDetail;
	
	// UserDetails methods
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}
	
	@Override
	public String getUsername() {
		return email;
	}
	
	@Column(nullable = false)
	private Boolean deleted = false;
	
	@Override
	public boolean isAccountNonExpired() {return true;}
	
	@Override
	public boolean isAccountNonLocked() {return true;}
	
	@Override
	public boolean isCredentialsNonExpired() {return true;}
	
	@Override
	public boolean isEnabled() {
		return !Boolean.TRUE.equals(deleted);
	}
	
	// ── Display Username helpers methods ───────────────────────────────────────────────────
	
	/**
	 * Best-effort display name for UI. Falls back to email if no user detail.
	 * Never returns null.
	 */
	public String getDisplayName() {
		if (userDetail == null) return email;
		String first = userDetail.getFirstName() != null ? userDetail.getFirstName() : "";
		String last = userDetail.getLastName() != null ? userDetail.getLastName() : "";
		String full = (first + " " + last).trim();
		return full.isEmpty() ? email : full;
	}
	
}