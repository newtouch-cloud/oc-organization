package com.geeke.notice.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyTree {

  public String id;      //id

  public String name;    //名字

  public String parentId; //父id

  public List<CompanyTree>  children = new ArrayList<>();

}
