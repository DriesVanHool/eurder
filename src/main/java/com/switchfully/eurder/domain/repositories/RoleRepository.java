package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    List<Role> findRolesByName(String name);
}
