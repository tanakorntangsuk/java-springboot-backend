package com.example.backend;

import com.example.backend.business.EmailBusiness;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.RegisterResponse;
import com.example.backend.entity.Address;
import com.example.backend.entity.Social;
import com.example.backend.entity.User;
import com.example.backend.exception.BaseException;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AddressService;
import com.example.backend.service.SocialService;
import com.example.backend.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmailTests {

	@Autowired
	private EmailBusiness emailBusiness;

	@Order(1)
	@Test
	void testSendActivateEmail() throws BaseException {
		emailBusiness.sendActivateUserEmail(
				TestData.email,
				TestData.name,
				TestData.token
		);
	}

	interface TestData{
		String email = "taengsuk.t@gmail.com";
		String name = "tanakorn taengsuk";
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJCYWNrZW5kU2VydmljZSIsInByaW5jaXBhb";
	}
}
