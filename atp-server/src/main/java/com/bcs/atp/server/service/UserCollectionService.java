package com.bcs.atp.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bcs.atp.server.gql.types.UserCollection;
import com.bcs.atp.server.model.UserCollectionModel;
import com.bcs.atp.server.model.qo.UserCollectionPageQo;

import java.util.List;

/**
 * <p>
 * 用户集合表 服务类
 * </p>
 *
 * @author Scott Lau
 * @since 2024-03-21
 */
public interface UserCollectionService extends IService<UserCollectionModel> {
  /**
   * <p>
   * 保存对象
   * </p>
   *
   * @param userCollection 对象实例
   * @return 是否保存成功
   */
  boolean create(UserCollectionModel userCollection);

  /**
   * <p>
   * 更新对象
   * </p>
   *
   * @param userCollection 对象实例
   * @return 是否更新成功
   */
  boolean update(UserCollectionModel userCollection);

  /**
   * <p>
   * 删除对象
   * </p>
   *
   * @param id 对象ID
   * @return 是否删除成功
   */
  boolean delete(String id);

  /**
   * <p>
   * 分页查询对象
   * </p>
   *
   * @param pageQo 分页查询条件
   * @return 分页查询结果
   */
  IPage<UserCollectionModel> list(UserCollectionPageQo pageQo);

  /**
   * <p>
   * 查询所有对象
   * </p>
   *
   * @return 所有对象
   */
  List<UserCollectionModel> listAll();

  /**
   * 将数据库对象转换为graphql对象
   *
   * @param dbModel 数据库对象
   * @return graphql对象
   */
  UserCollection convertDbModelToGraphqlModel(UserCollectionModel dbModel);
}
