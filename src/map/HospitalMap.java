/*
* Software Engineering 3733, Worcester Polytechnic Institute
* Team H
* Code produced for Iteration1
* Original author(s): Nicholas Fajardo, Tyrone Patterson, Leo Grande, Meghana Bhatia
* The following code
*/

package map;
import database.edgeDatabase;
import database.nodeDatabase;
import exceptions.InvalidNodeException;
import search.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HospitalMap{
    private ArrayList<Node> nodeMap;
    private ArrayList<Edge> edgeMap;
    private ArrayList<SearchStrategy> posSerchStrat;
    private SearchContext search;
    private static HospitalMap map;

    //Constructors
    private HospitalMap() {
        nodeMap = new ArrayList<>();
        edgeMap = new ArrayList<>();
        search = new SearchContext(new AStarSearch());
        posSerchStrat = new ArrayList<>();
        posSerchStrat.add(new AStarSearch());
        posSerchStrat.add(new BeamSearch(3));
        posSerchStrat.add(new BreadthFirstSearch());
        posSerchStrat.add(new DepthFirstSearch());
        posSerchStrat.add(new DijkstrasSearch());

        nodeMap.addAll(nodeDatabase.getNodes());
        edgeMap.addAll(edgeDatabase.getEdges());

    }

    public static HospitalMap getMap() {
        if(map == null){
            map = new HospitalMap();
        }
        return map;
    }

    //Helper Methods

    //Methods to add a new, isolated node to the map
    public void addNode(Node node){
        nodeMap.add(node);
        nodeDatabase.addNode(node);
    }

    public void addNode(String ID, String x, String y, String floor, String building, String type, String longName, String shortName, String team){
        nodeMap.add(new Node(ID ,x, y, floor, building, type, longName, shortName, team));
        nodeDatabase.addNode(new Node(ID,x,y,floor,building,type,longName,shortName, team));
    }

    public void addNodeandEdges(String ID, String x, String y, String floor, String building, String type, String longName, String shortName, String team, ArrayList<Node> connections){
        Node temp = new Node(ID ,x, y, floor, building, type, longName, shortName, team);
        nodeMap.add(temp);
        for(int i = 0; i < connections.size(); i++){
            addEdge(temp,connections.get(i));
        }
        nodeDatabase.addNode(new Node(ID,x,y,floor,building,type,longName,shortName, team));
    }

    public void editNode(Node node, String x, String y, String floor, String building, String type, String longName, String shortName){
        node.setBuilding(building);
        node.setFloor(floor);
        node.setLongName(longName);
        node.setShortName(shortName);
        node.setX(x);
        node.setY(y);
        node.setType(type);
        nodeDatabase.modifyNode(node);
    }

    public void removeNode(Node node){
        //get all connections and remove from edgeMap
        int size = node.getConnections().size();
        for(int i = 0; i < size; i++){
            //have to delete 0 index each time because array shrinks after each iteration
            removeEdge(node.getConnections().get(0));
        }
        nodeMap.remove(node);
        nodeDatabase.deleteNode(node);
    }

    public void addEdge(Edge edge){
        edgeMap.add(edge);
    }

    //this is the one used by the engine and therefore the tests
    public void addEdge(Node nodeOne,Node nodeTwo){
        Edge tempEdge = new Edge(nodeOne,nodeTwo);
        edgeMap.add(tempEdge);
        edgeDatabase.addEdge(tempEdge);
    }

    public void removeEdge(Edge edge){
        edge.deleteConnection();
        edgeMap.remove(edge);
        edgeDatabase.deleteAnyEdge(edge);
    }

    public void editEdge(Edge edge, Node oldNode, Node newNode) throws InvalidNodeException {
        try {
            edge.replaceNode(oldNode, newNode);
        } catch (Exception e){
            throw new InvalidNodeException("the edge does not contain the old node");
        }
        //edgeDatabase.modifyEdge(edge);
    }

    public Node findNode(String nodeID){
        int tempLoc = nodeMap.indexOf(new Node(nodeID,null,null,null,null,null,null,null, null));

        return nodeMap.get(tempLoc);
    }

    public Path findPath(Node start, Node end){
        return search.findPath(start,end);
    }

    public Path findNearest(Node start, String type){
        BreadthFirstSearch search = new BreadthFirstSearch();
        return search.findPathBy(start, type);
    }

    public Path findPathPitStop(ArrayList<Node> stops) throws InvalidNodeException {
        AStarSearch search = new AStarSearch();
        return search.findPathPitStop(stops);
    }

    //Getters
    public ArrayList<Node> getNodeMap() {
        return nodeMap;
    }
    public ArrayList<Edge> getEdgeMap() {
        return edgeMap;
    }
    public ArrayList<Node> getNodesByFloor(int floor){
        ArrayList<Node> output = new ArrayList<>();
        for(int i = 0; i < nodeMap.size(); i++){
            if(nodeMap.get(i).getFloor().getNodeMapping() == floor){
                output.add(nodeMap.get(i));
            }
        }
        return output;
    }

    //Setters
    public void setSearchStrategy(SearchStrategy searchStrategy){
        search.setStrategy(searchStrategy);
    }

    public ArrayList<SearchStrategy> getSearches() {
        return posSerchStrat;
    }


    public List<Node> getNodesBy(Function<Node, Boolean> function){
        return this.nodeMap.stream().filter(function::apply).collect(Collectors.toList());
    }
}