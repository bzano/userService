package com.bezz.services;

import java.util.List;

public interface ISubNetIpV4Service {
	List<String> getIpsRange(String ip, int mask);
	String getMaxSubNetIp(String ip, int mask);
	String getMinSubNetIp(String ip, int mask);
	String getNetMaskIp(int mask);
	String getWildCardIp(int mask);
}
