package com.Market.MeatShop.Parties.Spesifications;

import com.Market.MeatShop.Parties.Entities.Party;
import com.Market.MeatShop.Parties.Enums.PartyType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PartySpecifications {
    public static Specification<Party> likePartyName(String partyName){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("partyName"),"%"+partyName+"%"));
    }
    public static Specification<Party> likePartyAddress(String partyAddress){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("partyAddress"),"%"+partyAddress+"%"));
    }

    public static Specification<Party> betweenCreatingDates(LocalDateTime fromCreatedAt , LocalDateTime toDate ){
        if( fromCreatedAt!=null && toDate ==null){
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromCreatedAt));
        } else if (fromCreatedAt==null && toDate !=null) {
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate ));
        }

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createdAt"), fromCreatedAt, toDate));
    }

    public static Specification<Party> betweenUpdatingDates(LocalDateTime fromUpdatedAt , LocalDateTime toDate ){
        if( fromUpdatedAt!=null && toDate ==null){
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), fromUpdatedAt));
        } else if (fromUpdatedAt==null && toDate !=null) {
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), toDate ));
        }

            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("updatedAt"), fromUpdatedAt, toDate));

    }
    public static Specification<Party> likePartyType(PartyType partyType){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("partyType"),"%"+partyType.toString()+"%"));
    }


}
