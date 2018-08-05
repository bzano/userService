package com.bezz.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private Integer id;
	private String firstname;
	private String lastname;
	private String ipv4;
	private Integer age;
	private String country;
}
