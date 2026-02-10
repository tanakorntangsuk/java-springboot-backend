package com.example.backend.service;

import com.example.backend.entity.Address;
import com.example.backend.entity.Social;
import com.example.backend.entity.User;
import com.example.backend.repository.AddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }


    public List<Address> findByUser(User user){
        return addressRepository.findByUser(user);
    }

    @Transactional
    public Address create(User user, String line1, String line2, String zipcode){

        Address address = new Address();

        address.setUser(user);
        address.setLine1(line1);
        address.setLine2(line2);
        address.setZipcode(zipcode);

        return addressRepository.save(address);
    }

}