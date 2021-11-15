package com.iamhuy.jira.plugin.customfields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;

@SuppressWarnings("serial")
public class EditConfiguration extends AbstractEditConfigurationItemAction {

	private String role;

    protected EditConfiguration(ManagedConfigurationItemService managedConfigurationItemService) {
        super(managedConfigurationItemService);
    }

    @Override
	protected String doExecute() throws Exception {
		if (getRole() == null) {
			setRole(DAO.retrieveRole(getFieldConfig()));
		}
		final String saveValue = getText("common.words.save");
		final String save = request.getParameter("Save");
		if (save != null && save.equals(saveValue)) {
			log.debug("write value to props");
            DAO.updateRole(getFieldConfig(), getRole());
			setReturnUrl(request.getContextPath() + "/secure/admin/ConfigureCustomField!default.jspa?customFieldId=" + getFieldConfig().getCustomField().getIdAsLong().toString());
			return getRedirect("not used");
		}
		return INPUT;
	}

	public List<String> getRoles() {
		ProjectRoleManager projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager.class);
		Collection<ProjectRole> projectRoles = projectRoleManager.getProjectRoles();
		
		final List<String> rt = new ArrayList<String>();
		for (ProjectRole projectRole : projectRoles) {
			rt.add(projectRole.getName());
		}
		return rt;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
