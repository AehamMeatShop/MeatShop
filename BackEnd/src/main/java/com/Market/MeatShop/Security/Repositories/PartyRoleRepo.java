package com.Market.MeatShop.Security.Repositories;

import com.Market.MeatShop.Security.Entities.PartyRole;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartyRoleRepo extends JpaRepository<PartyRole, Long> {
  List<PartyRole> findByPartyTypeAndPartyId(SecuritySubjectType partyType, Long partyId);

  Optional<PartyRole> findByPartyTypeAndPartyIdAndRoleId(
      SecuritySubjectType partyType, Long partyId, Long roleId);
}
