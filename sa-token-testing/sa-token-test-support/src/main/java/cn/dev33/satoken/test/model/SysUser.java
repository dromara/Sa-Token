package com.pj.test.model;

import cn.dev33.satoken.json.SaJsonType;

import java.io.Serializable;

/**
 * User 实体类
 */
public class SysUser implements Serializable, SaJsonType {

	private static final long serialVersionUID = -3402582076947606196L;

	public SysUser() {
	}

	public SysUser(long id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	private long id;
	private String name;
	private int age;
	private SysRole role;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public SysRole getRole() {
		return role;
	}

	public SysUser setRole(SysRole role) {
		this.role = role;
		return this;
	}

	@Override
	public String toString() {
		return "SysUser{" +
				"id=" + id +
				", name='" + name + '\'' +
				", age=" + age +
				", role=" + role +
				'}';
	}

}
