package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.DTOs.FingerPrint;
import com.Market.MeatShop.Security.Enums.SessionState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FingerPrintService {

  public String serialize(FingerPrint fp) {
    return String.join(
        "__",
        safe(fp.ip()),
        safe(fp.DID()),
        safe(fp.screenResolution()),
        safe(fp.os()),
        safe(fp.osVersion()),
        safe(fp.browser()));
  }

  public FingerPrint toFingerPrint(String data) {

    String[] s = data.split("__", -1);

    if (s.length != 6) {
      throw new IllegalArgumentException(
          "Invalid fingerprint format: " + data + " length = " + s.length);
    }

    return new FingerPrint(s[0], s[1], s[2], s[3], s[4], s[5]);
  }

  public int getTrustScore(FingerPrint original, FingerPrint actual) {
    log.info("=== TRUST SCORE CALCULATION ===");
    log.info("Baseline: {}", serialize(original));
    log.info("Current:  {}", serialize(actual));
    int score = 0;

    // 1. DID (strong signal)
    if (isPresent(original.DID())
        && isPresent(actual.DID())
        && actual.DID().equals(original.DID())) {
      log.info("same did {} score {}", original.DID(), score);
      score += 50;
    } else {
      log.info("def did {} score {}", original.DID(), score);
      score -= 30;
    }

    // 2. IP (weak signal)
    score += compareWeak(original.ip(), actual.ip(), 10, -10);
    log.info("ip compare {} score {}", original.ip(), score);
    // 3. Browser
    score += compareMedium(original.browser(), actual.browser(), 10, -5);
    log.info("browser compare {} score {}", original.browser(), score);
    // 4. OS
    score += compareMedium(original.os(), actual.os(), 10, -5);
    log.info("os compare {} score {}", original.os(), score);

    // 5. OS Version
    score += compareWeak(original.osVersion(), actual.osVersion(), 5, 0);
    log.info("osV compare {} score {}", original.osVersion(), score);

    // 6. Screen Resolution
    score += compareWeak(original.screenResolution(), actual.screenResolution(), 5, 0);
    log.info("SR compare {} score {}", original.screenResolution(), score);

    // penalty for missing signals (important improvement)
    score -= missingSignalPenalty(actual);
    log.info("penalty {} score {} ", missingSignalPenalty(actual), score);

    return clamp(score, 0, 100);
  }

  // ---------------- HELPERS ----------------

  private String safe(String v) {
    return v == null ? "" : v;
  }

  private boolean isPresent(String v) {
    return v != null && !v.isBlank();
  }

  private int compareWeak(String a, String b, int match, int mismatch) {
    if (!isPresent(a) || !isPresent(b)) return 0;
    return a.equals(b) ? match : mismatch;
  }

  private int compareMedium(String a, String b, int match, int mismatch) {
    if (!isPresent(a) || !isPresent(b)) return mismatch; // unknown = slight penalty
    return a.equals(b) ? match : mismatch;
  }

  private int missingSignalPenalty(FingerPrint fp) {
    int penalty = 0;

    if (!isPresent(fp.ip())) penalty += 5;
    if (!isPresent(fp.browser())) penalty += 5;
    if (!isPresent(fp.os())) penalty += 5;
    if (!isPresent(fp.osVersion())) penalty += 3;
    if (!isPresent(fp.screenResolution())) penalty += 3;
    if (!isPresent(fp.DID())) penalty += 20; // مهم جدًا

    return penalty;
  }

  private int clamp(int v, int min, int max) {
    return Math.max(min, Math.min(max, v));
  }

  public SessionState getSuitableSessionState(int score) {
    if (score >= 70) {
      return SessionState.ACTIVE;
    }

    if (score >= 45) {
      return SessionState.OBSERVED;
    }

    if (score > 25) {
      return SessionState.CHALLENGED;
    }

    return SessionState.SUSPICIOUS;
  }

  public int getQuality(FingerPrint fp) {

    int quality = 0;

    if (!safe(fp.DID()).isBlank()) quality++;
    if (!safe(fp.browser()).isBlank()) quality++;
    if (!safe(fp.os()).isBlank()) quality++;
    if (!safe(fp.osVersion()).isBlank()) quality++;
    if (!safe(fp.screenResolution()).isBlank()) quality++;

    return quality;
  }

  private int extractMajorVersion(String version) {

    String digits = version.replaceAll("[^0-9.]", "");

    if (digits.isBlank()) {
      return 0;
    }

    return Integer.parseInt(digits.split("\\.")[0]);
  }

  private int osVersionPenalty(String baselineVersion, String currentVersion) {

    baselineVersion = safe(baselineVersion);
    currentVersion = safe(currentVersion);

    if (baselineVersion.equals(currentVersion)) {
      return 0;
    }

    try {

      int oldMajor = extractMajorVersion(baselineVersion);
      int newMajor = extractMajorVersion(currentVersion);

      int diff = Math.abs(oldMajor - newMajor);

      if (diff == 1) {
        return 2;
      }

      if (diff == 2) {
        return 5;
      }

      return 15;

    } catch (Exception ex) {
      return 10;
    }
  }

  private int calculateAdaptiveChangeScore(FingerPrint baseline, FingerPrint current) {

    int score = 100;

    if (isPresent(baseline.os())
        && isPresent(current.os())
        && !baseline.os().equalsIgnoreCase(current.os())) {

      score -= 60;

      log.info(
          "Adaptive score penalty. OS changed. baseline={} current={} penalty=60",
          baseline.os(),
          current.os());
    }

    if (isPresent(baseline.browser())
        && isPresent(current.browser())
        && !baseline.browser().equalsIgnoreCase(current.browser())) {

      score -= 15;

      log.info(
          "Adaptive score penalty. Browser changed. baseline={} current={} penalty=15",
          baseline.browser(),
          current.browser());
    }

    if (isPresent(baseline.screenResolution())
        && isPresent(current.screenResolution())
        && !baseline.screenResolution().equalsIgnoreCase(current.screenResolution())) {

      score -= 10;

      log.info(
          "Adaptive score penalty. Screen changed. baseline={} current={} penalty=10",
          baseline.screenResolution(),
          current.screenResolution());
    }

    if (isPresent(baseline.osVersion()) && isPresent(current.osVersion())) {

      int penalty = osVersionPenalty(baseline.osVersion(), current.osVersion());

      score -= penalty;

      log.info(
          "Adaptive score penalty. OS version changed. baseline={} current={} penalty={}",
          baseline.osVersion(),
          current.osVersion(),
          penalty);
    }

    log.info("Adaptive score result={}", Math.max(0, score));

    return Math.max(0, score);
  }

  public boolean shouldUpdateBaseline(FingerPrint baseline, FingerPrint current) {

    if (!current.DID().equals(baseline.DID())) {

      log.info(
          "Baseline update rejected. DID mismatch. baselineDid={} currentDid={}",
          baseline.DID(),
          current.DID());

      return false;
    }

    int baselineQuality = getQuality(baseline);
    int currentQuality = getQuality(current);

    log.info(
        "Baseline update check. baselineQuality={} currentQuality={}",
        baselineQuality,
        currentQuality);

    if (currentQuality > baselineQuality) {

      log.info(
          "Baseline update accepted. Quality improved from {} to {}",
          baselineQuality,
          currentQuality);

      return true;
    }

    int changeScore = calculateAdaptiveChangeScore(baseline, current);

    log.info("Baseline adaptive comparison. changeScore={} threshold=80", changeScore);

    boolean result = changeScore >= 80;

    log.info("Baseline update decision={}", result);

    return result;
  }

  public boolean canReplaceBaseline(FingerPrint baseline, FingerPrint current) {

    if (!shouldUpdateBaseline(baseline, current)) {

      return false;
    }

    return getQuality(current) >= getQuality(baseline);
  }
}
