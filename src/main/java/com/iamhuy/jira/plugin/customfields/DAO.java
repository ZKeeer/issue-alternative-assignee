package com.iamhuy.jira.plugin.customfields;

import org.apache.log4j.Logger;
import org.ofbiz.core.entity.DelegatorInterface;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.core.ofbiz.util.OFBizPropertyUtils;
import com.atlassian.core.util.map.EasyMap;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.opensymphony.module.propertyset.PropertySet;

public class DAO {

    public static final Logger log = Logger.getLogger(DAO.class);

    @SuppressWarnings("unchecked")
    public static String retrieveRole(FieldConfig fieldConfig) {
        DelegatorInterface gd = ComponentAccessor.getComponentOfType(DelegatorInterface.class);

        Long fieldConfigSchemeId = getFieldConfigSchemeId(fieldConfig);

        try {
            GenericValue genericValue = gd.findByPrimaryKey("FieldConfigScheme", EasyMap.build("id", fieldConfigSchemeId));
            PropertySet propertySet = OFBizPropertyUtils.getPropertySet(genericValue);
            return propertySet.getString("role");
        } catch (GenericEntityException e) {
            log.debug ("No config found");
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static void updateRole(FieldConfig fieldConfig, String role) {
        DelegatorInterface gd = ComponentAccessor.getComponentOfType(DelegatorInterface.class);
        Long fieldConfigSchemeId = getFieldConfigSchemeId(fieldConfig);

        try {
            PropertySet propertySet = OFBizPropertyUtils.getPropertySet(gd.findByPrimaryKey("FieldConfigScheme", EasyMap.build("id", fieldConfigSchemeId)));
            propertySet.setString("role", role);
        } catch (GenericEntityException e) {
            log.debug ("No config found");
        }
    }

    private static Long getFieldConfigSchemeId(FieldConfig fieldConfig) {
        FieldConfigSchemeManager fieldConfigSchemeManager = ComponentAccessor.getComponent(FieldConfigSchemeManager.class);

        FieldConfigScheme fieldConfigScheme = fieldConfigSchemeManager.getConfigSchemeForFieldConfig(fieldConfig);
        return fieldConfigScheme.getId();
    }
}
