

package com.heiduc.business.impl.imex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.zip.ZipEntry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.heiduc.business.decorators.TreeItemDecorator;
import com.heiduc.business.imex.ConfigExporter;
import com.heiduc.business.imex.FolderExporter;
import com.heiduc.business.imex.FormExporter;
import com.heiduc.business.imex.GroupExporter;
import com.heiduc.business.imex.MessagesExporter;
import com.heiduc.business.imex.PageDependencyExporter;
import com.heiduc.business.imex.PageExporter;
import com.heiduc.business.imex.PluginExporter;
import com.heiduc.business.imex.ResourceExporter;
import com.heiduc.business.imex.SeoUrlExporter;
import com.heiduc.business.imex.SiteExporter;
import com.heiduc.business.imex.StructureExporter;
import com.heiduc.business.imex.TagExporter;
import com.heiduc.business.imex.ThemeExporter;
import com.heiduc.business.imex.UserExporter;
import com.heiduc.business.imex.task.TaskTimeoutException;
import com.heiduc.business.imex.task.ZipOutStreamTaskAdapter;
import com.heiduc.dao.DaoTaskException;
import com.heiduc.entity.FolderEntity;
import com.heiduc.utils.FolderUtil;

public class SiteExporterImpl extends AbstractExporter 
		implements SiteExporter {

	public SiteExporterImpl(ExporterFactoryImpl factory) {
		super(factory);
	}

	public boolean isSiteContent(final ZipEntry entry)
			throws UnsupportedEncodingException {
		String[] chain = FolderUtil.getPathChain(entry);
		if (chain.length != 1 || !chain[0].equals("content.xml")) {
			return false;
		}
		return true;
	}

	public void exportSite(final ZipOutStreamTaskAdapter out) 
			throws IOException, TaskTimeoutException {
		if (!out.isSkip("_users.xml")) {
			saveFile(out, "_users.xml", getUserExporter().createUsersXML());
		}
		if (!out.isSkip("_groups.xml")) {
			saveFile(out, "_groups.xml", getGroupExporter().createGroupsXML());
		}
		if (!out.isSkip("_config.xml")) {
			saveFile(out, "_config.xml", getConfigExporter().createConfigXML());
		}
		if (!out.isSkip("_structures.xml")) {
			saveFile(out, "_structures.xml", getStructureExporter()
				.createStructuresXML());
		}
		if (!out.isSkip("_forms.xml")) {
			saveFile(out, "_forms.xml", getFormExporter().createFormsXML());
		}
		if (!out.isSkip("_messages.xml")) {
			saveFile(out, "_messages.xml", getMessagesExporter()
				.createMessagesXML());
		}
		if (!out.isSkip("_plugins.xml")) {
			saveFile(out, "_plugins.xml", getPluginExporter().createPluginsXML());
		}
		if (!out.isSkip("_tags.xml")) {
			saveFile(out, "_tags.xml", getTagExporter().createXML());
		}
		if (!out.isSkip("_seourls.xml")) {
			saveFile(out, "_seourls.xml", getSeoUrlExporter().createXML());
		}
		if (!out.isSkip("_dependencies.xml")) {
			saveFile(out, "_dependencies.xml", getPageDependencyExporter().createXML());
		}
		TreeItemDecorator<FolderEntity> page = getBusiness().getFolderBusiness()
				.findFolderByPath(getBusiness().getFolderBusiness().getTree(), 
						"/page");
		if (page != null) {
			getResourceExporter().addResourcesFromFolder(out, page, "page/");
		}
	}
	
	public void readSiteContent(final ZipEntry entry, final String xml)
			throws DocumentException, DaoTaskException {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
			Element element = i.next();
			if (element.getName().equals("config")) {
				getConfigExporter().readConfigs(element);
			}
			if (element.getName().equals("pages")) {
				getPageExporter().readPages(element);
			}
			if (element.getName().equals("forms")) {
				getFormExporter().readForms(element);
			}
			if (element.getName().equals("users")) {
				getUserExporter().readUsers(element);
			}
			if (element.getName().equals("groups")) {
				getGroupExporter().readGroups(element);
			}
			if (element.getName().equals("folders")) {
				getFolderExporter().readFolders(element);
			}
			if (element.getName().equals("messages")) {
				getMessagesExporter().readMessages(element);
			}
			if (element.getName().equals("structures")) {
				getStructureExporter().readStructures(element);
			}
			if (element.getName().equals("plugins")) {
				getPluginExporter().readPlugins(element);
			}
		}
	}

	private PluginExporter getPluginExporter() {
		return getExporterFactory().getPluginExporter();
	}

	private MessagesExporter getMessagesExporter() {
		return getExporterFactory().getMessagesExporter();
	}

	private StructureExporter getStructureExporter() {
		return getExporterFactory().getStructureExporter();
	}

	private FolderExporter getFolderExporter() {
		return getExporterFactory().getFolderExporter();
	}

	private UserExporter getUserExporter() {
		return getExporterFactory().getUserExporter();
	}

	private GroupExporter getGroupExporter() {
		return getExporterFactory().getGroupExporter();
	}

	private ConfigExporter getConfigExporter() {
		return getExporterFactory().getConfigExporter();
	}
	
	private PageExporter getPageExporter() {
		return getExporterFactory().getPageExporter();
	}

	private FormExporter getFormExporter() {
		return getExporterFactory().getFormExporter();
	}

	private ResourceExporter getResourceExporter() {
		return getExporterFactory().getResourceExporter();
	}

	private ThemeExporter getThemeExporter() {
		return getExporterFactory().getThemeExporter();
	}

	private TagExporter getTagExporter() {
		return getExporterFactory().getTagExporter();
	}

	private SeoUrlExporter getSeoUrlExporter() {
		return getExporterFactory().getSeoUrlExporter();
	}

	private PageDependencyExporter getPageDependencyExporter() {
		return getExporterFactory().getPageDependencyExporter();
	}

	private String toXML(ByteArrayOutputStream data) 
			throws UnsupportedEncodingException {
		return data.toString("UTF-8");
	}
	
	public boolean importSystemFile(ZipEntry entry, ByteArrayOutputStream data) 
			throws DocumentException, DaoTaskException, 
			UnsupportedEncodingException {
		return importSystemFile(entry.getName(), toXML(data));
	}
	
	public boolean importSystemFile(String name, String xml) 
			throws DocumentException, DaoTaskException, 
			UnsupportedEncodingException {
		if (name.equals("_users.xml")) {
			getUserExporter().readUsersFile(xml);
			return true;
		}
		if (name.equals("_groups.xml")) {
			getGroupExporter().readGroupsFile(xml);
			return true;
		}
		if (name.equals("_config.xml")) {
			getConfigExporter().readConfigFile(xml);
			return true;
		}
		if (name.equals("_structures.xml")) {
			getStructureExporter().readStructuresFile(xml);
			return true;
		}
		if (name.equals("_forms.xml")) {
			getFormExporter().readFormsFile(xml);
			return true;
		}
		if (name.equals("_messages.xml")) {
			getMessagesExporter().readMessagesFile(xml);
			return true;
		}
		if (name.equals("_plugins.xml")) {
			getPluginExporter().readPluginsFile(xml);
			return true;
		}
		if (name.equals("_tags.xml")) {
			getTagExporter().read(xml);
			return true;
		}
		if (name.equals("_seourls.xml")) {
			getSeoUrlExporter().read(xml);
			return true;
		}
		if (name.equals("_dependencies.xml")) {
			getPageDependencyExporter().readFile(xml);
			return true;
		}
		
		if (name.endsWith("_folder.xml")) {
			String folderPath = FolderUtil.getFilePath("/" + name);
			getResourceExporter().readFolderFile(folderPath, xml);
			return true;
		}
		if (name.endsWith("_template.xml")) {
			return getThemeExporter().readTemplateFile("/" + name, xml);
		}
		if (name.endsWith("_content.xml")) {
			String folderPath = FolderUtil.getFilePath("/" + name);
			return getPageExporter().readContentFile(folderPath, xml);
		}
		if (name.endsWith("_comments.xml")) {
			String folderPath = FolderUtil.getFilePath("/" + name);
			return getPageExporter().readCommentsFile(folderPath, xml);
		}
		if (name.endsWith("_permissions.xml")) {
			String folderPath = FolderUtil.getFilePath("/" + name);
			return getPageExporter().readPermissionsFile(folderPath, xml);
		}
		if (name.endsWith("_tag.xml")) {
			String folderPath = FolderUtil.getFilePath("/" + name);
			return getPageExporter().readPageTagFile(folderPath, xml);
		}
		return false;
	}

	
	
}
