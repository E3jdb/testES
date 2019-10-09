package com.spring_es.demo.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("测试库")
public class Tests {


	@Excel(name = "编号")
	@ApiModelProperty(name = "id", value = "ID")
	private Long id;

	@Excel(name = "姓名", width = 20)
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
