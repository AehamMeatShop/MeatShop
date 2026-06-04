package com.Market.MeatShop.Security.Specifications;

import com.Market.MeatShop.Security.Entities.Session;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Enums.SessionState;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class SessionSpecifications {

  public static Specification<Session> likePartyType(SecuritySubjectType partyType) {
    return (root, query, builder) ->
        builder.like(root.get("partyType").as(String.class), "%" + partyType + "%");
  }

  public static Specification<Session> hasPartyId(Long partyId) {
    return (root, query, builder) -> builder.equal(root.get("partyId").as(Long.class), partyId);
  }

  public static Specification<Session> hasState(SessionState state) {
    return (root, query, builder) -> builder.equal(root.get("state").as(SessionState.class), state);
  }

  public static Specification<Session> hasCreatedAt(LocalDateTime createdAt) {
    return (root, query, builder) ->
        builder.equal(root.get("createdAt").as(LocalDateTime.class), createdAt);
  }

  public static Specification<Session> hasUpdatedAt(LocalDateTime updatedAt) {
    return (root, query, builder) ->
        builder.equal(root.get("updatedAt").as(LocalDateTime.class), updatedAt);
  }

  public static Specification<Session> hasTrustScore(Integer trustScore) {
    return (root, query, builder) ->
        builder.equal(root.get("trustScore").as(Integer.class), trustScore);
  }
}
