package me.rhysxia.niemandsland.server.repository.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("database.table.mapping")
public class TableNameMapping {
    private String permission = "sys_permission";
    private String role = "sys_role";
    private String rolePermissionRelation = "sys_role_permission";
    private String user = "sys_user";

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRolePermissionRelation() {
        return rolePermissionRelation;
    }

    public void setRolePermissionRelation(String rolePermissionRelation) {
        this.rolePermissionRelation = rolePermissionRelation;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
