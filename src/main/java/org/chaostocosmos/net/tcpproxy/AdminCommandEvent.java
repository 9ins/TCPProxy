package org.chaostocosmos.net.tcpproxy;

import java.util.EventObject;

/**
 * 
 * AdminCommandEvent
 *
 * @author 9ins
 * 2020. 11. 18.
 */
public class AdminCommandEvent extends EventObject {
	
	AdminCommand adminCommand;

	/**
	 * Constructor
	 * @param source
	 */
	public AdminCommandEvent(Object source, AdminCommand adminCommand) {
		super(source);
		this.adminCommand = adminCommand;
	}
	
	/**
	 * Get adminCommand object
	 * @return
	 */
	public AdminCommand getAdminCommand() {
		return this.adminCommand;
	}
}
