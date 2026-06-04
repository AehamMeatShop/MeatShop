package com.Market.MeatShop.Security.Repositories;

import com.Market.MeatShop.Security.Entities.LoginIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginIndexRepo extends JpaRepository<LoginIndex, Long> {

  Optional<LoginIndex> findByEmail(String email);

  boolean existsByEmail(String email);
}
