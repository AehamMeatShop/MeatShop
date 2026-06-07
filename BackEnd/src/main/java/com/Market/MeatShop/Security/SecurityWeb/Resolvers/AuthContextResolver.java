package com.Market.MeatShop.Security.SecurityWeb.Resolvers;

import com.Market.MeatShop.Security.SecurityWeb.Dto.AuthContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthContextResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(AuthContext.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    String deviceId = webRequest.getHeader("did");

    String os = webRequest.getHeader("os");

    String osVersion = webRequest.getHeader("osVersion");

    String browser = webRequest.getHeader("browser");

    String screenResolution = webRequest.getHeader("screenResolution");

    return new AuthContext(deviceId, os, osVersion, browser, screenResolution);
  }
}
