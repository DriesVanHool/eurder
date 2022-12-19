package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findCitiesByZip(String zip);
}
