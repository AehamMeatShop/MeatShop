package com.Market.MeatShop.Security.Repositories;

import com.Market.MeatShop.Security.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
