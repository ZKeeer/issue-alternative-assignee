package com.iamhuy.jira.plugin.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

public class ChooseCustomfieldPluginFactory extends AbstractWorkflowPluginFactory implements
    WorkflowPluginFunctionFactory {
    private final FieldManager fieldManager;

    public ChooseCustomfieldPluginFactory(FieldManager fieldManager) {
        this.fieldManager = fieldManager;
    }

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
        final Map<String,String> fieldMap = new HashMap<String, String>();
        try {
            final List<CustomField> customFields = new ArrayList<CustomField>(ComponentAccessor.getCustomFieldManager().getCustomFieldObjects());
            for (CustomField customField : customFields) {
            	 fieldMap.put(customField.getId(), customField.getNameKey());
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        velocityParams.put("fields", sortByValue(fieldMap));
    }

    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        } else {
            FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
            velocityParams.put("fieldId", functionDescriptor.getArgs().get("field.id"));
            return;
        }
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        } else {
            FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
            velocityParams.put("fieldId", fieldManager.getCustomField(
                (String) functionDescriptor.getArgs().get("field.id")).getNameKey());
            return;
        }
    }

	public Map<String, ?> getDescriptorParams(Map<String, Object> conditionParams) {
		Map<String, Object> params = new HashMap<String, Object>();
        String fieldId = extractSingleParam(conditionParams, "fieldId");
        params.put("field.id", fieldId);
        return params;
    }

    @SuppressWarnings("unchecked")
	static Map sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
