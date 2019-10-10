package com.spring_es.demo.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ApiModel("测试库")
@Component
@PropertySource(value = {"classpath:page.properties"})
@ConfigurationProperties(prefix = "page")
public class Tests {

	@ApiModelProperty(name = "id", value = "ID")
	private Long id;

	@ApiModelProperty(name = "name", value = "姓名")
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Tests [id=" + id + ", name=" + name + "]";
	}


}
