package com.Market.MeatShop.Security.Repositories;

import com.Market.MeatShop.Security.Entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepo extends JpaRepository<Authority, Long> {}
