

package com.heiduc.dao;

import com.heiduc.dao.cache.EntityCache;
import com.heiduc.dao.cache.QueryCache;
import com.heiduc.global.SystemService;

public interface Dao {
	
	SystemService getSystemService();
	DaoStat getDaoStat();
	
	EntityCache getEntityCache();
	void setEntityCache(EntityCache entityCache);
	
	QueryCache getQueryCache();
	void setQueryCache(QueryCache queryCache);

	PageDao getPageDao();
	void setPageDao(final PageDao pageDao);
	
	FileDao getFileDao();
	void setFileDao(final FileDao fileDao);

	FileChunkDao getFileChunkDao();
	void setFileChunkDao(final FileChunkDao fileChunkDao);

	FolderDao getFolderDao();
	void setFolderDao(final FolderDao folderDao);

	UserDao getUserDao();
	void setUserDao(final UserDao userDao);

	TemplateDao getTemplateDao();
	void setTemplateDao(final TemplateDao templateDao);

	ConfigDao getConfigDao();
	void setConfigDao(final ConfigDao configDao);

	FormConfigDao getFormConfigDao();
	void setFormConfigDao(final FormConfigDao formConfigDao);
	
	FormDao getFormDao();
	void setFormDao(final FormDao formDao);

	FieldDao getFieldDao();
	void setFieldDao(final FieldDao bean);

	CommentDao getCommentDao();
	void setCommentDao(final CommentDao bean);

	SeoUrlDao getSeoUrlDao();
	void setSeoUrlDao(final SeoUrlDao bean);

	LanguageDao getLanguageDao();
	void setLanguageDao(final LanguageDao bean);

	ContentDao getContentDao();
	void setContentDao(final ContentDao bean);

	MessageDao getMessageDao();
	void setMessageDao(final MessageDao bean);

	GroupDao getGroupDao();
	void setGroupDao(final GroupDao bean);

	UserGroupDao getUserGroupDao();
	void setUserGroupDao(final UserGroupDao bean);

	ContentPermissionDao getContentPermissionDao();
	void setContentPermissionDao(final ContentPermissionDao bean);

	FolderPermissionDao getFolderPermissionDao();
	void setFolderPermissionDao(final FolderPermissionDao bean);

	StructureDao getStructureDao();
	void setStructureDao(final StructureDao bean);

	StructureTemplateDao getStructureTemplateDao();
	void setStructureTemplateDao(final StructureTemplateDao bean);

	PluginDao getPluginDao();
	void setPluginDao(final PluginDao bean);

	PluginResourceDao getPluginResourceDao();
	void setPluginResourceDao(final PluginResourceDao bean);
	
	TagDao getTagDao();
	void setTagDao(final TagDao bean);

	PageTagDao getPageTagDao();
	void setPageTagDao(final PageTagDao bean);

	FormDataDao getFormDataDao();
	void setFormDataDao(final FormDataDao bean);

	PageDependencyDao getPageDependencyDao();
	void setPageDependencyDao(final PageDependencyDao bean);
	
	PageAttributeDao getPageAttributeDao();
	void setPageAttributeDao(final PageAttributeDao bean);
	
	Oauth2Dao getOauth2Dao();
	void setOauth2Dao(final Oauth2Dao bean);

}
