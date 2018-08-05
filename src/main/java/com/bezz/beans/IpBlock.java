package com.bezz.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

import org.apache.logging.log4j.message.MessageFormatMessage;

@Getter
@Setter
public class IpBlock {
	public static final String BLOCK_SEP = "\\.";
	public static final String TO_STRING_FORMAT = "{0}.{1}";
	
	private Map<String, IpBlock> childrenBlocks = new HashMap<String, IpBlock>();
	
	private String code;
	private String country;
	private IpBlock parent;
	
	public IpBlock(String code){
		this.code = code;
	}
	
	public IpBlock append(IpBlock block){
		Optional<IpBlock> blockChild = getChild(block);
		if(!blockChild.isPresent()){
			childrenBlocks.put(block.getCode(), block);
			return block;
		}
		return blockChild.get();
	}
	
	public Optional<IpBlock> getChild(IpBlock block){
		IpBlock ipBlock = childrenBlocks.get(block.getCode());
		return ipBlock == null ? Optional.empty() : Optional.of(ipBlock);
	}

	@Override
	public String toString() {
		return (parent == null) ? code : new MessageFormatMessage(TO_STRING_FORMAT, parent.toString(), code).getFormattedMessage();
	}
	
	public int length(){
		return (parent == null) ? 1 : 1 + parent.length();
	}
}
