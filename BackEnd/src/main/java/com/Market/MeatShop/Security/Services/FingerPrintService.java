package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.Utils.FingerPrint;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class FingerPrintService {

  private final PasswordEncoder encoder;

  public FingerPrintService(PasswordEncoder encoder) {
    this.encoder = encoder;
  }

  public String serialize(FingerPrint fingerPrint) {
    return fingerPrint.ip()
        + "__"
        + fingerPrint.DID()
        + "__"
        + fingerPrint.screenResolution()
        + "__"
        + fingerPrint.os()
        + "__"
        + fingerPrint.osVersion()
        + "__"
        + fingerPrint.browser();
  }

  public FingerPrint toFingerPrint(String fingerPrintString) {

    String[] signals = fingerPrintString.split("__");
    FingerPrint fingerPrint =
        new FingerPrint(signals[0], signals[1], signals[2], signals[3], signals[4], signals[5]);

    return fingerPrint;
  }

  public int getTrustScore(FingerPrint originalFingerPrint, FingerPrint actualFingerPrint) {

    int trustScore = 0;

    if (encoder.matches(actualFingerPrint.DID(), originalFingerPrint.DID())) {
      trustScore += 65;
    }

    if (originalFingerPrint.ip().equals(actualFingerPrint.ip())) {
      trustScore += 5;
    }

    if (originalFingerPrint.browser().equals(actualFingerPrint.browser())) {
      trustScore += 5;
    }

    if (originalFingerPrint.os().equals(actualFingerPrint.os())) {
      trustScore += 5;
    }

    if (originalFingerPrint.osVersion().equals(actualFingerPrint.osVersion())) {
      trustScore += 5;
    }

    if (originalFingerPrint.screenResolution().equals(actualFingerPrint.screenResolution())) {
      trustScore += 15;
    }

    return trustScore;
  }
}
