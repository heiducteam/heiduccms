package com.heiduc.entity;

import static com.heiduc.utils.EntityUtil.*;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.heiduc.api.datastore.Entity;
import org.heiduc.api.datastore.Key;
import org.heiduc.api.datastore.KeyFactory;

import com.heiduc.utils.DateUtil;

public abstract class BaseEntityImpl implements BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6669536468737342559L;

	protected static final Log LOGGER = LogFactory.getLog(BaseEntityImpl.class);

	private Key key;
	private String createUserEmail;
	private Date createDate;
	private String modUserEmail;
	private Date modDate;

	public BaseEntityImpl() {
		createDate = new Date();
		modDate = createDate;
		createUserEmail = "";
		modUserEmail = "";
	}

	@Override
	public Long getId() {
		return key == null ? null : key.getId();
	}

	@Override
	public void setId(Long id) {
		if (id != null && id > 0) {
			key = KeyFactory.createKey(getKind(getClass()), id);
		} else {
			key = null;
		}
	}

	@Override
	public Key getKey() {
		return key;
	}

	@Override
	public void setKey(Key key) {
		this.key = key;
	}

	@Override
	public void load(Entity entity) {
		key = entity.getKey();
		createUserEmail = getStringProperty(entity, "createUserEmail");
		createDate = getDateProperty(entity, "createDate");
		modUserEmail = getStringProperty(entity, "modUserEmail");
		modDate = getDateProperty(entity, "modDate");
	}

	@Override
	public void save(Entity entity) {
		setProperty(entity, "createUserEmail", createUserEmail, true);
		setProperty(entity, "createDate", createDate, true);
		setProperty(entity, "modUserEmail", modUserEmail, true);
		setProperty(entity, "modDate", modDate, true);
	}

	@Override
	public boolean isNew() {
		return key == null;
	}

	@Override
	public void copy(BaseEntity entity) {
		Key myKey = getKey();
		Entity buf = new Entity("tmp");
		entity.save(buf);
		load(buf);
		setKey(myKey);
	}

	public boolean equals(Object object) {
		if (object instanceof BaseEntity && object.getClass().equals(this.getClass())) {
			BaseEntity entity = (BaseEntity) object;
			if (getId() == null && entity.getId() == null) {
				return true;
			}
			if (getId() != null && getId().equals(entity.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String getCreateUserEmail() {
		return createUserEmail;
	}

	@Override
	public void setCreateUserEmail(String createUserEmail) {
		this.createUserEmail = createUserEmail;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String getModUserEmail() {
		return modUserEmail;
	}

	@Override
	public void setModUserEmail(String modUserEmail) {
		this.modUserEmail = modUserEmail;
	}

	@Override
	public Date getModDate() {
		return modDate;
	}

	@Override
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	@Override
	public String getModDateString() {
		return DateUtil.toString(modDate);
	}

	@Override
	public String getCreateDateString() {
		return DateUtil.toString(createDate);
	}

	@Override
	public String getModDateTimeString() {
		return DateUtil.dateTimeToString(modDate);
	}

	@Override
	public String getCreateDateTimeString() {
		return DateUtil.dateTimeToString(createDate);
	}
}
