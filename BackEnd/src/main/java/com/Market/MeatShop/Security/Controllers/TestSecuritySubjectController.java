package com.Market.MeatShop.Security.Controllers;

import com.Market.MeatShop.Employees.Services.EmployeeSubjectProvider;
import com.Market.MeatShop.Security.Assemblers.*;
import com.Market.MeatShop.Security.Entities.PartyAuthority;
import com.Market.MeatShop.Security.Entities.PartyRole;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Repositories.PartyAuthorityRepo;
import com.Market.MeatShop.Security.Repositories.PartyRoleRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("test/security-subject")
@Slf4j
public class TestSecuritySubjectController {

  private final SecuritySubjectRegistry sr;
  private final SecuritySubjectFactory securitySubjectFactory;
  private final PartyRoleRepo paRoRepo;
  private final PartyAuthorityRepo partyAuthorityRepo;

  public TestSecuritySubjectController(
      SecuritySubjectFactory securitySubjectFactory,
      SecuritySubjectRegistry sr,
      PartyRoleRepo paRoRepo,
      PartyAuthorityRepo partyAuthorityRepo) {
    this.sr = sr;
    this.securitySubjectFactory = securitySubjectFactory;
    this.paRoRepo = paRoRepo;
    this.partyAuthorityRepo = partyAuthorityRepo;
  }

  @GetMapping("/{id}/{type}")
  public ResponseEntity<SecuritySubject> getSecuritySubject(
      @PathVariable Long id, @PathVariable(name = "type") SecuritySubjectType type) {
    log.info("Starting security subject fetch for employee ID: {}", id);

    log.info("Step 1: Fetching SecurityIdentity from EmployeeSubjectProvider");
    List<PartyRole> Lspr = paRoRepo.findByPartyTypeAndPartyId(type, id);
    SecuritySubjectType spType = null;
    if (!Lspr.isEmpty()) {
      spType = Lspr.get(0).getPartyType();
    } else {
      List<PartyAuthority> pa = partyAuthorityRepo.findByPartyTypeAndPartyId(type, id);
      if (!pa.isEmpty()) {
        spType = pa.get(0).getPartyType();
      }
    }
    if (spType == null) {
      log.warn("mo auth found ");
    }
    SecuritySubjectProvider secSubProv = sr.getProvider(type);
    SecurityIdentity identity = secSubProv.getSubject(id);
    log.info("Step 1 complete - SecurityIdentity: {}", identity);

    log.info("Step 2: Assembling SecuritySubject using SecuritySubjectFactory");
    SecuritySubject subject = securitySubjectFactory.assemble(identity);
    log.info(
        "Step 2 complete - SecuritySubject assembled with {} authorities",
        subject.authorities().size());

    log.info("Security subject fetch complete for employee ID: {}", id);
    return ResponseEntity.ok(subject);
  }
}
