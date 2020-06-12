package com.tines.model;

import java.util.HashMap;
import java.util.Map;

public class PrintAgent implements BaseAgent{


  final private BaseAgent base;

  public PrintAgent(BaseAgent base) {
    this.base=base;
  }

  @Override
  public String getType() {
    return base.getType();
  }

  @Override
  public String getName() {
    return base.getName();
  }

  @Override
  public Map<String, String> getOptions() {
    return base.getOptions();
  }

  public void executeTask(String str){
    System.out.println(str);

  }
}
