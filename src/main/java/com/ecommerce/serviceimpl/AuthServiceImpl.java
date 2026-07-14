package com.ecommerce.serviceimpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.model.Role;
import com.ecommerce.auth.model.User;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.dto.AuthResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.exception.InvalidCredentialsException;
import com.ecommerce.security.JwtUtil;
import com.ecommerce.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;

	public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.authenticationManager = authenticationManager;
	}

	@Override
	public void register(RegisterRequest request) {
		// TODO Auto-generated method stub

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
		}

		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.ROLE_USER);

		userRepository.save(user);

	}

	public AuthResponse login(LoginRequest request) {

	    try {
	        authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(
	                request.getEmail(),
	                request.getPassword()
	            )
	        );
	    } catch (AuthenticationException ex) {
	        throw new InvalidCredentialsException("Invalid email or password");
	    }

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

	    AuthResponse response = new AuthResponse();
	    response.setToken(token);
	    response.setEmail(user.getEmail());
	    response.setRole(user.getRole().name());

	    return response;
	}

}
