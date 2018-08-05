package com.bezz.exceptions;

public class IpNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public IpNotFoundException(){
		super();
	}
	
	public IpNotFoundException(String message){
		super(message);
	}
}
