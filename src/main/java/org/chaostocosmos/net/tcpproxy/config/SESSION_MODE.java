package org.chaostocosmos.net.tcpproxy.config;

/**
 * 
 * SESSION_MODE
 *
 * @author 9ins
 * 2020. 11. 26.
 */
public enum SESSION_MODE {
	STAND_ALONE, 
	HIGI_AVAILABLE_FAIL_OVER,
	HIGI_AVAILABLE_FAIL_BACK,
	LOAD_BALANCE_ROUND_ROBIN,
	LOAD_BALANCE_SEPARATE_RATIO,
};

