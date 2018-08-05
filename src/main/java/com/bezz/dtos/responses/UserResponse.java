package com.bezz.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserResponse {
	private Integer id;
	private String firstname;
	private String lastname;
	private String ipv4;
	private Integer age;
	private String country;
}
