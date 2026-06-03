package com.Market.MeatShop.Security.Repositories;

import com.Market.MeatShop.Security.Entities.Authority;
import com.Market.MeatShop.Security.Entities.PartyAuthority;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PartyAuthorityRepo extends JpaRepository<PartyAuthority, Long> {
  @Query(
      "SELECT pa FROM PartyAuthority pa JOIN FETCH pa.authority WHERE pa.partyType = :partyType AND pa.partyId = :partyId")
  List<PartyAuthority> findByPartyTypeAndPartyId(
      @Param("partyType") SecuritySubjectType partyType, @Param("partyId") Long partyId);

  @Query(
      "SELECT DISTINCT pa.authority.authority FROM PartyAuthority pa WHERE pa.partyType = :partyType AND pa.partyId = :partyId")
  Set<String> findAuthorityNamesByPartyTypeAndPartyId(
      @Param("partyType") SecuritySubjectType partyType, @Param("partyId") Long partyId);

  @Query(
      "SELECT DISTINCT pa.authority.authority FROM PartyAuthority pa WHERE pa.partyType = :partyType AND pa.partyId = :partyId")
  List<Authority> findAuthorityByPartyTypeAndPartyId(
      @Param("partyType") SecuritySubjectType partyType, @Param("partyId") Long partyId);

  @Query(
      "SELECT pa FROM PartyAuthority pa JOIN FETCH pa.authority WHERE pa.partyType = :partyType AND pa.partyId = :partyId AND pa.authority.id = :authorityId")
  Optional<PartyAuthority> findByPartyTypeAndPartyIdAndAuthorityId(
      @Param("partyType") SecuritySubjectType partyType,
      @Param("partyId") Long partyId,
      @Param("authorityId") Long authorityId);
}
