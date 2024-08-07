package com.bcs.atp.server.service;

import com.bcs.atp.server.gql.types.TeamMember;
import com.bcs.atp.server.model.TeamMemberModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bcs.atp.server.model.qo.TeamMemberPageQo;

import java.util.List;

/**
 * <p>
 * 团队成员表 服务类
 * </p>
 *
 * @author Scott Lau
 * @since 2024-03-21
 */
public interface TeamMemberService extends IService<TeamMemberModel> {
  /**
   * <p>
   * 保存对象
   * </p>
   *
   * @param teamMember 对象实例
   * @return 是否保存成功
   */
  boolean create(TeamMemberModel teamMember);

  /**
   * <p>
   * 更新对象
   * </p>
   *
   * @param teamMember 对象实例
   * @return 是否更新成功
   */
  boolean update(TeamMemberModel teamMember);

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
  IPage<TeamMemberModel> list(TeamMemberPageQo pageQo);

  /**
   * <p>
   * 查询所有对象
   * </p>
   *
   * @return 所有对象
   */
  List<TeamMemberModel> listAll();

  /**
   * 将数据库对象转换为graphql对象
   * @param teamMemberModel
   * @return
   */
  TeamMember convertDbModelToGraphqlModel(TeamMemberModel teamMemberModel);

  /**
   * 通过teamId查所有成员对象
   * @param teamId
   * @return
   */
  List<TeamMemberModel> findByTeamId(String teamId);
}
