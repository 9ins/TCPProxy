package org.chaostocosmos.net.tcp;

import java.util.EventListener;

/**
 * 
 * ConfigChangeListener
 *
 * @author 9ins
 * 2020. 11. 18.
 */
public interface AdminCommandListener extends EventListener {
	
	/**
	 * Receive process of changed configuration.
	 * @param cce
	 */
	public void receiveConfigChangeEvnet(AdminCommandEvent cce);

}
