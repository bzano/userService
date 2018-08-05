package com.bezz.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bezz.beans.IpBlock;
import com.bezz.services.ISubNetIpV4Service;
import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedInts;

@Service
public class SubNetIpV4Service implements ISubNetIpV4Service {
	private static final int BYTES_32 = 32;
	private static final int BYTE_SIZE = 8;
	private static final int IP_BLOCK_INDEX_MAX = 3;
	private static final String ZERO = "0";
	private static final String ONE = "1";
	private static final int RADIX = 2;
	private static final String BYTES_REGEXP = "(?<=\\G.{8})";
	private static final String BLOCK_SEP = ".";
	
	@Override
	public List<String> getIpsRange(String ip, int mask){
		String minIp = getMinSubNetIp(ip, mask);
		String maxIp = getMaxSubNetIp(ip, mask);
		
		ArrayList<String> ipsAccumulator = new ArrayList<String>();
		List<String> minBlockList = Arrays.asList(minIp.split(IpBlock.BLOCK_SEP));
		List<String> maxBlockList = Arrays.asList(maxIp.split(IpBlock.BLOCK_SEP));
		
		int firstLevel = 0;
		String accumulator = StringUtils.EMPTY;
		buildIpsRange(ipsAccumulator, accumulator, minBlockList, maxBlockList, firstLevel);
		return ipsAccumulator;
	}
	
	@Override
	public String getMaxSubNetIp(String ip, int mask){
		String wildCardIp = getWildCardIp(mask);
		long ipLong = ipToLong(ip);
		long wildCardIpLong = ipToLong(wildCardIp);
		long wildCardedIpLong = (ipLong | wildCardIpLong) - 1;

		String wildCardedIp = StringUtils.leftPad(Long.toString(wildCardedIpLong, RADIX), BYTES_32, ZERO);
		return bytesIpToString(wildCardedIp);
	}

	@Override
	public String getMinSubNetIp(String ip, int mask){
		String netMaskIp = getNetMaskIp(mask);
		long ipLong = ipToLong(ip);
		long netMaskIpLong = ipToLong(netMaskIp);
		long maskedIpLong = (ipLong & netMaskIpLong) + 1;
		
		String bytesMaskedIp = StringUtils.leftPad(Long.toString(maskedIpLong, RADIX), BYTES_32, ZERO);
		return bytesIpToString(bytesMaskedIp);
	}
	
	@Override
	public String getNetMaskIp(int mask) {
		String binaryIp = IntStream.range(0, BYTES_32).mapToObj(index -> (index < mask) ? ONE : ZERO).collect(Collectors.joining());
		String ip = binaryIpToRealIp(binaryIp);
		return ip;
	}
	
	@Override
	public String getWildCardIp(int mask) {
		String binaryIp = IntStream.range(0, BYTES_32).mapToObj(index -> (index < mask) ? ZERO : ONE).collect(Collectors.joining());
		List<String> ipBlocks = Arrays.asList(binaryIp.split(BYTES_REGEXP));
		String ip = ipBlocks.stream()
				.map(stringByte -> Integer.parseInt(stringByte, RADIX))
				.map(String::valueOf)
				.collect(Collectors.joining(BLOCK_SEP));
		return ip;
	}
	
	private void buildIpsRange(List<String> ips, String accumulator, List<String> minBlockList, List<String> maxBlockList, int level){
		if(level >= minBlockList.size()){
			ips.add(accumulator);
		}else{
			int minBlock = new Integer(minBlockList.get(level));
			int maxBlock = new Integer(maxBlockList.get(level));
			for(int i = minBlock; i <= maxBlock; i++){
				String newAccumulator = (Strings.isNullOrEmpty(accumulator)) ? String.valueOf(i) : accumulator + BLOCK_SEP + i;
				buildIpsRange(ips, newAccumulator, minBlockList, maxBlockList, level + 1);
			}
		}
	}
	
	private String bytesIpToString(String bytesMaskedIp) {
		String resultIp = Arrays.asList(bytesMaskedIp.split(BYTES_REGEXP))
				.stream()
				.map(value -> UnsignedInts.parseUnsignedInt(value, RADIX))
				.map(String::valueOf)
				.collect(Collectors.joining(BLOCK_SEP));
		return resultIp;
	}

	private long ipToLong(String ip) {
		String[] ipSplits = ip.split(IpBlock.BLOCK_SEP);
		long ipResult = IntStream.range(0, ipSplits.length)
			.mapToObj(index -> {
				int shift = (BYTE_SIZE * (IP_BLOCK_INDEX_MAX - index));
				long value = new Long(ipSplits[index]);
				long ipValue = value << shift;
				return ipValue;
			}).mapToLong(Long::new).sum();
		return ipResult;
	}

	private String binaryIpToRealIp(String binaryIp) {
		List<String> ipBlocks = Arrays.asList(binaryIp.split(BYTES_REGEXP));
		String ip = ipBlocks.stream()
				.map(stringByte -> Integer.parseInt(stringByte, RADIX))
				.map(String::valueOf)
				.collect(Collectors.joining(BLOCK_SEP));
		return ip;
	}
}
