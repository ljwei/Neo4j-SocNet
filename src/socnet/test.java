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
		
		PersonRepository pr = new PersonRepository(ctn.getGraphDb());
		
        Person p = pr.getPersonByName("person2");
        // START SNIPPET: traversebasetraverser
        Iterable<Person> persons;
        try ( Transaction tx = ctn.getGraphDb().beginTx() )
        {
        	persons = p.getFriends();
        	for (Person friend : persons) {
            	System.out.println(friend.toString());
            	
            	for (StatusUpdate status : friend.getStatus()) {
            		System.out.println(status.getStatusText() + status.getDate());
            	}
            	break;
            }
        	tx.success();
        }
        
        
        try ( Transaction tx = ctn.getGraphDb().beginTx() )
        {
	        Iterator<StatusUpdate> itStatus = p.friendStatuses();
	        while (itStatus.hasNext())
	        {
	        	StatusUpdate status = itStatus.next();
		        System.out.println(status.getStatusText() + status.getPerson());
	        }
	        tx.success();
        }
	}

}
