import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tines.model.Agent;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class TinyTinesTest {


  final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void getValue_TestSuccess() {

    String unpopulatedString = "https://api.sunrise-sunset.org/json?lat={{location.latitude}}&lng={{location.longitude}}";
    //data fields that need to be populated
    List<String> matches = Arrays.asList("location.latitude","location.longitude");
    //api responses for each agent
    Map<String, Map<String, JSONObject >> data = new HashMap<>();

    JSONObject response = new JSONObject();
    response.put("ip", "2a02:8084:2160:4400:4994:d7c2:a1f0:339e");
    response.put("success",true);
    response.put("continent", "Europe");
    response.put("country", "Ireland");
    response.put("latitude", "53.3165322");
    response.put("longitude", "-6.3425318");
    response.put("region", "County Dublin");
    response.put("timezone", "Europe/Dublin");
    response.put("currency", "Euro");

    data.put("location",response);

    assertEquals(TinyTines.getValue(matches,data,unpopulatedString), "https://api.sunrise-sunset.org/json?lat=53.3165322&lng=-6.3425318");
  }

  @Test
  public void getValue_AbsentField_TestSuccess() {

    String unpopulatedString = "https://api.sunrise-sunset.org/json?lat={{location.latitude}}&lng={{not.exist}}";
    //data fields that need to be populated
    List<String> matches = Arrays.asList("location.latitude","not.exist");
    //api responses for each agent
    Map<String, Map<String, JSONObject >> data = new HashMap<>();

    JSONObject response = new JSONObject();
    response.put("ip", "2a02:8084:2160:4400:4994:d7c2:a1f0:339e");
    response.put("success",true);
    response.put("continent", "Europe");
    response.put("country", "Ireland");
    response.put("latitude", "53.3165322");
    response.put("longitude", "-6.3425318");
    response.put("region", "County Dublin");
    response.put("timezone", "Europe/Dublin");
    response.put("currency", "Euro");

    data.put("location",response);

    assertEquals(TinyTines.getValue(matches,data,unpopulatedString) ,"https://api.sunrise-sunset.org/json?lat=53.3165322&lng=");
  }

  @Test
  public void getValue_AbsentFieldNested_TestSuccess() {

    String unpopulatedString = "https://api.sunrise-sunset.org/json?lat={{location.latitude}}&lng={{does.not.exist}}";
    //data fields that need to be populated
    List<String> matches = Arrays.asList("location.latitude","does.not.exist");
    //api responses for each agent
    Map<String, Map<String, JSONObject >> data = new HashMap<>();

    JSONObject response = new JSONObject();
    response.put("ip", "2a02:8084:2160:4400:4994:d7c2:a1f0:339e");
    response.put("success",true);
    response.put("continent", "Europe");
    response.put("country", "Ireland");
    response.put("latitude", "53.3165322");
    response.put("longitude", "-6.3425318");
    response.put("region", "County Dublin");
    response.put("timezone", "Europe/Dublin");
    response.put("currency", "Euro");

    data.put("location",response);

    assertEquals(TinyTines.getValue(matches,data,unpopulatedString) ,"https://api.sunrise-sunset.org/json?lat=53.3165322&lng=");
  }

  @Test
  public void extractDataFields_TestSuccess() {
    String unpopulatedString="{{test_data.submission_url}}&data=sunset.status_{{sunset.status}}_{{test_data.date}}_{{test_data.deeply.nested.data}}_{{does.not.exist}}";
    List<String> dataFields= TinyTines.extractDataFields(unpopulatedString);


    assertEquals("test_data.submission_url",dataFields.get(0));
    assertEquals("sunset.status",dataFields.get(1));
    assertEquals("test_data.date",dataFields.get(2));
    assertEquals("test_data.deeply.nested.data",dataFields.get(3));
    assertEquals("does.not.exist",dataFields.get(4));
    assertEquals(5,dataFields.size());
  }
  @Test
  public void extractDataFields_unmatchedBrackets() {
    String unpopulatedString="_{{unmatched.brackets}";
    List<String> dataFields= TinyTines.extractDataFields(unpopulatedString);
    assertTrue(dataFields.isEmpty());
  }

  @Test
  public void extractDataFields_ExtraUnmatchedBrackets() {
    String unpopulatedString= "Hello {{extra.unmatched.brackets}} }}" ;
    List<String> dataFields= TinyTines.extractDataFields(unpopulatedString);

    assertEquals(dataFields.get(0) ,"extra.unmatched.brackets");
    assertEquals(dataFields.size() ,1);
  }

  @Test
  public void parseJson_TestSuccess() throws IOException, ParseException {
    String path = "src/main/resources/tiny-tines-sunset.json";
    String expected = "[{\"name\":\"location\",\"options\":{\"url\":\"http:\\/\\/free.ipwhois.io\\/json\\/\"},\"type\":\"HTTPRequestAgent\"},{\"name\":\"sunset\",\"options\":{\"url\":\"https:\\/\\/api.sunrise-sunset.org\\/json?lat={{location.latitude}}&lng={{location.longitude}}\"},\"type\":\"HTTPRequestAgent\"},{\"name\":\"print\",\"options\":{\"message\":\"Sunset in {{location.city}}, {{location.country}} is at {{sunset.results.sunset}}.\"},\"type\":\"PrintAgent\"}]";

    File file = new File(path);
    String absolutePath = file.getAbsolutePath();

    assertNotNull(TinyTines.ParseJson(absolutePath).get("agents"));
    assertEquals(expected,TinyTines.ParseJson(absolutePath).get("agents").toString());

  }

  @Test(expected = IOException.class)
  public void parseJson_IOExceptionThrown() throws IOException, ParseException {
    String path = "";
    File file = new File(path);
    String absolutePath = file.getAbsolutePath();
    TinyTines.ParseJson(absolutePath).get("agents");

  }

  @Test(expected = ParseException.class)
  public void parseJson_ParseExceptionThrown() throws IOException, ParseException {
    String path = "src/main/resources/tiny-tines-sunset-unvalid-json.json";
    File file = new File(path);
    String absolutePath = file.getAbsolutePath();
    TinyTines.ParseJson(absolutePath);
  }

  @Test
  public void jsonToList_TestSuccess() throws JsonProcessingException {
    String jsonAgents = "[{\"name\":\"test_data\",\"options\":{\"url\":\"https:\\/\\/coding-exercise.tines.io\\/webhook\\/160564ed1b5ea694ca19024c9a7d2889\\/54a35506d279294db2abac754d46685c?email=jane@example.com\"},\"type\":\"HTTPRequestAgent\"}," +
      "{\"name\":\"sunset\",\"options\":{\"url\":\"https:\\/\\/api.sunrise-sunset.org\\/json?lat={{test_data.latitude}}&lng={{test_data.longitude}}&date={{test_data.date}}\"},\"type\":\"HTTPRequestAgent\"}]";
    Map<String, String> data1 = new HashMap();
    data1.put("url", "https://coding-exercise.tines.io/webhook/160564ed1b5ea694ca19024c9a7d2889/54a35506d279294db2abac754d46685c?email=jane@example.com");
    Map<String, String> data2 = new HashMap();
    data2.put("url", "https://api.sunrise-sunset.org/json?lat={{test_data.latitude}}&lng={{test_data.longitude}}&date={{test_data.date}}");

    Agent agent1 = new Agent();
    agent1.setName("test_data");
    agent1.setType("HTTPRequestAgent");
    agent1.setOptions(data1);

    Agent agent2 = new Agent();
    agent2.setName("sunset");
    agent2.setType("HTTPRequestAgent");
    agent2.setOptions(data2);


    TinyTines.JsonToList(jsonAgents, mapper);
    assertEquals(TinyTines.JsonToList(jsonAgents, mapper).size() , 2);
    assertThat(TinyTines.JsonToList(jsonAgents, mapper), hasItems(agent1, agent2));


  }

  @Test(expected = JsonProcessingException.class)
  public void jsonToList_ExceptionThrown() throws JsonProcessingException {
    String jsonAgents = "[{\"name\":\"test_data\",\"options\":{url:https\\/\\/coding-exercise.tines.io\\/webhook\\/160564ed1b5ea694ca19024c9a7d2889\\/54a35506d279294db2abac754d46685c?email=jane@example.com\"},\"type\":\"HTTPRequestAgent\"}]";
      TinyTines.JsonToList(jsonAgents, mapper);
  }

}
