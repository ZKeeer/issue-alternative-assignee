package scripts

import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.fields.config.FieldConfigScheme
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.customfields.option.Options
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager
import org.ofbiz.core.entity.GenericValue
import org.ofbiz.core.entity.DelegatorInterface
import org.ofbiz.core.entity.GenericPK
import com.atlassian.core.util.map.EasyMap
import com.atlassian.core.ofbiz.util.OFBizPropertyUtils

Logger log = Logger.getLogger("Script");
log.setLevel(Level.DEBUG)

log.debug("bollo")

ComponentManager componentManager = ComponentManager.getInstance()
CustomFieldManager customFieldManager = componentManager.getCustomFieldManager()
OptionsManager optionsManager = ComponentManager.getComponentInstanceOfType(OptionsManager.class)

FieldConfigSchemeManager fieldConfigSchemeManager = ComponentManager.getComponentInstanceOfType(FieldConfigSchemeManager.class)
log.debug fieldConfigSchemeManager
DelegatorInterface gd = (DelegatorInterface) componentManager.getComponentInstanceOfType(DelegatorInterface.class)
// log.debug OFBizPropertyUtils.getPropertySet(gd.findByPrimaryKey("FieldConfigScheme", EasyMap.build("id", 10781L)))

// return

Class<?> jupClazz = componentManager.getPluginAccessor().getDynamicPluginClass("com.iamhuy.jira.plugin.customfields.UserPickerProjectRole")
log.debug jupClazz

customFieldManager.getCustomFieldObjects().each {CustomField cf ->
    if (cf.getCustomFieldType().getClass() == jupClazz) {
        List<FieldConfigScheme> schemes = cf.getConfigurationSchemes()
        log.debug schemes
        schemes.each {FieldConfigScheme scheme ->
            // scheme.getO
            Options options = optionsManager.getOptions(scheme.getOneAndOnlyConfig())
            log.debug options
            if (! options.isEmpty()) {
                log.debug options?.getRootOptions()?.get(0)
            }


        }

        // get the root option if there is one and set it as the configured value
    }

}

