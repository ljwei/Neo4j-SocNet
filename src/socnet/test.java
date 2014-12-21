package socnet;

import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.Node;

import data.CreateRandomData;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//CreateRandomData crd = new CreateRandomData(100, 10);
		ConnectToNeo4j ctn = new ConnectToNeo4j();
		ctn.setUp();
		
		PersonRepository pr = new PersonRepository(ctn.getGraphDb());
		
		try ( Transaction tx = ctn.getGraphDb().beginTx() ) {

	        Person p = pr.getPersonByName("person2");
	        // START SNIPPET: traversebasetraverser
	        for ( Person friend : p.getFriendsOfFriends() )
	        {
		        System.out.println(friend.getName());
	        }
		}
	}

}
