package com.bcs.atp.server.gql.mutations;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bcs.atp.server.gql.types.ReqType;
import com.bcs.atp.server.model.UserCollectionModel;
import com.bcs.atp.server.model.UserModel;
import com.bcs.atp.server.service.UserCollectionService;
import com.bcs.atp.server.util.AuthUserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@DgsComponent
public class CollectionAdminMutations {
/**
 *     """
 *   Creates a collection at the root of the team hierarchy (no parent collection)
 *   """
 *   createRootCollection(
 *     """ID of the team"""
 *     teamID: ID!
 *
 *     """Title of the new collection"""
 *     title: String!
 *
 *     """JSON string representing the collection data"""
 *     data: String
 *   ): TeamCollection!
 *
 *   """Import collections from JSON string to the specified Team"""
 *   importCollectionsFromJSON(
 *     """Id of the team to add to"""
 *     teamID: ID!
 *
 *     """JSON string to import"""
 *     jsonString: String!
 *
 *     """
 *     ID to the collection to which to import to (null if to import to the root of team)
 *     """
 *     parentCollectionID: ID
 *   ): Boolean!
 *
 *   """
 *   Replace existing collections of a specific team with collections in JSON string
 *   """
 *   replaceCollectionsWithJSON(
 *     """Id of the team to add to"""
 *     teamID: ID!
 *
 *     """JSON string to replace with"""
 *     jsonString: String!
 *
 *     """
 *     ID to the collection to which to import to (null if to import to the root of team)
 *     """
 *     parentCollectionID: ID
 *   ): Boolean!
 *
 *   """Create a collection that has a parent collection"""
 *   createChildCollection(
 *     """ID of the parent to the new collection"""
 *     collectionID: ID!
 *
 *     """Title of the new collection"""
 *     childTitle: String!
 *
 *     """JSON string representing the collection data"""
 *     data: String
 *   ): TeamCollection!
 *
 *   """Rename a collection"""
 *   renameCollection(
 *     """ID of the collection"""
 *     collectionID: ID!
 *
 *     """The updated title of the collection"""
 *     newTitle: String!
 *   ): TeamCollection! @deprecated(reason: "Switch to updateTeamCollection mutation instead")
 *
 *   """Delete a collection"""
 *   deleteCollection(
 *     """ID of the collection"""
 *     collectionID: ID!
 *   ): Boolean!
 *
 *   """Move a collection into a new parent collection or the root of the team"""
 *   moveCollection(
 *     """ID of the parent to the new collection"""
 *     parentCollectionID: ID
 *
 *     """ID of the collection"""
 *     collectionID: ID!
 *   ): TeamCollection!
 *
 *   """Update the order of collections"""
 *   updateCollectionOrder(
 *     """ID of the collection"""
 *     collectionID: ID!
 *
 *     """
 *     ID of the collection that comes after the updated collection in its new position
 *     """
 *     destCollID: ID
 *   ): Boolean!
 *
 *   """Update Team Collection details"""
 *   updateTeamCollection(
 *     """ID of the collection"""
 *     collectionID: ID!
 *
 *     """The updated title of the collection"""
 *     newTitle: String
 *
 *     """JSON string representing the collection data"""
 *     data: String
 *   ): TeamCollection!
 *
 *
 *
 *    """Creates root REST user collection(no parent user collection)"""
 *   createRESTRootUserCollection(
 *     """Title of the new user collection"""
 *     title: String!
 *
 *     """JSON string representing the collection data"""
 *     data: String
 *   ): UserCollection!
 *
 *   """Creates root GraphQL user collection(no parent user collection)"""
 *   createGQLRootUserCollection(
 *     """Title of the new user collection"""
 *     title: String!
 *
 *     """JSON string representing the collection data"""
 *     data: String
 *   ): UserCollection!
 *
 *   """Creates a new child GraphQL user collection"""
 *   createGQLChildUserCollection(
 *     """Title of the new user collection"""
 *     title: String!
 *
 *     """ID of the parent to the new user collection"""
 *     parentUserCollectionID: ID!
 *
 *     """JSON string representing the collection data"""
 *     data: String
 *   ): UserCollection!
 *
 *   """Creates a new child REST user collection"""
 *   createRESTChildUserCollection(
 *     """Title of the new user collection"""
 *     title: String!
 *
 *     """ID of the parent to the new user collection"""
 *     parentUserCollectionID: ID!
 *
 *     """JSON string representing the collection data"""
 *     data: String
 *   ): UserCollection!
 *
 *   """Rename a user collection"""
 *   renameUserCollection(
 *     """ID of the user collection"""
 *     userCollectionID: ID!
 *
 *     """The updated title of the user collection"""
 *     newTitle: String!
 *   ): UserCollection!
 *
 *   """Delete a user collection"""
 *   deleteUserCollection(
 *     """ID of the user collection"""
 *     userCollectionID: ID!
 *   ): Boolean!
 *
 *   """Move user collection into new parent or root"""
 *   moveUserCollection(
 *     """ID of the parent to the new collection"""
 *     destCollectionID: ID
 *
 *     """ID of the collection"""
 *     userCollectionID: ID!
 *   ): UserCollection!
 *
 *   """
 *   Update the order of UserCollections inside parent collection or in root
 *   """
 *   updateUserCollectionOrder(
 *     """ID of collection being moved"""
 *     collectionID: ID!
 *
 *     """ID of collection being moved"""
 *     nextCollectionID: ID
 *   ): Boolean!
 *
 *   """Import collections from JSON string to the specified Team"""
 *   importUserCollectionsFromJSON(
 *     """JSON string to import"""
 *     jsonString: String!
 *
 *     """Type of UserCollection"""
 *     reqType: ReqType!
 *
 *     """
 *     ID to the collection to which to import into (null if to import into the root of the user)
 *     """
 *     parentCollectionID: ID
 *   ): Boolean!
 *
 *   """Update a UserCollection"""
 *   updateUserCollection(
 *     """ID of the user collection"""
 *     userCollectionID: ID!
 *
 *     """The updated title of the user collection"""
 *     newTitle: String
 *
 *     """JSON string representing the collection data"""
 *     data: String
 *   ): UserCollection!
 *
 */

    @Autowired
    private UserCollectionService userCollectionService;
    @Autowired
    private AuthUserUtil authUserUtil;
    @DgsMutation
    public UserCollectionModel createRESTRootUserCollection(DgsDataFetchingEnvironment dfe, @InputArgument("title") String title, @InputArgument("data") String data){
        return createRESTUserCollection(dfe, title, data, null);
    }

    @DgsMutation
    public UserCollectionModel createRESTChildUserCollection(DgsDataFetchingEnvironment dfe, @InputArgument("title") String title, @InputArgument("parentUserCollectionID") String parentUserCollectionID){
        return createRESTUserCollection(dfe, title, null, parentUserCollectionID);
    }

    private UserCollectionModel createRESTUserCollection(DgsDataFetchingEnvironment dfe, String title, String data, String parentUserCollectionID){
        UserModel userModel = authUserUtil.getAuthUser(dfe);
        String userId = userModel.getId();
        // Validate title length
        if (StringUtils.isBlank(title)){
            throw new IllegalArgumentException("Title is too short");
        }
        // Validate data
        if (data != null) {
            if (data.trim().isEmpty()){
                throw new IllegalArgumentException("Data is invalid");
            }
            ObjectMapper mapper = new ObjectMapper();
            try {
                // 将JSON字符串转换为Map
                Map<String, Object> map = mapper.readValue(data, Map.class);
            } catch (IOException e) {
                throw new IllegalArgumentException("Data is invalid", e);
            }
        }

        // If creating a child collection
        if (!StringUtils.isBlank(parentUserCollectionID)){
            UserCollectionModel parentUserCollection = userCollectionService.getById(parentUserCollectionID);
            Optional.ofNullable(parentUserCollection).orElseThrow(() -> new IllegalArgumentException("parentUserCollection does not exist"));
            if (!parentUserCollection.getUserId().equals(userId)){
                throw new IllegalArgumentException("user_coll/user_not_owner");
            }
            if (!ReqType.REST.name().equals(parentUserCollection.getType())){
                throw new IllegalArgumentException("user_coll/type_mismatch");
            }
        }
        // 计算orderIndex
        LambdaQueryWrapper<UserCollectionModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        int orderIndex = 0;
        if (StringUtils.isBlank(parentUserCollectionID)){
            lambdaQueryWrapper.eq(UserCollectionModel::getUserId, userId).isNull(UserCollectionModel::getParentId);
            orderIndex = userCollectionService.count(lambdaQueryWrapper) + 1;
        }else {
            lambdaQueryWrapper.eq(UserCollectionModel::getParentId, parentUserCollectionID);
            orderIndex = userCollectionService.count(lambdaQueryWrapper) + 1;
        }
        // Create the user collection
        UserCollectionModel userCollectionModel = UserCollectionModel.builder()
                .userId(userId)
                .title(title)
                .orderIndex(orderIndex)
                .type(ReqType.REST.name())
                .data(data)
                .parentId(parentUserCollectionID)
                .build();
        userCollectionService.create(userCollectionModel);
        return userCollectionModel;
    }

    @DgsMutation
    public boolean deleteUserCollection(DgsDataFetchingEnvironment dfe, @InputArgument("userCollectionID") String userCollectionID){
        UserCollectionModel userCollectionModel = userCollectionService.getById(userCollectionID);
        Optional.ofNullable(userCollectionModel).orElseThrow(() -> new IllegalArgumentException("user_coll/not_found"));
        UserModel userModel = authUserUtil.getAuthUser(dfe);
        String userId = userModel.getId();
        if (!userId.equals(userCollectionModel.getUserId())){
            throw new IllegalArgumentException("user_coll/user_not_owner");
        }
        return userCollectionService.delete(userCollectionID);
    }

}
