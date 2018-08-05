package com.bezz.services.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.message.MessageFormatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.bezz.beans.IpBlock;
import com.bezz.exceptions.IpNotFoundException;
import com.bezz.services.IIpV4Service;
import com.bezz.services.ISubNetIpV4Service;

@Service
public class IpV4Service implements IIpV4Service {
	private static final Logger LOG = LoggerFactory.getLogger(IpV4Service.class);
	private static final String LOADING_IPS_LOG = "Loading geo ips file";
	private static final String LOADING_IPS_TIME_LOG = "Loading geo ips file took {0} ms";
	private static final String ADD_IP_RANGE_TIME_LOG = "Adding {0} ips took {1} ms";
	private static final String LOADING_IPS_ERROR_LOG = "Error loading geo ips file";
	private static final String IP_NOT_FOUND = "{0} not found";
	private static final String IP_COUNTRY_SEP = ",";
	private static final String MULT = "*";
	private static final String SUBNET_SEP = "\\/";
	private static final IpBlock MULT_IP_BLOCK = new IpBlock(MULT);
	
	private IpBlock root = new IpBlock(MULT);
	
	@Value("classpath:geoip.csv")
	private Resource geoip;
	
	@Autowired
	private ISubNetIpV4Service subNetIpV4Service;
	
	@PostConstruct
	private void loadGeoIpFile(){
		LOG.info(LOADING_IPS_LOG);
		long startTime = System.currentTimeMillis();
		try (BufferedReader reader = new BufferedReader(new FileReader(geoip.getFile()))){;
			String ipLine = null;
			do{
				ipLine = reader.readLine();
				if(ipLine != null){
					String[] ipWithCountry = ipLine.split(IP_COUNTRY_SEP);
					
					String ip = ipWithCountry[0];
					String country = ipWithCountry[1];
					
					addIpOrSubNet(ip, country);
				}
			}while(ipLine != null);
		} catch (IOException e) {
			LOG.error(LOADING_IPS_ERROR_LOG, e);
		}
		long totalTime = System.currentTimeMillis() - startTime;
		LOG.info(new MessageFormatMessage(LOADING_IPS_TIME_LOG, totalTime).getFormattedMessage());
	}
	
	@Override
	public IpBlock addIp(String ip, String country) {
		Stream<IpBlock> blocksStream = ipToIpBlocksStream(ip);
		IpBlock finalIpBlock = blocksStream.reduce(root, this::appendsTwoBlocks);
		finalIpBlock.setCountry(country);
		return finalIpBlock;
	}

	@Override
	public String findIpCountry(String ip) throws IpNotFoundException {
		Optional<IpBlock> optionalIpBlock = getLowerIpBlock(ip);
		return optionalIpBlock.get().getCountry();
	}

	private void addIpOrSubNet(String ip, String country) {
		String[] subNetSplit = ip.split(SUBNET_SEP);
		if(subNetSplit.length > 1){
			String subnetIp = subNetSplit[0];
			int mask = new Integer(subNetSplit[1]);
			List<String> ips = subNetIpV4Service.getIpsRange(subnetIp, mask);
			long startTime = System.currentTimeMillis();
			ips.stream().forEach(ipValue -> addIp(ipValue, country));
			long totalTime = System.currentTimeMillis() - startTime;
			LOG.info(new MessageFormatMessage(ADD_IP_RANGE_TIME_LOG, ips.size(), totalTime).getFormattedMessage());
		}else{
			addIp(ip, country);
		}
	}

	private Optional<IpBlock> getLowerIpBlock(String ip) throws IpNotFoundException {
		Iterator<IpBlock> blocks = ipToIpBlocksStream(ip).iterator();
		Optional<IpBlock> optionalIpBlock = recursiveBorwsing(root, blocks);
		if(!optionalIpBlock.isPresent()){
			String errorMessage = new MessageFormatMessage(IP_NOT_FOUND, ip).getFormattedMessage();
			throw new IpNotFoundException(errorMessage);
		}
		return optionalIpBlock;
	}
	
	private Optional<IpBlock> recursiveBorwsing(IpBlock rootIpBlock, Iterator<IpBlock> blocks){
		if(!blocks.hasNext()){
			return Optional.of(rootIpBlock);
		}
		IpBlock nextBlock = blocks.next();
		Optional<IpBlock> childBlock = rootIpBlock.getChild(nextBlock);
		if(!childBlock.isPresent()){
			childBlock = rootIpBlock.getChild(MULT_IP_BLOCK);
			if(!childBlock.isPresent()){
				return Optional.empty();
			}
		}
		return recursiveBorwsing(childBlock.get(), blocks);
	}
	
	private Stream<IpBlock> ipToIpBlocksStream(String ip) {
		Stream<IpBlock> blocksStream = Arrays.asList(ip.split(IpBlock.BLOCK_SEP)).stream().map(IpBlock::new);
		return blocksStream;
	}
	
	private IpBlock appendsTwoBlocks(IpBlock block1, IpBlock block2) {
		IpBlock newBlock2 = block1.append(block2);
		if(block1 != root){
			block2.setParent(block1);
		}
		return newBlock2;
	}
	
	@Override
	public IpBlock getRoot() {
		return root;
	}
	
	@Override
	public void setRoot(IpBlock pRoot) {
		root = pRoot;
	}
}
