package com.tines.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class HTTPRequestAgentTest {


    final ObjectMapper mapper = new ObjectMapper();


  @Test
  public void executeTask_JsonResponse_TestSuccess() throws URISyntaxException, MalformedURLException {
    Agent agent = new Agent();
    agent.setName("datetime");
    HTTPRequestAgent httpRequestAgent = new HTTPRequestAgent(agent);

    String url ="https://jsonplaceholder.typicode.com/todos/1";
    String response ="{\n  \"userId\": 1,\n  \"id\": 1,\n  \"title\": \"delectus aut autem\",\n  \"completed\": false\n}";


    assertEquals(httpRequestAgent.executeTask(url,mapper).getStatus(),200);
    assertEquals(httpRequestAgent.executeTask(url,mapper).getEntity(String.class),response);
  }

  @Test
  public void executeTask_TextResponse_TestSuccess() throws URISyntaxException, MalformedURLException {
    Agent agent = new Agent();
    agent.setName("fact");
    HTTPRequestAgent httpRequestAgent = new HTTPRequestAgent(agent);

    String url ="https://baconipsum.com/api/?type=meat-and-filler&paras=1&format=text";

    assertEquals(httpRequestAgent.executeTask(url,mapper).getStatus(),200);
  }

  @Test(expected = MalformedURLException.class)
  public void executeTask_MalformedURLExceptionThrown() throws  URISyntaxException, MalformedURLException {
    Agent agent = new Agent();
    agent.setName("datetime");
    HTTPRequestAgent httpRequestAgent = new HTTPRequestAgent(agent);

    String url ="hps://jsonplaceholder.typicode.com/todos/1";
    String response ="{\n  \"userId\": 1,\n  \"id\": 1,\n  \"title\": \"delectus aut autem\",\n  \"completed\": false\n}";

    httpRequestAgent.executeTask(url,mapper);

  }

  @Test(expected = URISyntaxException.class)
  public void executeTask_URISyntaxExceptionThrown() throws URISyntaxException, MalformedURLException {
    Agent agent = new Agent();
    agent.setName("datetime");
    HTTPRequestAgent httpRequestAgent = new HTTPRequestAgent(agent);

    String url ="https://jsonplaceholder.typicode.com/ todos/1";
    String response ="{\n  \"userId\": 1,\n  \"id\": 1,\n  \"title\": \"delectus aut autem\",\n  \"completed\": false\n}";

    httpRequestAgent.executeTask(url,mapper);

  }
}


