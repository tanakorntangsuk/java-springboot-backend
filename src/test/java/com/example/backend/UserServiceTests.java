package com.example.backend;

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
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTests {

	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SocialService socialService;
	@Autowired
	private AddressService addressService;


	@Order(1)
	@Test
	void testCreateUser() throws BaseException {

		RegisterRequest request = new RegisterRequest();
		request.setEmail(TestData.email);
		request.setPassword(TestData.password);
		request.setName(TestData.name);

		RegisterResponse response = userService.create(request);

		User user = userRepository.findByEmail(TestData.email)
				.orElseThrow();

		// check not null
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(user.getId());

		// check equals
		Assertions.assertEquals(TestData.email, response.getEmail());
		Assertions.assertEquals(TestData.name, response.getName());

		// db checks
		Assertions.assertTrue(
				passwordEncoder.matches(TestData.password, user.getPassword())
		);
	}

	@Order(2)
	@Test
	void testUpdate() throws BaseException {
		Optional<User> opt = userRepository.findByEmail(TestData.email);
		Assertions.assertTrue(opt.isPresent());

		User user = opt.get();
		User updatedUser = userService.updateNameById(user.getId(),"Golf");

		Assertions.assertNotNull(updatedUser);
		Assertions.assertEquals("Golf",updatedUser.getName());
	}

	@Order(3)
	@Test
	void testCreateSocial() throws BaseException {
		Optional<User> opt = userRepository.findByEmail(TestData.email); //ดึง user
		Assertions.assertTrue(opt.isPresent());

		User user = opt.get();

		Social social = user.getSocial();
		Assertions.assertNull(social);

		social = socialService.create(user,
				SocialTestData.facebook,
				SocialTestData.line,
				SocialTestData.instagram,
				SocialTestData.tiktok);

		Assertions.assertNotNull(social);
		Assertions.assertEquals(SocialTestData.facebook, social.getFacebook());
	}

	@Order(4)
	@Test
	void testCreateAddress() throws BaseException {
		Optional<User> opt = userRepository.findByEmail(TestData.email); //ดึง user
		Assertions.assertTrue(opt.isPresent());

		User user = opt.get();
		List<Address> addresses = user.getAddresses();
		Assertions.assertTrue(addresses.isEmpty());

		createAddress(user, AddressTestData1.line1, AddressTestData1.line2, AddressTestData1.zipcode);
		createAddress(user, AddressTestData2.line1, AddressTestData2.line2, AddressTestData2.zipcode);

	}
	private void createAddress(User user, String line1, String line2, String zipcode) throws BaseException {
		Address address = addressService.create(user,
				line1,
				line2,
				zipcode);

		Assertions.assertNotNull(address);
		Assertions.assertEquals(line1, address.getLine1());
		Assertions.assertEquals(line2, address.getLine2());
		Assertions.assertEquals(zipcode, address.getZipcode());

	}

	@Order(5)
	@Test
	void testDelete() {
		Optional<User> opt = userRepository.findByEmail(TestData.email); //ดึง user
		Assertions.assertTrue(opt.isPresent());

		User user = opt.get();

		// check social
		Social social = user.getSocial();
		Assertions.assertNotNull(social);
		Assertions.assertEquals(SocialTestData.facebook, social.getFacebook());

		// check address
		List<Address> addresses = user.getAddresses();
		Assertions.assertNotNull(addresses);
		Assertions.assertFalse(addresses.isEmpty());

		userService.deleteById(user.getId());

		Optional<User> optDelete = userRepository.findByEmail(TestData.email); //ค้นหาอีกรอบ
		Assertions.assertTrue(optDelete.isEmpty()); //ต้องไม่มี
	}

	interface TestData{
		String email = "golf@test.com";
		String password = "1234567890-=!@#$%^&*()_+";
		String name = "golf";
	}

	interface SocialTestData{
		String facebook = "Tanakorn Taengsuk";
		String line = "Tanakorn";
		String instagram = "Tanakorn";
		String tiktok = "Tanakorn";
	}

	interface AddressTestData1{
		String line1 = "95 m.6";
		String line2 = "baengpa, baengpa";
		String zipcode = "70160";
	}
	interface AddressTestData2{
		String line1 = "113 m.5";
		String line2 = "boukunk, dummansadung";
		String zipcode = "70130";
	}
}
