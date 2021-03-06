

package com.heiduc.business.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.heiduc.business.StructureTemplateBusiness;
import com.heiduc.entity.PageEntity;
import com.heiduc.entity.StructureTemplateEntity;
import com.heiduc.i18n.Messages;

/**
 * @author Alexander Oleynik
 */
public class StructureTemplateBusinessImpl extends AbstractBusinessImpl 
	implements StructureTemplateBusiness {

	@Override
	public List<String> validateBeforeUpdate(
			final StructureTemplateEntity entity) {
		List<String> errors = new ArrayList<String>();
		StructureTemplateEntity foundStructure = getDao().getStructureTemplateDao()
				.getByName(entity.getName());
		if (entity.getId() == null) {
			if (foundStructure != null) {
				errors.add(Messages.get("structureTemplate.already_exists"));
			}
		}
		else {
			if (foundStructure != null 
				&& !foundStructure.getId().equals(entity.getId())) {
				errors.add(Messages.get("structureTemplate.already_exists"));
			}
		}
		if (StringUtils.isEmpty(entity.getTitle())) {
			errors.add(Messages.get("title_is_empty"));
		}
		if (StringUtils.isEmpty(entity.getName())) {
			errors.add(Messages.get("name_is_empty"));
		}
		return errors;
	}
	
	@Override
	public List<String> remove(List<Long> ids) {
		List<String> result = new ArrayList<String>();
		for (Long id : ids) {
			StructureTemplateEntity entity = getDao().getStructureTemplateDao()
					.getById(id);
			if (entity == null) {
				continue;
			}
			List<PageEntity> pages = getDao().getPageDao()
					.selectByStructureTemplate(id);
			if (pages.size() > 0) {
				result.add(Messages.get("structureTemplate.has_references", 
						entity.getTitle(), pages.get(0).getFriendlyURL()));
			}
			else {
				getDao().getStructureTemplateDao().remove(id);
			}
		}	
		return result;
	}

	@Override
	public StructureTemplateEntity save(StructureTemplateEntity template) {
		Set<String> pages = new HashSet<String>();
		for (PageEntity page : getDao().getPageDao().selectByStructureTemplate(
				template.getId())) {
			pages.add(page.getFriendlyURL());
		}
		for (String url : pages) {
			getBusiness().getSystemService().getPageCache().remove(url);
		}
		return getDao().getStructureTemplateDao().save(template);
	}
	
	
}
