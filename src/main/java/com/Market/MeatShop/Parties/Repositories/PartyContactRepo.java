package com.Market.MeatShop.Parties.Repositories;

import com.Market.MeatShop.Parties.Entities.Party;
import com.Market.MeatShop.Parties.Entities.PartyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PartyContactRepo extends JpaRepository<PartyContact , Long> , JpaSpecificationExecutor<PartyContact> {


}
