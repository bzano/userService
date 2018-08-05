package com.bezz.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.bezz.beans.IpBlock;
import com.bezz.exceptions.IpNotFoundException;
import com.bezz.services.impl.IpV4Service;
import com.bezz.services.impl.SubNetIpV4Service;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class IpV4ServiceTest {
	private static final String COUNTRY_FRANCE = "France";
	private static final String COUNTRY_LOCAL = "Local";

	@TestConfiguration
	static class IpServiceTestConfiguration {
		
		@Bean
		public ISubNetIpV4Service subNetIpV4Service(){
			return new SubNetIpV4Service();
		}
		
		@Bean
		public IIpV4Service ipServiceBean(){
			return new IpV4Service();
		}
	}
	
	@Autowired
	private IIpV4Service ipService;
	
	private IpBlock root;
	private IpBlock ipBlock1;
	private IpBlock ipBlock2_1;
	private IpBlock ipBlock2_2;
	private IpBlock ipBlock3;
	private IpBlock ipBlock4_1;
	private IpBlock ipBlock4_2;
	private IpBlock ipBlock4_3;
	
	@Before
	public void setUp(){
		root = new IpBlock("*");
		ipBlock1 = new IpBlock("127");
		ipBlock2_1 = new IpBlock("0");
		ipBlock2_2 = new IpBlock("*");
		ipBlock3 = new IpBlock("0");
		ipBlock4_1 = new IpBlock("1");
		ipBlock4_2 = new IpBlock("2");
		ipBlock4_3 = new IpBlock("3");
		
		root.append(ipBlock1);
		ipBlock1.append(ipBlock2_1);
		ipBlock1.append(ipBlock2_2);
		ipBlock2_1.append(ipBlock3);
		ipBlock2_2.append(ipBlock3);
		ipBlock3.append(ipBlock4_1);
		ipBlock3.append(ipBlock4_2);
		ipBlock3.append(ipBlock4_3);

		ipBlock2_1.setParent(ipBlock1);
		ipBlock2_2.setParent(ipBlock1);
		ipBlock3.setParent(ipBlock2_1);
		ipBlock4_1.setParent(ipBlock3);
		ipBlock4_2.setParent(ipBlock3);
		ipBlock4_3.setParent(ipBlock3);

		ipBlock4_1.setCountry(COUNTRY_LOCAL);
		
		ipService.setRoot(root);
	}
	
	@Test
	public void add_should_build_and_add_a_ipblock(){
		// GIVEN
		String ip = "1.2.3.4";
		// WHEN
		String generatedIp = ipService.addIp(ip, COUNTRY_FRANCE).toString();
		// THEN
		Assert.assertEquals(ip, generatedIp);
	}
	
	@Test
	public void add_should_build_and_add_a_ipblock_with_length_four(){
		// GIVEN
		String ip = "1.2.3.4";
		// WHEN
		int ipLenght = ipService.addIp(ip, "France").length();
		// THEN
		Assert.assertEquals(4, ipLenght);
	}
	
	
	@Test
	public void add_an_existing_ip_should_not_override_original() throws IpNotFoundException {
		// GIVEN
		String ip = "127.0.0.1";
		// WHEN
		IpBlock ipBlock = ipService.addIp(ip, COUNTRY_LOCAL);
		// THEN
		Assert.assertSame(ipBlock4_1, ipBlock);
	}
	
	@Test
	public void findIpCountry_should_return_corresponding_country_if_exist() throws IpNotFoundException {
		// GIVEN
		String ip = "127.0.0.1";
		// WHEN
		String actualCountry = ipService.findIpCountry(ip);
		// THEN
		Assert.assertEquals(COUNTRY_LOCAL, actualCountry);
	}
	
	@Test
	public void findIpCountry_should_always_return_corresponding_country_if_exist() throws IpNotFoundException {
		// GIVEN
		String ip = "127.4.0.1";
		// WHEN
		String actualCountry = ipService.findIpCountry(ip);
		// THEN
		Assert.assertEquals(COUNTRY_LOCAL, actualCountry);
	}
	
	@Test(expected = IpNotFoundException.class)
	public void findIpCountry_should_throw_ip_not_found_exception_if_does_not_exist() throws IpNotFoundException {
		// GIVEN
		String ip = "1.1.1.1";
		// WHEN
		ipService.findIpCountry(ip);
		// THEN Assert exception
	}
}
