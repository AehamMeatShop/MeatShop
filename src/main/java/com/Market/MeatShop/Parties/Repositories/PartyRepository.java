package com.Market.MeatShop.Parties.Repositories;

import com.Market.MeatShop.Parties.Entities.Party;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PartyRepository extends JpaRepository<Party, Long> {
}
