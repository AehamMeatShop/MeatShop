package com.Market.MeatShop.Parties.Repositories;

import com.Market.MeatShop.Parties.Entities.Party;
import org.springframework.data.repository.CrudRepository;

public interface PartyRepository extends CrudRepository<Party, Long> {
}
