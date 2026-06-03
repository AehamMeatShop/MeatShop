package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.DTOs.Requests.SessionFilterRequest;
import com.Market.MeatShop.Security.DTOs.SessionViewDto;
import com.Market.MeatShop.Security.Entities.Session;
import com.Market.MeatShop.Security.Enums.SessionState;
import com.Market.MeatShop.Security.Mappers.SessionMapper;
import com.Market.MeatShop.Security.QueryRoles.SessionQueryRoles;
import com.Market.MeatShop.Security.Repositories.SessionRepo;
import com.Market.MeatShop.Security.Specifications.SessionSpecifications;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SessionService {

  private final SessionRepo sessionRepo;
  private final SessionMapper sessionMapper;

  public SessionService(SessionRepo sessionRepo, SessionMapper sessionMapper) {
    this.sessionRepo = sessionRepo;
    this.sessionMapper = sessionMapper;
  }

  public Page<SessionViewDto> findAllByFilter(SessionFilterRequest filter, Pageable pageable) {
    pageable
        .getSort()
        .forEach(
            sort -> {
              if (!SessionQueryRoles.ALLOWED_SORT_FIELDS.contains(sort.getProperty())) {
                throw new IllegalArgumentException("Sorting not allowed on: " + sort.getProperty());
              }
            });
    if (pageable.getPageSize() > SessionQueryRoles.maxPageSize) {
      throw new IllegalArgumentException(
          "Page size is greater than " + SessionQueryRoles.maxPageSize);
    }

    Specification<Session> spec = Specification.allOf();
    if (filter.partyType() != null) {
      spec = spec.and(SessionSpecifications.likePartyType(filter.partyType()));
    }
    if (filter.partyId() != null) {
      spec = spec.and(SessionSpecifications.hasPartyId(filter.partyId()));
    }
    if (filter.state() != null) {
      spec = spec.and(SessionSpecifications.hasState(filter.state()));
    }
    if (filter.createdAt() != null) {
      spec = spec.and(SessionSpecifications.hasCreatedAt(filter.createdAt()));
    }
    if (filter.updatedAt() != null) {
      spec = spec.and(SessionSpecifications.hasUpdatedAt(filter.updatedAt()));
    }
    if (filter.trustScore() != null) {
      spec = spec.and(SessionSpecifications.hasTrustScore(filter.trustScore()));
    }

    Page<Session> sessions = sessionRepo.findAll(spec, pageable);
    Page<SessionViewDto> resp = sessions.map(sessionMapper::toViewDto);
    log.info("Sessions returned: {}", resp.getContent().size());
    return resp;
  }

  public SessionViewDto updateSessionState(Long sessionId, SessionState newState) {
    Session session =
        sessionRepo
            .findById(sessionId)
            .orElseThrow(() -> new TargetNotFound("Session not found with id: " + sessionId));
    session.setState(newState);
    session = sessionRepo.save(session);
    log.info("Session state updated to {} for session id: {}", newState, sessionId);
    return sessionMapper.toViewDto(session);
  }
}
