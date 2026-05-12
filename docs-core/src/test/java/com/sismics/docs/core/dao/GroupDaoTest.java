package com.sismics.docs.core.dao;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.model.jpa.Group;
import org.junit.Assert;
import org.junit.Test;

public class GroupDaoTest extends BaseTransactionalTest {
    @Test
    public void testCreateAndGetActiveByName() {
        GroupDao groupDao = new GroupDao();
        Group group = new Group();
        group.setName("TestGroup");
        group.setRoleId("role1");
        group.setParentId(null);

        String groupId = groupDao.create(group, "testUser");
        Assert.assertNotNull(groupId);

        Group fetched = groupDao.getActiveByName("TestGroup");
        Assert.assertNotNull(fetched);
        Assert.assertEquals("TestGroup", fetched.getName());
        Assert.assertNull(fetched.getDeleteDate());
    }

    @Test
    public void testCreateAndDelete() {
        GroupDao groupDao = new GroupDao();
        Group group = new Group();
        group.setName("ToDeleteGroup");
        group.setRoleId("role2");
        group.setParentId(null);

        String groupId = groupDao.create(group, "testUser");
        Assert.assertNotNull(groupId);

        groupDao.delete(groupId, "testUser");
        Group deleted = groupDao.getActiveById(groupId);
        Assert.assertNull(deleted);
    }
}
