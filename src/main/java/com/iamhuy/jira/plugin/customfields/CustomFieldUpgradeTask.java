package com.iamhuy.jira.plugin.customfields;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;

public class CustomFieldUpgradeTask implements PluginUpgradeTask {

    public static final Logger log = Logger.getLogger(DAO.class);

    public int getBuildNumber() {
        return 7;
    }

    public String getShortDescription() {
        return "Upgrades JUP custom field instances to use CF configurations";
    }

    public Collection<Message> doUpgrade() throws Exception {
        log.warn("Upgrading issue-alternative-assignee");

        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        OptionsManager optionsManager = ComponentAccessor.getOptionsManager();

        for (CustomField customField : customFieldManager.getCustomFieldObjects()) {
            if (customField.getCustomFieldType() instanceof UserPickerProjectRole) {
                List<FieldConfigScheme> schemes = customField.getConfigurationSchemes();

                for (FieldConfigScheme scheme : schemes) {
                    Options options = optionsManager.getOptions(scheme.getOneAndOnlyConfig());

                    if (!options.isEmpty()) {
                        List rootOptions = options.getRootOptions();
                        if (! rootOptions.isEmpty()) {
                            log.info("Converting custom field: " + customField.getName() + " field config: " + scheme.getName());
                            Object o = rootOptions.get(0);
                            if (o instanceof Option) {
                                DAO.updateRole(scheme.getOneAndOnlyConfig(), ((Option) o).getValue());
                            }
                            else {
                                DAO.updateRole(scheme.getOneAndOnlyConfig(), (String) o);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String getPluginKey() {
        return "com.iamhuy.jira.plugin.issue-alternative-assignee";
    }
}
