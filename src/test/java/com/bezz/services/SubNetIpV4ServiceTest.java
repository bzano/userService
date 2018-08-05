package com.bezz.services;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.bezz.services.impl.SubNetIpV4Service;


@RunWith(SpringRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class SubNetIpV4ServiceTest {
	
	@TestConfiguration
	static class SubNetIpV4ServiceTestConfiguration {
		
		@Bean
		public ISubNetIpV4Service subNetIpV4Service(){
			return new SubNetIpV4Service();
		}
	}
	
	@Autowired
	ISubNetIpV4Service subNetIpV4Service;
	
	@Test
	public void getMinSubNetIp_should_return_the_first_host_ip_in_subnet(){
		// GIVEN
		String ip = "192.168.3.5";
		// AND
		int mask = 22;
		// WHEN
		String minIp = subNetIpV4Service.getMinSubNetIp(ip, mask);
		// THEN
		Assert.assertEquals("192.168.0.1", minIp);
	}
	
	@Test
	public void getMaxSubNetIp_should_return_the_last_host_ip_in_subnet(){
		// GIVEN
		String ip = "192.168.3.5";
		// AND
		int mask = 22;
		// WHEN
		String maxIp = subNetIpV4Service.getMaxSubNetIp(ip, mask);
		// THEN
		Assert.assertEquals("192.168.3.254", maxIp);
	}
	
	@Test
	public void getNetMaskIp_should_return_net_mask_ip(){
		// GIVEN
		int mask = 22;
		// WHEN
		String netMaskIp = subNetIpV4Service.getNetMaskIp(mask);
		// THEN
		Assert.assertEquals("255.255.252.0", netMaskIp);
	}
	
	@Test
	public void getWildCardIp_should_return_wild_card_ip(){
		// GIVEN
		int mask = 22;
		// WHEN
		String netMaskIp = subNetIpV4Service.getWildCardIp(mask);
		// THEN
		Assert.assertEquals("0.0.3.255", netMaskIp);
	}
	
	@Test
	public void getIpsRange_should_return_all_ips_in_range(){
		// GIVEN
		String ip = "138.23.0.0";
		// AND
		int mask = 16;
		// WHEN
		List<String> ipsRange = subNetIpV4Service.getIpsRange(ip, mask);
		// THEN
		Assert.assertTrue(ipsRange.contains("138.23.0.1"));
	}
}
