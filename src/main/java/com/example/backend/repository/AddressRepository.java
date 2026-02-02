package com.example.backend.repository;

import com.example.backend.entity.Address;
import com.example.backend.entity.Social;
import com.example.backend.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends CrudRepository<Address, String> {

    List<Address> findByUser (User user);

}
