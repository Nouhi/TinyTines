# TinyTines
Files in Resources folder can be used for testing the application .

The application first check if there's a file passed and an argument if there's no file it will exit with status code 1 .
Then parse the Json File create Agent Objects according to the type of Agent 
Once we have a list of Agent we iterate through it while executing tasks of each agent depending of the type :
 * HttpRequestAgent : make and Api call and store the data in a Map accessible to all Agents
 
 * PrintAgent : Populate a string with data from api and print it to screen 


