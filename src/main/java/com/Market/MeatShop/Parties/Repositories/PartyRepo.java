package com.Market.MeatShop.Parties.Repositories;

import com.Market.MeatShop.Parties.Entities.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.domain.Pageable;
import java.util.List;


public interface PartyRepo extends JpaRepository<Party , Long> , JpaSpecificationExecutor<Party> {
    Page<Party> findAll(Specification<Party> spec , Pageable pageable);
}
