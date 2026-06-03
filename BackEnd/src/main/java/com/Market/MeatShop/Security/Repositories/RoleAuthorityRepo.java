package com.Market.MeatShop.Security.Repositories;

import com.Market.MeatShop.Security.Entities.Authority;
import com.Market.MeatShop.Security.Entities.RoleAuthority;
import com.Market.MeatShop.Security.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleAuthorityRepo extends JpaRepository<RoleAuthority, Long> {
  @Query("SELECT ra FROM RoleAuthority ra JOIN FETCH ra.authority WHERE ra.role = :role")
  List<RoleAuthority> findByRole(@Param("role") Role role);

  @Query(
      "SELECT DISTINCT ra.authority.authority FROM RoleAuthority ra WHERE ra.role.id IN :roleIds")
  Set<String> findAuthorityNamesByRoleIds(@Param("roleIds") List<Long> roleIds);

  @Query(
      "SELECT DISTINCT ra.authority.authority FROM RoleAuthority ra WHERE ra.role.id IN :roleIds")
  List<Authority> findAuthorityByRoleIds(@Param("roleIds") List<Long> roleIds);

  @Query(
      "SELECT ra FROM RoleAuthority ra JOIN FETCH ra.authority JOIN FETCH ra.role WHERE ra.role.id = :roleId AND ra.authority.id = :authorityId")
  Optional<RoleAuthority> findByRoleIdAndAuthorityId(
      @Param("roleId") Long roleId, @Param("authorityId") Long authorityId);
}
