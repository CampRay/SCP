package com.missionsky.scp.dataadapter.common;

/**
 * @author Ellis Xie 
 * @version 1.0
 * 系统常量
 */
public class SystemConstants {

	// 数据格式
	public static final int DATA_FORMAT_JSON = 1;
	public static final int DATA_FORMAT_XML = 2;
	public static final int DATA_FORMAT_CSV = 3;

	// 数据返回成功标识操作
	public static final Integer SUCCESS_FLAG_GOON = 1;
	public static final Integer SUCCESS_FLAG_HALT = 0;

	// DataFliter API 操作类型
	public static final Integer DATA_FLITER_TYPE_INSERT = 1;
	public static final Integer DATA_FLITER_TYPE_UPDATE = 2;
	public static final Integer DATA_FLITER_TYPE_DELETE = 3;

}
