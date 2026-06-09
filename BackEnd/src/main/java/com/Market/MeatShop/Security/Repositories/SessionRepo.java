package com.Market.MeatShop.Security.Repositories;

import com.Market.MeatShop.Security.Entities.Session;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface SessionRepo
    extends JpaRepository<Session, Long>, JpaSpecificationExecutor<Session> {

  Page<Session> findAll(Specification<Session> spec, Pageable pageable);

  List<Session> findByPartyType(String partyType);

  List<Session> findByPartyId(Long partyId);

  Optional<Session> findByIdAndPartyIdAndPartyType(
      Long id, Long partyId, SecuritySubjectType partyType);

  Optional<Session> findFirstByBaseLineFingerPrintContainingAndPartyIdAndPartyType(
      String value, Long partyId, SecuritySubjectType partyType);

  Optional<Session> findByRefreshToken(String refreshTokenHash);
}
