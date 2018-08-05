package com.bezz.dtos.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRequest {
	@ApiModelProperty(required=true)
	@NotNull(message="user id is required")
	private Integer id;
	
	private String firstname;
	
	@ApiModelProperty(required=true)
	@NotEmpty(message="user lastname is required")
	private String lastname;
	
	@ApiModelProperty(required=true)
	@NotEmpty(message="user ipv4 is required")
	private String ipv4;
	
	private Integer age;
}
