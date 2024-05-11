package com.pj;

/**
 * @author click33
 * @since 2024/5/11
 */
public class SysUser {

    public int id;

    public String name;

    public SysUser() {
    }
    public SysUser(int id, String name) {
        this.id = id;
        this.name = name;
    }


    /**
     * 获取
     *
     * @return id
     */
    public int getId() {
        return this.id;
    }

    /**
     * 设置
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取
     *
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}