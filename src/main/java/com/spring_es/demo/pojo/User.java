package com.spring_es.demo.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel("用户信息")
public class User {

	@ApiModelProperty(name = "id", value = "ID")
	private Long id;

	@ApiModelProperty(name = "name", value = "姓名")
	private String name;

	@ApiModelProperty(name = "age", value = "年龄")
	private Integer age;

	@ApiModelProperty(name = "birthday", value = "生日")
	private Date birthday;

	@ApiModelProperty(name = "city", value = "出生地")
	private String city;

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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
