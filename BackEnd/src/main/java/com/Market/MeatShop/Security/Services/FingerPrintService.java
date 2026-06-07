package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.DTOs.FingerPrint;
import com.Market.MeatShop.Security.Enums.SessionState;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    int score = 0;

    // 1. DID (strong signal)
    if (isPresent(original.DID())
        && isPresent(actual.DID())
        && actual.DID().equals(original.DID())) {
      score += 50;
    } else {
      score -= 30;
    }

    // 2. IP (weak signal)
    score += compareWeak(original.ip(), actual.ip(), 10, -10);

    // 3. Browser
    score += compareMedium(original.browser(), actual.browser(), 10, -5);

    // 4. OS
    score += compareMedium(original.os(), actual.os(), 10, -5);

    // 5. OS Version
    score += compareWeak(original.osVersion(), actual.osVersion(), 5, 0);

    // 6. Screen Resolution
    score += compareWeak(original.screenResolution(), actual.screenResolution(), 5, 0);

    // penalty for missing signals (important improvement)
    score -= missingSignalPenalty(actual);

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
    }

    if (isPresent(baseline.browser())
        && isPresent(current.browser())
        && !baseline.browser().equalsIgnoreCase(current.browser())) {

      score -= 15;
    }

    if (isPresent(baseline.screenResolution())
        && isPresent(current.screenResolution())
        && !baseline.screenResolution().equalsIgnoreCase(current.screenResolution())) {

      score -= 10;
    }

    if (isPresent(baseline.osVersion()) && isPresent(current.osVersion())) {

      score -= osVersionPenalty(baseline.osVersion(), current.osVersion());
    }

    return Math.max(0, score);
  }

  public boolean shouldUpdateBaseline(FingerPrint baseline, FingerPrint current) {

    if (!current.DID().equals(baseline.DID())) {
      return false;
    }

    int baselineQuality = getQuality(baseline);
    int currentQuality = getQuality(current);

    if (currentQuality > baselineQuality) {
      return true;
    }

    int changeScore = calculateAdaptiveChangeScore(baseline, current);

    return changeScore >= 80;
  }

  public boolean canReplaceBaseline(FingerPrint baseline, FingerPrint current) {

    if (!shouldUpdateBaseline(baseline, current)) {

      return false;
    }

    return getQuality(current) >= getQuality(baseline);
  }
}
