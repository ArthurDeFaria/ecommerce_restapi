package com.ckweb.rest_api.model.enumeration;

public enum Cargo{
    USER("user"),
    ADMIN("admin"),
    MANAGER("manager");

    private String cargo;

    Cargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCargo(){
        return cargo;
    }
}