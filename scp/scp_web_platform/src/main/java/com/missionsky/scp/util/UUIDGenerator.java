package com.missionsky.scp.util;

import java.util.UUID;

public class UUIDGenerator {
	/**
	 * generator 32-bit UUID
	 * 
	 * @return
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replace("-", "");
	}
}