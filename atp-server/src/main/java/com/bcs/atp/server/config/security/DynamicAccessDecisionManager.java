package com.bcs.atp.server.config.security;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * 动态权限决策管理器，用于判断用户是否有访问权限
 *
 * @author scott
 * @since 2024/03/29
 */
@Slf4j
@Component
@ConditionalOnBean(name = "dynamicSecurityService")
public class DynamicAccessDecisionManager implements AccessDecisionManager {

  @Override
  public void decide(Authentication authentication, Object object,
                     Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
    // 当接口未被配置资源时直接放行
    if (CollUtil.isEmpty(configAttributes)) {
      return;
    }
    Iterator<ConfigAttribute> iterator = configAttributes.iterator();
    while (iterator.hasNext()) {
      ConfigAttribute configAttribute = iterator.next();
      //将访问所需资源或用户拥有资源进行比对
      String needAuthority = configAttribute.getAttribute();
      for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
        if (needAuthority.trim().equals(grantedAuthority.getAuthority())) {
          return;
        }
      }
    }
    String url = object.toString();
    if (object instanceof FilterInvocation) {
      url = ((FilterInvocation) object).getRequestUrl();
      int index = url.indexOf("?");
      if (index > 0) {
        url = url.substring(0, index);
      }
    }
    log.info("failed to authorize " + url);
    throw new AccessDeniedException(url);
  }

  @Override
  public boolean supports(ConfigAttribute configAttribute) {
    return true;
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return true;
  }

}
