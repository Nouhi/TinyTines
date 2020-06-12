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
  public void executeTask_TestSuccess() throws JsonProcessingException, URISyntaxException, MalformedURLException {
    Agent agent = new Agent();
    agent.setName("datetime");
    HTTPRequestAgent httpRequestAgent = new HTTPRequestAgent(agent);

    String url ="https://jsonplaceholder.typicode.com/todos/1";
    httpRequestAgent.executeTask(url,mapper);
    String response ="{id=1, completed=false, title=delectus aut autem, userId=1}";

    assertEquals(httpRequestAgent.executeTask(url,mapper).toString(),response);


  }

  @Test(expected = MalformedURLException.class)
  public void executeTask_MalformedURLExceptionThrown() throws JsonProcessingException, URISyntaxException, MalformedURLException {
    Agent agent = new Agent();
    agent.setName("datetime");
    HTTPRequestAgent httpRequestAgent = new HTTPRequestAgent(agent);

    String url ="hts://jsonplaceholder.typicode.com/todos/1";
    httpRequestAgent.executeTask(url,mapper);
    String response ="{id=1, completed=false, title=delectus aut autem, userId=1}";

    assertEquals(httpRequestAgent.executeTask(url,mapper).toString(),response);


  }

  @Test(expected = URISyntaxException.class)
  public void executeTask_URISyntaxExceptionThrown() throws JsonProcessingException, URISyntaxException, MalformedURLException {
    Agent agent = new Agent();
    agent.setName("datetime");
    HTTPRequestAgent httpRequestAgent = new HTTPRequestAgent(agent);

    String url ="https://jsonplaceholder.typicode.com/ todos/1";
    httpRequestAgent.executeTask(url,mapper);
    String response ="{id=1, completed=false, title=delectus aut autem, userId=1}";

    assertEquals(httpRequestAgent.executeTask(url,mapper).toString(),response);


  }
}



//"{unixtime=1591909497, utc_offset=+01:00, dst=false, day_of_year=163, timezone=Europe/Dublin, abbreviation=IST, dst_offset=0, utc_datetime=2020-06-11T21:04:57.009955+00:00, datetime=2020-06-11T22:04:57.009955+01:00, dst_until=null, client_ip=37.228.241.241, dst_from=null, week_number=24, day_of_week=4, raw_offset=3600}";
      /*new JSONObject();
    response.put("abbreviation", "IST");
    response.put("client_ip", "37.228.241.241");
    response.put( "datetime", "2020-06-11T21,22,30.067167+01,00");
    response.put("day_of_week", 4);
    response.put("day_of_year", 163);
    response.put("dst", false);
    response.put("dst_from", null);
    response.put("dst_offset", 0);
    response.put("raw_offset", 3600);
    response.put("timezone", "Europe/Dublin");
    response.put("unixtime", 1591906950);
    response.put("utc_datetime", "2020-06-11T20,22,30.067167+00,00");
    response.put("utc_offset", "+01,00");
    response.put("week_number", 24);*/
