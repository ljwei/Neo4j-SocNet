package socnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.IterableWrapper;

import data.CreateRandomData;
import static socnet.RelTypes.A_PERSON;
import static socnet.RelTypes.FRIEND;

public class PersonRepository
{
    private final GraphDatabaseService graphDb;
    private final Index<Node> index;
    private final Node personRefNode;

    public PersonRepository( GraphDatabaseService graphDb)
    {
        this.graphDb = graphDb;

        try ( Transaction tx = graphDb.beginTx() )
        {
        	index = graphDb.index().forNodes("personIndex");
        	tx.success();
        }
        
        personRefNode = getPersonsRootNode( graphDb );		
        //init();
    }

    private Node getPersonsRootNode( GraphDatabaseService graphDb )
    {
    	try ( Transaction tx = graphDb.beginTx() )
        {
	        Index<Node> referenceIndex = graphDb.index().forNodes( "reference");
	        IndexHits<Node> result = referenceIndex.get( "reference", "person" );
	        if (result.hasNext())
	        {
	            return result.next();
	        }
	
	        Node refNode = this.graphDb.createNode();
	        refNode.setProperty( "reference", "persons" );
	        referenceIndex.add( refNode, "reference", "persons" );
	        
	        tx.success();
	        
	        return refNode;
        }
    }
    
    //initialize the database, run once.
    private void init()
    {
    	BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(CreateRandomData.DATA_PERSON)));
			
			//create person node.
			String line = br.readLine();
			String[] strings = line.split(" ");
			int nodeCount = Integer.parseInt(strings[0]);
			for (int i = 0; i < nodeCount; i++) {
				createPerson("person" + i);
			}
			
			//create friend relationship.
			while (null != (line = br.readLine())) {
				String[] names = line.split(" ");

				addFriend(getPersonByName(names[1]), getPersonByName(names[0]));
			}
			
			//create status node.
			br = new BufferedReader(new FileReader(new File(CreateRandomData.DATA_STATUS)));
			while (null != (line = br.readLine())) {
				String[] status = line.split(" ");
				getPersonByName(status[0]).addStatus(status[1]);
			}
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    

    public void addFriend( Person fromPerson, Person toPerson )
    {
    	try ( Transaction tx = graphDb.beginTx() ) {
	        if ( !fromPerson.equals( toPerson ) )
	        {
	            Relationship friendRel = fromPerson.getFriendRelationshipTo( toPerson );
	            if ( friendRel == null )
	            {
	            	fromPerson.getUnderlyingNode().createRelationshipTo( toPerson.getUnderlyingNode(), FRIEND );
	            }
	        }
	        tx.success();
    	}
    }
    
    public Person createPerson( String name ) throws Exception
    {
        // to guard against duplications we use the lock grabbed on ref node
        // when
        // creating a relationship and are optimistic about person not existing
    	try ( Transaction tx = graphDb.beginTx() ) {
	        Node newPersonNode = graphDb.createNode();
	        newPersonNode.addLabel(MyLabel.PERSON);
	        personRefNode.createRelationshipTo( newPersonNode, A_PERSON );
	        // lock now taken, we can check if  already exist in index
	        Node alreadyExist = index.get( Person.NAME, name ).getSingle();
	        if ( alreadyExist != null )
	        {
	            throw new Exception( "Person with this name already exists " );
	        }
	        newPersonNode.setProperty( Person.NAME, name );
	        index.add( newPersonNode, Person.NAME, name );
	        
	        tx.success();
	        return new Person( newPersonNode );
    	}
    }

    public Person getPersonByName( String name )
    {
    	Node personNode;
    	try ( Transaction tx = graphDb.beginTx() ) {
	        personNode = index.get( Person.NAME, name ).getSingle();
	        if ( personNode == null )
	        {
	            return null;
	        }
	        tx.success();
    	}
    	return new Person( personNode );
    }

    public void deletePerson( Person person )
    {
        Node personNode = person.getUnderlyingNode();
        index.remove( personNode, Person.NAME, person.getName() );
        for ( Person friend : person.getFriends() )
        {
            person.removeFriend( friend );
        }
        personNode.getSingleRelationship( A_PERSON, Direction.INCOMING ).delete();

        for ( StatusUpdate status : person.getStatus() )
        {
            Node statusNode = status.getUnderlyingNode();
            for ( Relationship r : statusNode.getRelationships() )
            {
                r.delete();
            }
            statusNode.delete();
        }

        personNode.delete();
    }

    public Iterable<Person> getAllPersons()
    {
        return new IterableWrapper<Person, Relationship>(
                personRefNode.getRelationships( A_PERSON ) )
        {
            @Override
            protected Person underlyingObjectToObject( Relationship personRel )
            {
                return new Person( personRel.getEndNode() );
            }
        };
    }
}