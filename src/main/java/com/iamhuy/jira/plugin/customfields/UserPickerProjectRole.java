package com.iamhuy.jira.plugin.customfields;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.config.FeatureManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.UserConverter;
import com.atlassian.jira.issue.customfields.impl.UserCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.UserBeanFactory;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.notification.type.UserCFNotificationTypeAware;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.template.soy.SoyTemplateRendererProvider;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserFilterManager;
import com.atlassian.jira.user.UserHistoryManager;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.I18nHelper;

import javax.annotation.Nonnull;

public class UserPickerProjectRole extends UserCFType implements UserCFNotificationTypeAware {

    public UserPickerProjectRole(CustomFieldValuePersister customFieldValuePersister, UserConverter userConverter, GenericConfigManager genericConfigManager, ApplicationProperties applicationProperties, JiraAuthenticationContext authenticationContext, FieldConfigSchemeManager fieldConfigSchemeManager, ProjectManager projectManager, SoyTemplateRendererProvider soyTemplateRendererProvider, GroupManager groupManager, ProjectRoleManager projectRoleManager, UserSearchService searchService, JiraBaseUrls jiraBaseUrls, UserHistoryManager userHistoryManager, UserFilterManager userFilterManager, I18nHelper i18nHelper, UserBeanFactory userBeanFactory, FeatureManager featureManager) {
        super(customFieldValuePersister, userConverter, genericConfigManager, applicationProperties, authenticationContext, fieldConfigSchemeManager,
                projectManager, soyTemplateRendererProvider, groupManager, projectRoleManager, searchService, jiraBaseUrls,
                userHistoryManager, userFilterManager, i18nHelper, userBeanFactory, featureManager);
    }

    /**
     * This is where the different aspects of a custom field such as
     * having a default value or other config items are set.
     */
    @Override
    @Nonnull
    public List<FieldConfigItemType> getConfigurationItemTypes() {
        final List<FieldConfigItemType> configurationItemTypes = super.getConfigurationItemTypes();
        configurationItemTypes.add(new UserPickerConfigItem());
        return configurationItemTypes;
    }


    public ArrayList<ApplicationUser> getUserListForRole(Issue issue, String projectRoleName) {
        final ArrayList<ApplicationUser> userListForRole = new ArrayList<ApplicationUser>();
        if (issue != null && issue.getProjectObject() != null) {
            // If the options list of this custom field is not empty
            if (projectRoleName != null && !projectRoleName.equals("")) {
                ProjectRoleManager projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager.class);
                ProjectRole projectRole = projectRoleManager.getProjectRole(projectRoleName);

                // If the name of project role is valid
                if (projectRole != null) {
                    ProjectRoleActors projectRoleActors = projectRoleManager.getProjectRoleActors(projectRole, issue.getProjectObject());
                    userListForRole.addAll(projectRoleActors.getApplicationUsers());
                }
                // sort users by name
                final Locale locale = ComponentAccessor.getApplicationProperties().getDefaultLocale();
                java.util.Collections.sort(userListForRole, new UserDisplayNameComparator(locale));
            }
        }
        return userListForRole;
    }


    /**
     * Check if the CustomFieldParams of Strings is a valid representation of the Custom Field values.
     * In this case, it should return without adding any errors to errorCollectionToAddTo
     *
     * @param relevantParams
     * @param errorCollectionToAddTo
     * @param config
     */
    @Override
    public void validateFromParams(CustomFieldParams relevantParams, ErrorCollection errorCollectionToAddTo, FieldConfig config) {

    }

    /**
     * Adds the parameters required for displaying custom field
     * Map that the framework passes to the Velocity template.
     *
     * @param issue : current issue
     * @return A map of the parameters required to display the issue calendar.
     */
    @Override
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
        Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);
        map.put("customFieldObject", this);
        map.put("issueObject", issue);
        return map;
    }
}
