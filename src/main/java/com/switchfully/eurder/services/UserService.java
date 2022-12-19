package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.CreateUserDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.Adress;
import com.switchfully.eurder.domain.City;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.exceptions.InvallidInputException;
import com.switchfully.eurder.domain.repositories.CityRepository;
import com.switchfully.eurder.domain.repositories.RoleRepository;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import com.switchfully.eurder.services.mappers.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, CityRepository cityRepository, RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    public UserDto createCustomer(CreateUserDto createUserDto) throws InvallidInputException {
        City city = cityRepository.findCitiesByZip(createUserDto.zip()).stream().findFirst().orElseThrow(()-> new NoSuchElementException("Incorrect Zip"));
        Adress adress = new Adress(createUserDto.street(),createUserDto.houseNumber(), city);
        Role customerRole = getUserRole();
        if (userRepository.getUserByEmail(createUserDto.email()).size()>0) throw new IllegalArgumentException("This user already exists");
        User user = new User(createUserDto.firstname(), createUserDto.lastname(), createUserDto.email(), createUserDto.phoneNumber(), adress, customerRole);
        return userMapper.toDto(userRepository.save(user));
    }

    public List<UserDto> getAllCustomers() {
        Role customerRole = getUserRole();
        return userMapper.toDto(userRepository.getUsersByRole(customerRole));
    }

    private Role getUserRole(){
        return roleRepository.findRolesByName("CUSTOMER").stream().findFirst().orElseThrow(()-> new NoSuchElementException("Role inconsitent"));
    }

    public UserDto getCustomerById(String id) throws IllegalArgumentException {
        int userId;
        try {
            userId = Integer.parseInt(id);
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userMapper.toDto(userRepository.getUsersById(userId).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("User id " + id + " not found.")));
    }
}
