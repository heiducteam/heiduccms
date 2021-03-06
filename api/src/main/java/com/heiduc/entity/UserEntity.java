

package com.heiduc.entity;

import java.util.TimeZone;

import org.heiduc.api.datastore.Entity;

import static com.heiduc.utils.EntityUtil.*;

import com.heiduc.enums.UserRole;

/**
 * @author Alexander Oleynik
 */
public class UserEntity extends BaseEntityImpl {

	private static final long serialVersionUID = 4L;

	private String name;
	private String password;
	private String email;
	private UserRole role;
	private String forgotPasswordKey;
	private boolean disabled;
	private String timezone;
	private String salt;
	private String nickName;
	private String avatar;
	
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public UserEntity() {
		role = UserRole.USER;
		disabled = false;
		timezone = TimeZone.getDefault().getID();
		salt = "";
	}
	
	@Override
	public void load(Entity entity) {
		super.load(entity);
		name = getStringProperty(entity, "name");
		password = getStringProperty(entity, "password");
		email = getStringProperty(entity, "email");
		role = UserRole.valueOf(getStringProperty(entity, "role"));
		forgotPasswordKey = getStringProperty(entity, "forgotPasswordKey");
		disabled = getBooleanProperty(entity, "disabled", false);
		timezone = getStringProperty(entity, "timezone");
		salt = getStringProperty(entity, "salt");
		nickName = getStringProperty(entity, "nickName");
		avatar = getStringProperty(entity, "avatar");
	}
	
	@Override
	public void save(Entity entity) {
		super.save(entity);
		setProperty(entity, "name", name, false);
		setProperty(entity, "password", password, false);
		setProperty(entity, "email", email, true);
		setProperty(entity, "role", role.name(), true);
		setProperty(entity, "forgotPasswordKey", forgotPasswordKey, true);
		setProperty(entity, "disabled", disabled, false);
		setProperty(entity, "timezone", timezone, false);
		setProperty(entity, "salt", salt, false);
		setProperty(entity, "nickName", nickName, false);
		setProperty(entity, "avatar", avatar, false);
	}

	public UserEntity(String aName, String aPassword,
			String anEmail, UserRole aRole,String aNickName,String anAvatar) {
		this();
		name = aName;
		password = aPassword;
		email = anEmail;
		role = aRole;
		nickName = aNickName;
		avatar = anAvatar;
	}
	
	public UserEntity(String aName, String aPassword,
			String anEmail, UserRole aRole) {
		this();
		name = aName;
		password = aPassword;
		email = anEmail;
		role = aRole;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String aPassword) {
		password = aPassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getRoleString() {
		if (role == null) {
			return "";
		}
		return role.name();
	}
	
	public boolean isAdmin() {
		if (role == null) {
			return false;
		}
		return role.equals(UserRole.ADMIN);
	}

	public boolean isSiteUser() {
		if (role == null) {
			return false;
		}
		return role.equals(UserRole.SITE_USER);
	}

	public boolean isUser() {
		if (role == null) {
			return false;
		}
		return role.equals(UserRole.USER);
	}
	
	public boolean isEditor() {
		return isAdmin() || isUser();
	}

	public String getForgotPasswordKey() {
		return forgotPasswordKey;
	}

	public void setForgotPasswordKey(String forgotPasswordKey) {
		this.forgotPasswordKey = forgotPasswordKey;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public String getSalt() {
		return salt;
	}

	public void setSlat(String salt) {
		this.salt = salt;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
}
