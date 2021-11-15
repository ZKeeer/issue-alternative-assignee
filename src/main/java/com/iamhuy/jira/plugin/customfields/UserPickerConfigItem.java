package com.iamhuy.jira.plugin.customfields;

import com.atlassian.core.util.map.EasyMap;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

public class UserPickerConfigItem implements FieldConfigItemType {
    public String getDisplayName() {
        return "Role";
    }

    public String getDisplayNameKey() {
        return "Selected role";
    }

    public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem) {
        return DAO.retrieveRole(fieldConfig);
    }

    public String getObjectKey() {
        return "jupconfig";
    }

    public Object getConfigurationObject(Issue issue, FieldConfig fieldConfig) {
        return EasyMap.build("role", DAO.retrieveRole(fieldConfig));
    }

    public String getBaseEditUrl() {
        return "EditRoleConfig.jspa";
    }
}
