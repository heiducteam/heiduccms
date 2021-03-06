package com.heiduc.business.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.dom4j.DocumentException;

import com.heiduc.business.ImportExportBusiness;
import com.heiduc.business.decorators.TreeItemDecorator;
import com.heiduc.business.imex.ExporterFactory;
import com.heiduc.business.imex.ResourceExporter;
import com.heiduc.business.imex.SiteExporter;
import com.heiduc.business.imex.StructureExporter;
import com.heiduc.business.imex.ThemeExporter;
import com.heiduc.business.imex.task.DaoTaskAdapter;
import com.heiduc.business.imex.task.TaskTimeoutException;
import com.heiduc.business.imex.task.ZipOutStreamTaskAdapter;
import com.heiduc.business.impl.imex.ExporterFactoryImpl;
import com.heiduc.business.impl.imex.task.DaoTaskAdapterImpl;
import com.heiduc.business.mq.Topic;
import com.heiduc.business.mq.message.SimpleMessage;
import com.heiduc.common.RequestTimeoutException;
import com.heiduc.common.VfsNode;
import com.heiduc.dao.Dao;
import com.heiduc.dao.DaoTaskException;
import com.heiduc.entity.FolderEntity;
import com.heiduc.entity.StructureEntity;
import com.heiduc.entity.TemplateEntity;
import com.heiduc.utils.FolderUtil;

public class ImportExportBusinessImpl extends AbstractBusinessImpl implements ImportExportBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DaoTaskAdapter daoTaskAdapter;
	private ExporterFactory exporterFactory;

	public ExporterFactory getExporterFactory() {
		if (exporterFactory == null) {
			exporterFactory = new ExporterFactoryImpl(getBusiness(), getDaoTaskAdapter());
		}
		return exporterFactory;
	}

	private ThemeExporter getThemeExporter() {
		return getExporterFactory().getThemeExporter();
	}

	private StructureExporter getStructureExporter() {
		return getExporterFactory().getStructureExporter();
	}

	private ResourceExporter getResourceExporter() {
		return getExporterFactory().getResourceExporter();
	}

	private SiteExporter getSiteExporter() {
		return getExporterFactory().getSiteExporter();
	}

	@Override
	public void createTemplateExportFile(final ZipOutStreamTaskAdapter zip, final List<TemplateEntity> list, final List<StructureEntity> structures) throws IOException, TaskTimeoutException {
		getThemeExporter().exportThemes(zip, list);
		getStructureExporter().exportStructures(zip, structures);
	}

	public void importZip(ZipInputStream in) throws IOException, DocumentException, DaoTaskException {

		List<String> result = new ArrayList<String>();
		ZipEntry entry;
		byte[] buffer = new byte[4096];
		boolean skipping = getDaoTaskAdapter().getCurrentFile() != null;
		while ((entry = in.getNextEntry()) != null) {
			if (skipping) {
				if (entry.getName().equals(getDaoTaskAdapter().getCurrentFile())) {
					skipping = false;
				} else {
					continue;
				}
			}
			getDaoTaskAdapter().setCurrentFile(entry.getName());
			getDaoTaskAdapter().nextFile();
			if (entry.isDirectory()) {
				getBusiness().getFolderBusiness().createFolder("/" + entry.getName());
			} else {
				ByteArrayOutputStream data = new ByteArrayOutputStream();
				int len = 0;
				while ((len = in.read(buffer)) > 0) {
					data.write(buffer, 0, len);
				}
				if (getSiteExporter().isSiteContent(entry)) {
					getSiteExporter().readSiteContent(entry, data.toString("UTF-8"));
				} else if (getThemeExporter().isThemeDescription(entry)) {
					getThemeExporter().createThemeByDescription(entry, data.toString("UTF-8"));
				} else if (getThemeExporter().isThemeContent(entry)) {
					getThemeExporter().createThemeByContent(entry, data.toString("UTF-8"));
				} else {
					result.add(getResourceExporter().importResourceFile(entry, data.toByteArray()));
				}
			}
			getDaoTaskAdapter().resetCounters();
		}
		clearResourcesCache(result);
	}

	private void clearResourcesCache(List<String> files) {
		for (String file : files) {
			getBusiness().getSystemService().getFileCache().remove(file);
			LOGGER.debug("Clear cache " + file);
		}
	}

	@Override
	public void createSiteExportFile(ZipOutStreamTaskAdapter out) throws IOException, TaskTimeoutException {
		exportRootFolder(out);
		List<TemplateEntity> list = getDao().getTemplateDao().select();
		getThemeExporter().exportThemes(out, list);
		getSiteExporter().exportSite(out);
	}

	private void exportRootFolder(ZipOutStreamTaskAdapter out) throws IOException, TaskTimeoutException {
		FolderEntity root = getDao().getFolderDao().getByPath("/");
		if (root == null) {
			LOGGER.error("Folder not found: /");
		} else {
			getResourceExporter().addFolder(out, root, "");
		}
	}

	@Override
	public void createExportFile(final ZipOutStreamTaskAdapter out, FolderEntity folder) throws IOException, TaskTimeoutException {
		TreeItemDecorator<FolderEntity> root = getBusiness().getFolderBusiness().getTree();
		TreeItemDecorator<FolderEntity> exportFolder = root.find(folder);
		if (exportFolder != null) {
			String zipPath = removeRootSlash(getBusiness().getFolderBusiness().getFolderPath(folder, root)) + "/";
			if ("/".equals(zipPath)) {
				zipPath = "";
			}
			getResourceExporter().addResourcesFromFolder(out, exportFolder, zipPath);
		} else {
			LOGGER.error("folder decorator was not found " + folder.getName());
		}
	}

	private String removeRootSlash(final String path) {
		if (path.length() > 0 && path.charAt(0) == '/') {
			return path.substring(1);
		}
		return path;
	}

	public Dao getDao() {
		return getBusiness().getDao();
	}

	public DaoTaskAdapter getDaoTaskAdapter() {
		if (daoTaskAdapter == null) {
			daoTaskAdapter = new DaoTaskAdapterImpl();
		}
		return daoTaskAdapter;
	}

	public void setDaoTaskAdapter(DaoTaskAdapter daoTaskAdapter) {
		this.daoTaskAdapter = daoTaskAdapter;
	}

	@Override
	public void createFullExportFile(final ZipOutStreamTaskAdapter out) throws IOException, TaskTimeoutException {
		exportRootFolder(out);
		List<TemplateEntity> list = getDao().getTemplateDao().select();
		getThemeExporter().exportThemes(out, list);
		getSiteExporter().exportSite(out);
		exportResources(out);
	}

	private void exportResources(ZipOutStreamTaskAdapter out) throws IOException, TaskTimeoutException {
		TreeItemDecorator<FolderEntity> root = getBusiness().getFolderBusiness().getTree();
		for (TreeItemDecorator<FolderEntity> child : root.getChildren()) {
			if (!isSkipFolder(child.getEntity().getName())) {
				getResourceExporter().addResourcesFromFolder(out, child, child.getEntity().getName() + "/");
			}
		}
	}

	private static String[] skipFolder = { "page", "theme", "tmp", "plugins" };

	private boolean isSkipFolder(String path) {
		for (String s : skipFolder) {
			if (path.startsWith(s)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void createResourcesExportFile(final ZipOutStreamTaskAdapter out) throws IOException, TaskTimeoutException {
		exportResources(out);
	}

	public void importZip2(ZipInputStream in) throws IOException, DocumentException, DaoTaskException {

		List<String> result = new ArrayList<String>();
		ZipEntry entry;
		byte[] buffer = new byte[4096];
		boolean skipping = getDaoTaskAdapter().getCurrentFile() != null;
		while ((entry = in.getNextEntry()) != null) {
			if (skipping) {
				if (entry.getName().equals(getDaoTaskAdapter().getCurrentFile())) {
					skipping = false;
				} else {
					continue;
				}
			}
			getDaoTaskAdapter().setCurrentFile(entry.getName());
			getDaoTaskAdapter().nextFile();
			if (!entry.isDirectory()) {
				ByteArrayOutputStream data = new ByteArrayOutputStream();
				int len = 0;
				while ((len = in.read(buffer)) > 0) {
					data.write(buffer, 0, len);
				}
				if (!getSiteExporter().importSystemFile(entry, data)) {
					result.add(getResourceExporter().importResourceFile(entry, data.toByteArray()));
				}
			}
			getDaoTaskAdapter().resetCounters();
		}
		clearResourcesCache(result);
	}

	@Override
	public void importUnzip(ZipInputStream in, String currentFile) throws IOException, RequestTimeoutException {
		ZipEntry entry;
		byte[] buffer = new byte[4096];
		boolean skipping = currentFile != null;
		if (!skipping) {
			VfsNode.reset();
		}
		while ((entry = in.getNextEntry()) != null) {
			if (skipping) {
				if (entry.getName().equals(currentFile)) {
					skipping = false;
				} else {
					continue;
				}
			}
			if (getSystemService().getRequestCPUTimeSeconds() > 20) {
				throw new RequestTimeoutException(entry.getName());
			}
			if (entry.isDirectory()) {
				String name = FolderUtil.removeTrailingSlash(entry.getName());
				VfsNode.createDirectory("/" + name);
				checkTheme(name);
			} else {
				ByteArrayOutputStream data = new ByteArrayOutputStream();
				int len = 0;
				while ((len = in.read(buffer)) > 0) {
					data.write(buffer, 0, len);
				}
				if (isGlobalSequenceImportFile(entry.getName())) {
					try {
						getSiteExporter().importSystemFile(entry, data);
					} catch (DaoTaskException e) {
						e.printStackTrace();
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				} else {
					VfsNode.createFile("/" + entry.getName(), data.toByteArray());
				}
			}
		}
		getBusiness().getMessageQueue().publish(new SimpleMessage(Topic.IMPORT_FOLDER, "/"));
		LOGGER.info("Unzip finished.");
	}

	private void checkTheme(String path) {
		if (path.startsWith("theme/")) {
			String[] parts = path.split("/");
			if (parts.length == 2) {
				String url = parts[1];
				TemplateEntity template = getDao().getTemplateDao().getByUrl(url);
				if (template == null) {
					template = new TemplateEntity(url, "", url);
					getDao().getTemplateDao().save(template);
				}
			}
		}
	}

	private static final String[] FILES_TO_IMPORT = { "_structures.xml", "_tags.xml" };

	public static boolean isGlobalSequenceImportFile(String name) {
		for (String file : FILES_TO_IMPORT) {
			if (file.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
