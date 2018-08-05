package com.bezz.configuration;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
	private static final DozerBeanMapper DOZER_BEAN_MAPPER = new DozerBeanMapper();

	@Bean
	public DozerBeanMapper mapper(){
		return DOZER_BEAN_MAPPER;
	}
}
