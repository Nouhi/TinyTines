import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;
import com.tines.model.Agent;
import com.tines.model.HTTPRequestAgent;

import com.tines.model.PrintAgent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TinyTines {

  public static void main(String args[])  {

    final ObjectMapper mapper = new ObjectMapper();
    Map<String, Map<String, JSONObject>> responseMap = new HashMap<>();
    List<Agent> agentsList=new ArrayList<>();
    JSONObject result;
    String endpoint ,agents ;
    String path = "";
    List<String> dataFields;
    File file;
    Map<String, JSONObject> apiResponse;

    if (args.length > 0) {
      file = new File(args[0]);
      path = file.getAbsolutePath();

    } else {
      System.out.println("Please add you story file....... ");
      System.exit(1);
    }
    try {
      result = ParseJson(path);
      agents = result.get("agents").toString();
      agentsList = JsonToList(agents, mapper);
    } catch (ParseException | IOException e) {
      System.err.println(e.getMessage());
    }



    //get agent names and add them as keys for the responses from each corresponding HTTPRequestAgent
    agentsList.forEach(agent -> responseMap.put(agent.getName(), null));


    for (Agent agent : agentsList) {
      if (agent.getType().compareTo("HTTPRequestAgent") == 0) {
        endpoint = agent.getOptions().get("url");
        dataFields = extractDataFields(endpoint);
        endpoint = getValue(dataFields, responseMap, endpoint);
        ClientResponse clientResponse;
        HTTPRequestAgent httpRequestAgent;

        try {
          httpRequestAgent=new HTTPRequestAgent(agent);
          clientResponse=  httpRequestAgent.executeTask(endpoint, mapper);
          String output = clientResponse.getEntity(String.class);
          if (httpRequestAgent.getName().compareTo("fact") == 0) {
            output = "{\"" + "text" + "\":" + "\"" + output + "\"}";
          }
          apiResponse = mapper.readValue(output, HashMap.class);
          responseMap.replace(agent.getName(), apiResponse);
        } catch (MalformedURLException | URISyntaxException | JsonProcessingException ex) {
          System.out.println(ex.getMessage());
        }
      } else if (agent.getType().compareTo("PrintAgent") == 0) {

        endpoint = agent.getOptions().get("message");
        dataFields = extractDataFields(endpoint);
        endpoint = getValue(dataFields, responseMap, endpoint);
        new PrintAgent(agent).executeTask(endpoint);
      }
    }
  }

  /**
   * @param matches           a list of the data fields that needs to be populated
   * @param data              Map of API responses from different agents
   * @param unpopulatedString the string containing fields that need to be populated with data from response
   * @return
   */

  public static String getValue(List<String> matches, Map<String, Map<String, JSONObject>> data, String unpopulatedString) {

    List<String> tokens;
    for (String match : matches) {

      tokens = Arrays.asList(match.split("\\."));
      Map<String, JSONObject> tempMap = data.get(tokens.get(0));



      if (tokens.size() <= 2 && !tokens.isEmpty()) {
        String key = tokens.get(1);
        if (tempMap == null) {
          unpopulatedString = unpopulatedString.replace("{{" + match + "}}", "");
        } else {
          Object obj = tempMap.get(key);
          if (obj instanceof Integer) {
            Integer rep = (Integer) obj;
            unpopulatedString = unpopulatedString.replace("{{" + match + "}}", Integer.toString(rep));
          } else {
            unpopulatedString = unpopulatedString.replace("{{" + match + "}}", obj.toString());
          }
        }
      }
      else if (tokens.size() > 2 && !tokens.isEmpty()) {

        if (tempMap == null) {
          unpopulatedString = unpopulatedString.replace("{{" + match + "}}", "");
        } else {
            String agentName = tokens.get(1);
            Object array = (Object) tempMap.get(agentName);
            LinkedHashMap test1 = (LinkedHashMap) array;
            String missingValue = tokens.get(tokens.size() - 1);

            while (test1.values().toString().contains(missingValue)) {
              test1 = (LinkedHashMap) test1.get(test1.keySet().toArray()[0]);
            }
            unpopulatedString = unpopulatedString.replace("{{" + match + "}}", test1.values().toArray()[0].toString());
          }
        }
      }

    return unpopulatedString;
  }

  /**
   * Method to extract the data fields that need to be replaced ie "data.sunset.date"
   *
   * @param unpopulatedString String containing data field that need to be populated
   * @return list of data fileds
   */
  public static List<String> extractDataFields(String unpopulatedString) {
    List<String> matches = new ArrayList<>();
    Matcher m = Pattern.compile("\\{\\{([^{}]+)}}").matcher(unpopulatedString);
    while (m.find()) {
      matches.add(m.group(1));
    }
    return matches;
  }

  /**
   * Takes a path to json file and returns Json Object based in the json file (stories file)
   *
   * @param path
   * @return
   */
  public static JSONObject ParseJson(String path) throws IOException, ParseException {
    JSONParser parser = new JSONParser();
    Object obj = null;
    obj = parser.parse(new FileReader(path));
    JSONObject jsonObject = (JSONObject) obj;

    return jsonObject;
  }

  /**
   * takes a json array of agents and returns a list
   *
   * @param jsonStr take a string containing a json Array of Agent Objects
   * @param mapper
   * @return
   * @throws JsonProcessingException
   */
  public static List<Agent> JsonToList(String jsonStr, ObjectMapper mapper) throws JsonProcessingException {
    List<Agent> agentsList = null;
    Agent[] agents = mapper.readValue(jsonStr, Agent[].class);
    agentsList = Arrays.asList(agents);
    return agentsList;
  }

}
