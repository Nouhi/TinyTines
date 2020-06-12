package com.tines.model;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.Map;
import java.util.Objects;

public class Agent implements BaseAgent{

  @JsonProperty("type")
  private String type;
  @JsonProperty("name")
  private String name;
  @JsonProperty("options")
  private Map<String, String> options;


  public Agent() {
  }


  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }


  public Map<String, String> getOptions() {
    return options;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOptions(Map<String, String> options) {
    this.options = options;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Agent agent = (Agent) o;
    return Objects.equals(type, agent.type) &&
      Objects.equals(name, agent.name) &&
      Objects.equals(options, agent.options);

  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type, options);
  }
}
