package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.Entities.LoginIndex;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Repositories.LoginIndexRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class LoginIndexService {

  private final LoginIndexRepo loginIndexRepo;

  public LoginIndexService(LoginIndexRepo loginIndexRepo) {
    this.loginIndexRepo = loginIndexRepo;
  }

  @Transactional
  public boolean createIndex(Long subjectId, SecuritySubjectType subjectType, String email) {
    log.info("Attempting to create login index for email: {}", email);

    if (loginIndexRepo.existsByEmail(email)) {
      log.warn("Login index already exists for email: {}", email);
      return false;
    }

    LoginIndex index = new LoginIndex();
    index.setSubjectId(subjectId);
    index.setSubjectType(subjectType);
    index.setEmail(email);

    try {
      loginIndexRepo.save(index);
      log.info("Login index created successfully for email: {}", email);
      return true;
    } catch (Exception e) {
      log.error("Failed to create login index for email: {}", email, e);
      return false;
    }
  }

  @Transactional
  public boolean updateEmail(String oldEmail, String newEmail) {
    log.info("Attempting to update login index email from {} to {}", oldEmail, newEmail);

    LoginIndex index = loginIndexRepo.findByEmail(oldEmail).orElse(null);
    if (index == null) {
      log.warn("Login index not found for email: {}", oldEmail);
      return false;
    }

    if (loginIndexRepo.existsByEmail(newEmail)) {
      log.warn("Login index already exists for new email: {}", newEmail);
      return false;
    }

    try {
      index.setEmail(newEmail);
      loginIndexRepo.save(index);
      log.info("Login index email updated successfully from {} to {}", oldEmail, newEmail);
      return true;
    } catch (Exception e) {
      log.error("Failed to update login index email from {} to {}", oldEmail, newEmail, e);
      return false;
    }
  }

  @Transactional
  public boolean deleteIndex(String email) {
    log.info("Attempting to delete login index for email: {}", email);

    LoginIndex index = loginIndexRepo.findByEmail(email).orElse(null);
    if (index == null) {
      log.warn("Login index not found for email: {}", email);
      return false;
    }

    try {
      loginIndexRepo.delete(index);
      log.info("Login index deleted successfully for email: {}", email);
      return true;
    } catch (Exception e) {
      log.error("Failed to delete login index for email: {}", email, e);
      return false;
    }
  }

  public LoginIndex findByEmail(String email) {
    log.info("Finding login index by email: {}", email);
    return loginIndexRepo.findByEmail(email).orElse(null);
  }
}
