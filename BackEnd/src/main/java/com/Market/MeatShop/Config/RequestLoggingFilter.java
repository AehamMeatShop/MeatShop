package com.Market.MeatShop.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
  @Value("${instance.id:unknown}")
  private String instanceId;

  @Value("${instance.name:Server}")
  private String instanceName;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestId = UUID.randomUUID().toString();

    MDC.put("requestId", requestId);
    MDC.put("ip", request.getRemoteAddr());
    MDC.put("method", request.getMethod());
    MDC.put("endpoint", request.getRequestURI());
    MDC.put("instanceId", instanceId);
    MDC.put("instanceName", instanceName);
    try {
      filterChain.doFilter(request, response);
    } finally {
      // مهم جداً - امسح MDC بعد كل request
      MDC.clear();
    }
  }
}
