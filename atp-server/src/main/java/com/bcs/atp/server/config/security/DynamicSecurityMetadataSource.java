package com.bcs.atp.server.config.security;

import cn.hutool.core.util.URLUtil;
import com.bcs.atp.server.service.security.DynamicSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 动态权限数据源，用于获取动态权限规则
 *
 * @author scott
 * @since 2024/03/29
 */
@Component
@ConditionalOnBean(name = "dynamicSecurityService")
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

  private static Map<String, ConfigAttribute> configAttributeMap = null;
  @Autowired
  private DynamicSecurityService dynamicSecurityService;

  @PostConstruct
  public void loadDataSource() {
    configAttributeMap = dynamicSecurityService.loadDataSource();
  }

  public void clearDataSource() {
    configAttributeMap.clear();
    configAttributeMap = null;
  }

  @Override
  public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
    if (configAttributeMap == null) {
      this.loadDataSource();
    }
    List<ConfigAttribute> configAttributes = new ArrayList<>();
    //获取当前访问的路径
    String url = ((FilterInvocation) o).getRequestUrl();
    String path = URLUtil.getPath(url);
    PathMatcher pathMatcher = new AntPathMatcher();
    Iterator<String> iterator = configAttributeMap.keySet().iterator();
    //获取访问该路径所需资源
    while (iterator.hasNext()) {
      String pattern = iterator.next();
      if (pathMatcher.match(pattern, path)) {
        configAttributes.add(configAttributeMap.get(pattern));
      }
    }
    // 未设置操作请求权限，返回空集合
    return configAttributes;
  }

  @Override
  public Collection<ConfigAttribute> getAllConfigAttributes() {
    return null;
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return true;
  }

}
