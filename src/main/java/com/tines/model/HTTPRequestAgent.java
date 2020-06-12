package com.tines.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.json.simple.JSONObject;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequestAgent implements BaseAgent {

  final private BaseAgent base;

  public HTTPRequestAgent(BaseAgent base) {
    this.base = base;
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


  public Map<String, JSONObject> executeTask(String url, ObjectMapper mapper) throws MalformedURLException, URISyntaxException, JsonProcessingException {

    Map<String, JSONObject> apiResponse;

    URL link = new URL(url);
    URI uri = new URI(link.toString());

    Client client = Client.create();
    WebResource webResource = client.resource(uri);
    ClientResponse clientResponse = webResource.accept("application/json").get(ClientResponse.class);

    if (clientResponse.getStatus() != 200 && clientResponse.getStatus() != 201) {
      throw new RuntimeException("Failed : HTTP error code : " + clientResponse.getStatus());
    }
    String output = clientResponse.getEntity(String.class)  ;

    if (this.getName().compareTo("fact") == 0) {
      output = "{\"" + "text" + "\":" + "\"" + output + "\"}";
    }
    apiResponse = mapper.readValue(output, HashMap.class);
    return apiResponse;
  }

}
