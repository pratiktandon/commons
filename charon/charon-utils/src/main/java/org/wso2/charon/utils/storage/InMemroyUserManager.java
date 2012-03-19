/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.utils.storage;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemroyUserManager implements UserManager {

    private String tenantDomain = null;
    private int tenantId = 0;

    //in memory user manager stores users
    ConcurrentHashMap<String, User> inMemoryUserList = new ConcurrentHashMap<String, User>();
    ConcurrentHashMap<String, Group> inMemoryGroupList = new ConcurrentHashMap<String, Group>();


    public InMemroyUserManager(int tenantId, String tenantDomain) {
        this.tenantId = tenantId;
        this.tenantDomain = tenantDomain;
    }

    /**
     * Obtains the user given the id.
     *
     * @param userId
     * @return
     */
    @Override
    public User getUser(String userId) throws CharonException {
        if (userId != null) {
            if (!inMemoryUserList.isEmpty() && inMemoryUserList.containsKey(userId)) {
                return inMemoryUserList.get(userId);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public List<User> listUsers() throws CharonException {
        if (!inMemoryUserList.isEmpty()) {
            List<User> returnedUsers = new ArrayList<User>();
            for (User user : inMemoryUserList.values()) {
                returnedUsers.add(user);
            }
            return returnedUsers;
        } else {
            throw new CharonException("User storage is empty");
        }
    }

    @Override
    public List<User> listUsersByAttribute(Attribute attribute) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<User> listUsersByFilter(String filter) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<User> listUsersBySort(String sortBy, String sortOrder) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<User> listUsersWithPagination(int startIndex, int count) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Update the user in full.
     *
     * @param user
     */
    @Override
    public User updateUser(User user) {
        //TODO:should set last modified date
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    /**
     * Update the user partially only with updated attributes.
     *
     * @param updatedAttributes
     */
    @Override
    public User updateUser(List<Attribute> updatedAttributes) {
        //TODO:should set last modified date
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    /**
     * Delete the user given the user id.
     *
     * @param userId
     */
    @Override
    public void deleteUser(String userId) throws NotFoundException, CharonException {
        if (userId != null) {
            if (!inMemoryUserList.isEmpty() && inMemoryUserList.containsKey(userId)) {
                validateGroupsOnUserDelete(inMemoryUserList.get(userId));
                inMemoryUserList.remove(userId);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * Create user with the given user object.
     *
     * @param user
     */
    @Override
    public User createUser(User user) throws CharonException {

        if (!inMemoryUserList.isEmpty()) {
            if (user.getExternalId() != null) {
                for (Map.Entry<String, User> userEntry : inMemoryUserList.entrySet()) {
                    if (user.getUserName().equals(userEntry.getValue().getUserName())) {
                        String error = "User already exist in the system.";
                        //TODO:log error
                        throw new CharonException(error);
                    }
                }
            }
            inMemoryUserList.put(user.getId(), user);
        } else {
            inMemoryUserList.put(user.getId(), user);
        }
        return user;
    }

    /**
     * ****************Group manipulation operations*******************
     */
    @Override
    public Group getGroup(String groupId) throws CharonException {
        if (groupId != null) {
            if (!inMemoryGroupList.isEmpty() && inMemoryGroupList.containsKey(groupId)) {
                return inMemoryGroupList.get(groupId);
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    @Override
    public Group createGroup(Group group) throws CharonException {
        if (!inMemoryGroupList.isEmpty()) {
            if (group.getExternalId() != null) {
                for (Group group1 : inMemoryGroupList.values()) {
                    if (group.getExternalId().equals(group1.getExternalId())) {
                        String error = "Group already exist in the system.";
                        //TODO:log error
                        throw new CharonException(error);
                    }
                }
            }
            validateMembersOnGroupCreate(group);
            inMemoryGroupList.put(group.getId(), group);
        } else {
            validateMembersOnGroupCreate(group);
            inMemoryGroupList.put(group.getId(), group);
        }
        return group;
    }

    @Override
    public Group updateGroup(Group group) throws CharonException {
        //TODO:should set last modified date
        return null;
    }

    @Override
    public Group updateGroup(List<Attribute> attributes) throws CharonException {
        //TODO:should set last modified date
        return null;
    }

    @Override
    public void deleteGroup(String groupId) throws NotFoundException, CharonException {

        if (groupId != null) {
            if (!inMemoryGroupList.isEmpty() && inMemoryGroupList.containsKey(groupId)) {
                validateMembersOnGroupDelete(inMemoryGroupList.get(groupId));
                inMemoryGroupList.remove(groupId);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * ****************private methods*************************************
     */

    private void validateMembersOnGroupCreate(Group group) throws CharonException {
        List<String> userIDs = group.getUserMembers();
        List<String> groupIDs = group.getGroupMembers();
        if (groupIDs != null && !groupIDs.isEmpty()) {
            for (String groupID : groupIDs) {
                if (!inMemoryGroupList.containsKey(groupID)) {
                    //throw exception
                    String error = "Group member: " + groupID + " doesn't exist in the system.";
                    throw new CharonException(error);
                }
            }
        }
        for (String userID : userIDs) {
            if (inMemoryUserList.containsKey(userID)) {
                User user = inMemoryUserList.get(userID);
                //update direct membership
                if (!user.isUserMemberOfGroup(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP, group.getId())) {
                    user.setGroup(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP, group.getId(), group.getDisplayName());
                    //TODO:set display name in group members taken from 
                }
            } else {
                //throw error.
                String error = "User member: " + userID + " doesn't exist in the system.";
                throw new CharonException(error);
            }
        }
    }

    private void validateMembersOnGroupDelete(Group group) throws CharonException {
        //get user members and remove direct membership from them
        List<String> userMembers = group.getUserMembers();
        if (userMembers != null && !userMembers.isEmpty()) {
            for (String userMember : userMembers) {
                if (inMemoryUserList.containsKey(userMember)) {
                    User user = inMemoryUserList.get(userMember);
                    if (user.isUserMemberOfGroup(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP,
                                                 group.getId())) {
                        user.removeFromGroup(group.getId());
                    }
                }
            }
        }

    }

    private void validateGroupsOnUserDelete(User user) throws CharonException {
        //get groups in which user is a direct member
        List<String> groupIds = user.getGroups();
        if (groupIds != null && !groupIds.isEmpty()) {
            for (String groupId : groupIds) {
                if (inMemoryGroupList.containsKey(groupId)) {
                    Group group = inMemoryGroupList.get(groupId);
                    group.removeUserMember(user.getId());
                }
            }
        }
    }

}
