package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import edu.uob.Model.*;
import edu.uob.Utils.ClassContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    static GameEngine gameEngine;
    // TODO handle wrong spelled commands like llok

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here
        gameEngine = new GameEngine();
        try {
            parseEntityFile(entitiesFile);
            parseActionsFile(actionsFile);
        }
        catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }

    private void parseActionsFile(File actionsFile) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionsFile);
        Element root = document.getDocumentElement();
        NodeList actions = root.getChildNodes();
        for(int index=1;index<actions.getLength();index+=2){
            Element curElement = (Element) actions.item(index);
            // narration too pls
            HashSet<String> triggers = extractTriggers((Element) curElement.getElementsByTagName("triggers").item(0));
            String narration = curElement.getElementsByTagName("narration").item(0).getTextContent();
            HashSet<GameEntity> subjects = extractEntities((Element) curElement.getElementsByTagName("subjects").item(0));
            HashSet<GameEntity> consumed = extractEntities((Element) curElement.getElementsByTagName("consumed").item(0));
            HashSet<GameEntity> produced = extractEntities((Element) curElement.getElementsByTagName("produced").item(0));
            GameAction gameAction = new GameAction.GameActionBuilder(narration, triggers, subjects)
                    .setConsumed(consumed)
                    .setProduced(produced)
                    .build();
            gameEngine.addAction(gameAction);
        }
    }

    private HashSet<String> extractTriggers(Element triggers) {
        HashSet<String> result = new HashSet<>();
        NodeList phrases = triggers.getElementsByTagName("keyphrase");
        for(int index=0;index<phrases.getLength();index++){
            String phrase = phrases.item(index).getTextContent();
            result.add(phrase);
        }
        return result;
    }

    private HashSet<GameEntity> extractEntities(Element consumed) throws Exception{
        NodeList entities = consumed.getElementsByTagName("entity");
        HashSet<GameEntity> result = new HashSet<>();
        for(int index=0;index<entities.getLength();index++){
            String entityName = entities.item(index).getTextContent();
            result.add(gameEngine.getEntityByName(entityName));
        }
        return result;
    }


    private void parseEntityFile(File entitiesFile) throws FileNotFoundException, ParseException {
        ArrayList<Graph> sections = getSections(entitiesFile);
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        Location startingLocation = null;
        HashMap<String, Location> allLocations = new HashMap<String, Location>();
        for(Graph location : locations){
            var locationDetails = location.getNodes(false);
            String name = locationDetails.get(0).getId().getId();
            String description = locationDetails.get(0).getAttribute("description");
            ArrayList<Artefact> artefacts = extractArtefacts(location.getSubgraphs());
            ArrayList<Furniture> furniture = extractFurniture(location.getSubgraphs());
            ArrayList<GameCharacter> gameCharacters = extractGameChars(location.getSubgraphs());

            Location currentLocation = new Location.LocationBuilder(name, description, gameEngine)
                    .setGameCharacters(gameCharacters)
                    .setArtefacts(artefacts)
                    .setFurniture(furniture)
                    .build();
            allLocations.put(currentLocation.getName(), currentLocation);
            artefacts.forEach((e) -> e.setLocation(currentLocation));
            furniture.forEach((e) -> e.setLocation(currentLocation));
            gameCharacters.forEach((e) -> e.setLocation(currentLocation));
            if(startingLocation == null){
                startingLocation = currentLocation;
            }
        }
        ArrayList<Edge> paths = sections.get(1).getEdges();
        connectLocations(paths, allLocations);
        gameEngine.setStartingLocation(startingLocation);
        gameEngine.addLocations(allLocations);
    }

    private void connectLocations(ArrayList<Edge> paths, HashMap<String, Location> allLocations) {
        for(Edge path : paths){
            String source = path.getSource().getNode().getId().getId();
            String destination = path.getTarget().getNode().getId().getId();
            Location sourceLocation = allLocations.get(source);
            Location destinationLocation = allLocations.get(destination);
            sourceLocation.addToLocation(destinationLocation);
        }
    }

    private ArrayList<Graph> getSections(File entitiesFile) throws ParseException, FileNotFoundException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFile);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();
        return sections;
    }

    private ArrayList<GameCharacter> extractGameChars(ArrayList<Graph> locationDetails) {
        ArrayList<GameCharacter> gameCharacters = new ArrayList<>();
        for(Graph subgraph : locationDetails){
            if(subgraph.getId().getId().equals("characters")){
                var nodeList = subgraph.getNodes(false);
                for(var node : nodeList){
                    String name = node.getId().getId();
                    String description =  node.getAttribute("description");
                    GameCharacter  gameCharacter = new GameCharacter(name, description);
                    gameCharacters.add(gameCharacter);
                }
                break;
            }
        }
        return gameCharacters;
    }

    private ArrayList<Furniture> extractFurniture(ArrayList<Graph> locationDetails) {
        ArrayList<Furniture> furniture = new ArrayList<>();
        for(Graph subgraph : locationDetails){
            if(subgraph.getId().getId().equals("furniture")){
                var nodeList = subgraph.getNodes(false);
                for(var node : nodeList){
                    String name = node.getId().getId();
                    String description =  node.getAttribute("description");
                    Furniture  currentFurniture = new Furniture(name, description);
                    furniture.add(currentFurniture);
                }
                break;
            }
        }
        return furniture;
    }

    private ArrayList<Artefact> extractArtefacts(ArrayList<Graph> locationDetails) {
        ArrayList<Artefact> artefacts = new ArrayList<>();
        for(Graph subgraph : locationDetails){
            if(subgraph.getId().getId().equals("artefacts")){
                var nodeList = subgraph.getNodes(false);
                for(var node : nodeList){
                    String name = node.getId().getId();
                    String description =  node.getAttribute("description");
                    Artefact  artefact = new Artefact(name, description);
                    artefacts.add(artefact);
                }
                break;
            }
        }
        return artefacts;
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        CommandParser cmdParser;
        try {
            ClassContainer classContainer = ClassContainer.getInstance();
            cmdParser = new CommandParser(command, gameEngine);
            classContainer.setCommandParser(cmdParser);
            String playerName = cmdParser.getPlayerName();
            ArrayList<String> tokenizedCMD = cmdParser.getTokenizedCMD();
            cmdParser.executeCommand(playerName, tokenizedCMD);
        }
        catch(Exception exception){
            return exception.getMessage();
        }
        return cmdParser.getResponse();
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
