

package com.heiduc.business.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


import com.google.gdata.client.Query;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.heiduc.business.PicasaBusiness;
import com.heiduc.common.HeiducContext;
import com.heiduc.entity.ConfigEntity;
import com.heiduc.i18n.Messages;
import com.heiduc.utils.FolderUtil;
import com.heiduc.utils.MimeType;

/**
 * 
 * @author Alexander Oleynik
 *
 */
public class PicasaBusinessImpl extends AbstractBusinessImpl 
	implements PicasaBusiness {

	private PicasawebService picasawebService;

	@Override
	public PicasawebService getPicasawebService() {
		if (picasawebService == null) {
			picasawebService = new PicasawebService("heiduc-cms");
			ConfigEntity config = HeiducContext.getInstance().getConfig();
			if (config.isEnablePicasa()) {
				try {
					picasawebService.setUserCredentials(config.getPicasaUser(),
						config.getPicasaPassword());
				}
				catch (AuthenticationException e) {
					LOGGER.error("Picasa auth problem. " + e.getMessage());
				}
			}
		}
		return picasawebService;
	}
	
	private String getUsername() {
		return HeiducContext.getInstance().getConfig().getPicasaUser();
	}

	private String getPicasaURL() {
		return "http://picasaweb.google.com/data/feed/api/user/" + getUsername();
	}

	private String getPicasaURL(String albumId) {
		return getPicasaURL() + "/albumid/" + albumId + "?imgmax=d";
	}
	
	@Override
	public List<AlbumEntry> selectAlbums() throws IOException, 
			ServiceException {
		URL feedUrl = new URL(getPicasaURL() + "?kind=album");
		UserFeed feed = getPicasawebService().getFeed(feedUrl, UserFeed.class);
		return feed.getAlbumEntries();
	}
	
	@Override
	public List<PhotoEntry> selectPhotos(String albumId) throws 
			MalformedURLException, IOException, ServiceException {
		AlbumFeed feed = getPicasawebService().getFeed(new URL(
				getPicasaURL(albumId)), AlbumFeed.class);
		return feed.getPhotoEntries();
	}
	
	@Override
	public AlbumEntry addAlbum(AlbumEntry album) throws MalformedURLException, 
			IOException, ServiceException {
		return getPicasawebService().insert(new URL(getPicasaURL()), album);
	}

	@Override
	public void removeAlbum(String albumId) throws IOException, ServiceException {
		for (AlbumEntry album : selectAlbums()) {
			if (album.getGphotoId().equals(albumId)) {
				album.delete();
				break;
			}
		}
	}

	@Override
	public void removePhoto(String albumId, String photoId) throws 
			MalformedURLException, IOException, ServiceException {
		for (PhotoEntry photo : selectPhotos(albumId)) {
			if (photo.getGphotoId().equals(photoId)) {
				photo.delete();
				break;
			}
		}
	}

	@Override
	public AlbumEntry findAlbum(String albumId) throws MalformedURLException,
			IOException, ServiceException {
		for (AlbumEntry album : selectAlbums()) {
			if (album.getGphotoId().equals(albumId)) {
				return album;
			}
		}
		return null;
	}

	@Override
	public PhotoEntry upload(String albumId, byte[] data, String name) 
			throws MalformedURLException, IOException, ServiceException {
		AlbumEntry album = findAlbum(albumId);
		if (album == null) {
			throw new IllegalArgumentException(Messages.get("album_not_found", 
					albumId));
		}
		PhotoEntry photo = new PhotoEntry();
		photo.setTitle(new PlainTextConstruct(name));
		MediaSource mediaSource = new MediaByteArraySource(data, 
				MimeType.getContentTypeByExt(FolderUtil.getFileExt(name)));
		photo.setMediaSource(mediaSource);
		return getPicasawebService().insert(new URL(getPicasaURL(albumId)), 
				photo);
	}

	@Override
	public AlbumEntry findAlbumByTitle(String title)
			throws MalformedURLException, IOException, ServiceException {
		for (AlbumEntry album : selectAlbums()) {
			if (album.getTitle().getPlainText().indexOf(title) != -1) {
				return album;
			}
		}
		return null;
	}

	@Override
	public List<PhotoEntry> findPhotos(String title, int count)
			throws MalformedURLException, IOException, ServiceException {
		Query myQuery = new Query(new URL(getPicasaURL()));
		myQuery.setStringCustomParameter("kind", "photo");
		myQuery.setMaxResults(count);
		myQuery.setFullTextQuery(title);
		AlbumFeed feed = getPicasawebService().query(myQuery, AlbumFeed.class);
		return feed.getPhotoEntries();
	}

}
