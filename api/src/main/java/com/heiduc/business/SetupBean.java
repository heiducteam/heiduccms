

package com.heiduc.business;

/**
 * 
 * @author Alexander Oleynik
 *
 */
public interface SetupBean {
	
	public static final String VERSION = "1.0";
	public static final String FULLVERSION = "1.0.01";

	/**
	 * setup with init user admin@test.com
	 */
	public void init(); 
	
	/**
	 * setup with default site.
	 */
	void setup();
	
	/**
	 * Clear datastore before setup.
	 */
	void clear();

	/**
	 * Clear file cache.
	 */
	void clearFileCache();
	
	void loadDefaultSite();
	
}
