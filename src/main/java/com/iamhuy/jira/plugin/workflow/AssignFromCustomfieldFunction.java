package com.iamhuy.jira.plugin.workflow;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.impl.UserCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.user.ApplicationUser;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

public class AssignFromCustomfieldFunction implements FunctionProvider {
    public static final Logger log = Logger.getLogger(AssignFromCustomfieldFunction.class);

	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue issue = (MutableIssue) transientVars.get("issue");
        IssueManager issueManager = ComponentAccessor.getIssueManager();
        ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser();
        String fieldId = (String) args.get("field.id");
        ApplicationUser assignee = null;
        assignee = getCustomfieldUser(issue, fieldId);
        ModifiedValue modifiedValue = (ModifiedValue) issue.getModifiedFields().get(fieldId);
        if (modifiedValue != null)
            assignee = getUserFromModifiedValue(modifiedValue);

        if (assignee != null) {
            issue.setAssigneeId(assignee.getKey());
            issueManager.updateIssue(currentUser, issue, EventDispatchOption.ISSUE_UPDATED,Boolean.FALSE);
        }
    }

    private ApplicationUser getUserFromModifiedValue(ModifiedValue modifiedValue) {
    	ApplicationUser assignee = null;

        if (modifiedValue.getNewValue() instanceof ApplicationUser) {
            assignee = (ApplicationUser) modifiedValue.getNewValue();
        } else if (modifiedValue.getNewValue() instanceof String) {
            String username = (String) modifiedValue.getNewValue();
            if (username != null) {
                try {
                    assignee = ComponentAccessor.getUserUtil().getUserByKey(username);
                } catch (Exception e) {
                    log.error("Error in getUserFromModifiedValue", e);
                }
            }
        }
        return assignee;
    }

    private ApplicationUser getCustomfieldUser(Issue issue, String fieldId) {
        CustomField cf = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(fieldId);
        ApplicationUser cfUserValue = null;
        try {
            if (cf.getCustomFieldType() instanceof UserCFType || cf.getValue(issue) instanceof ApplicationUser) {
                if (cf.getValue(issue) != null) {
                    cfUserValue = ComponentAccessor.getUserUtil().getUserByKey(((ApplicationUser) cf.getValue(issue)).getKey());
                } else if (cf.getDefaultValue(issue) != null) {
                    ApplicationUser defaultValue = (ApplicationUser) cf.getDefaultValue(issue);
                    if (defaultValue != null) {
                        cfUserValue = ComponentAccessor.getUserUtil().getUserByKey(defaultValue.getKey());
                    }
                    else {
                        cfUserValue = null;
                    }
                }
            } else {
                String username = (String) cf.getValue(issue);
                if (username != null)
                    cfUserValue = ComponentAccessor.getUserUtil().getUserByKey(username);
            }
        } catch (Exception e) {
            log.error("Error in getCustomfieldUser", e);
        }
        return cfUserValue;
    }
}
