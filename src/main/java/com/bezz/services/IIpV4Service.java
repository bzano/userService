package com.bezz.services;

import com.bezz.beans.IpBlock;
import com.bezz.exceptions.IpNotFoundException;

public interface IIpV4Service {
	IpBlock addIp(String ip, String country);
	String findIpCountry(String ip) throws IpNotFoundException;
	IpBlock getRoot();
	void setRoot(IpBlock block);
}
