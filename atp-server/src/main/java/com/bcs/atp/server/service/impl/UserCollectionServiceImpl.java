package com.bcs.atp.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bcs.atp.server.gql.types.ReqType;
import com.bcs.atp.server.gql.types.UserCollection;
import com.bcs.atp.server.mapper.UserCollectionMapper;
import com.bcs.atp.server.model.UserCollectionModel;
import com.bcs.atp.server.model.UserRequestModel;
import com.bcs.atp.server.model.qo.UserCollectionPageQo;
import com.bcs.atp.server.service.UserCollectionService;
import com.bcs.atp.server.service.UserRequestService;
import com.bcs.atp.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户集合表 服务实现类
 * </p>
 *
 * @author Scott Lau
 * @since 2024-03-21
 */
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollectionModel> implements UserCollectionService {
  @Autowired
  private UserService userService;

  @Autowired
  private UserRequestService userRequestService;

  @Override
  public boolean create(UserCollectionModel userCollection) {
    return save(userCollection);
  }

  @Override
  public boolean update(UserCollectionModel userCollection) {
    return updateById(userCollection);
  }

  @Override
  public boolean delete(String id) {
    return removeById(id);
  }

  @Override
  public IPage<UserCollectionModel> list(UserCollectionPageQo pageQo) {
    IPage<UserCollectionModel> page = new Page<>(pageQo.getPageNum(), pageQo.getPageSize());
    QueryWrapper<UserCollectionModel> wrapper = new QueryWrapper<>();
    return page(page, wrapper);
  }

  @Override
  public List<UserCollectionModel> listAll() {
    QueryWrapper<UserCollectionModel> wrapper = new QueryWrapper<>();
    LambdaQueryWrapper<UserCollectionModel> lambda = wrapper.lambda();
    return list(wrapper);
  }

  @Override
  public UserCollection convertDbModelToGraphqlModel(UserCollectionModel userCollectionModel) {
    return convertDbModelToGraphqlModel(userCollectionModel, new HashMap<>());
  }

  public UserCollection convertDbModelToGraphqlModel(UserCollectionModel userCollectionModel, Map<String, UserCollection> cache) {
    // 使用userId作为键，避免重复转换同一个用户
    String userId = userCollectionModel.getUserId();
    if (cache.containsKey(userId)) {
      return cache.get(userId);
    }

    UserCollection userCollection = new UserCollection();
    BeanUtil.copyProperties(userCollectionModel, userCollection);

    // 添加到缓存前先完成基本属性的复制，避免循环引用
    cache.put(userId, userCollection);

    // 递归填充用户信息
    userCollection.setUser(userService.convertDbModelToGraphqlModel(userService.getById(userId)));

    if (userCollectionModel.getParentId() != null) {
      UserCollectionModel parentUserCollectionModel = getById(userCollectionModel.getParentId());
      UserCollection parentUserCollection = convertDbModelToGraphqlModel(parentUserCollectionModel, cache);
      userCollection.setParent(parentUserCollection);
    }

    String id = userCollectionModel.getId();
    List<UserCollectionModel> userCollectionModelList = lambdaQuery().eq(UserCollectionModel::getParentId, id).list();
    for (UserCollectionModel model : userCollectionModelList) {
      if (ReqType.REST.name().equals(model.getType())) {
        userCollection.getChildrenREST().add(convertDbModelToGraphqlModel(model, cache));
      }
      if (ReqType.GQL.name().equals(model.getType())) {
        userCollection.getChildrenGQL().add(convertDbModelToGraphqlModel(model, cache));
      }
    }

    userCollection.setRequests(userRequestService.lambdaQuery().eq(UserRequestModel::getUserId, userId).list()
            .stream().map(userRequestService::convertDbModelToGraphqlModel).collect(Collectors.toList()));

    return userCollection;
  }

}
