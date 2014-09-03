package com.missionsky.scp.dataanalysis;

/**
 * @author ellis.xie 
 * @version 1.0
 * SCP Data Analysis Platform Constants 
 */
public class Constants {
	
	/**
	 * Duplicate removal aggregate type: 
	 * FIRST_NOT_NULL:1
	 * LAST_NOT_NULL:2
	 * WEIGHT:3
	 */
	public static final int DUPL_REMOV_AGGR_TYPE_FIRST = 1;
	
	public static final int DUPL_REMOV_AGGR_TYPE_LAST = 2;
	
	public static final int DUPL_REMOV_AGGR_TYPE_WEIGHT = 3;
	
	/**
	 * Genertic aggregate type: 
	 * FIRST_NOT_NULL:1
	 * LAST_NOT_NULL:2
	 * WEIGHT:3
	 */
	public static final int GEN_AGGR_TYPE_FIRST = 1;
	
	public static final int GEN_AGGR_TYPE_LAST = 2;
	
	public static final int GEN_AGGR_TYPE_WEIGHT = 3;
	
	/**
	 * Algorithm running result: 
	 * SUCCESS:1
	 * FAIL:0
	 */
	public static final int ALGORITHM_RESULT_SUCCESS = 1;
	
	public static final int ALGORITHM_RESULT_FAIL = 0;
	
	//schedule run status
	public static final int jobIsReady = 0;
	
	public static final int jobToBeExecuted = 1;
	
	public static final int jobExecutionVetoed = 2;
	
	public static final int jobWasExecuted = 3;
}
