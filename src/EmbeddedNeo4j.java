import java.io.File;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;

public class EmbeddedNeo4j
{
    public enum RelTypes implements RelationshipType
    {
        NEO_NODE,
        KNOWS,
        CODED_BY
    }

    private static final String MATRIX_DB = "target/matrix-new-db";
    private GraphDatabaseService graphDb;
    private long matrixNodeId;

    public static void main( String[] args )
    {
        EmbeddedNeo4j matrix = new EmbeddedNeo4j();
        matrix.setUp();
        System.out.println( matrix.printNeoFriends() );
        System.out.println( matrix.printMatrixHackers() );
        matrix.shutdown();
    }

    public void setUp()
    {
        //deleteFileOrDirectory( new File( MATRIX_DB ) );
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( MATRIX_DB );
        registerShutdownHook();
        //createNodespace();
    }

    public void shutdown()
    {
        graphDb.shutdown();
    }

    public void createNodespace()
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            // Create matrix node
            Node matrix = graphDb.createNode();
            matrixNodeId = matrix.getId();

            // Create Neo
            Node thomas = graphDb.createNode();
            thomas.setProperty( "name", "Thomas Anderson" );
            thomas.setProperty( "age", 29 );

            // connect Neo/Thomas to the matrix node
            matrix.createRelationshipTo( thomas, RelTypes.NEO_NODE );

            Node trinity = graphDb.createNode();
            trinity.setProperty( "name", "Trinity" );
            Relationship rel = thomas.createRelationshipTo( trinity,
                    RelTypes.KNOWS );
            rel.setProperty( "age", "3 days" );
            Node morpheus = graphDb.createNode();
            morpheus.setProperty( "name", "Morpheus" );
            morpheus.setProperty( "rank", "Captain" );
            morpheus.setProperty( "occupation", "Total badass" );
            thomas.createRelationshipTo( morpheus, RelTypes.KNOWS );
            rel = morpheus.createRelationshipTo( trinity, RelTypes.KNOWS );
            rel.setProperty( "age", "12 years" );
            Node cypher = graphDb.createNode();
            cypher.setProperty( "name", "Cypher" );
            cypher.setProperty( "last name", "Reagan" );
            trinity.createRelationshipTo( cypher, RelTypes.KNOWS );
            rel = morpheus.createRelationshipTo( cypher, RelTypes.KNOWS );
            rel.setProperty( "disclosure", "public" );
            Node smith = graphDb.createNode();
            smith.setProperty( "name", "Agent Smith" );
            smith.setProperty( "version", "1.0b" );
            smith.setProperty( "language", "C++" );
            rel = cypher.createRelationshipTo( smith, RelTypes.KNOWS );
            rel.setProperty( "disclosure", "secret" );
            rel.setProperty( "age", "6 months" );
            Node architect = graphDb.createNode();
            architect.setProperty( "name", "The Architect" );
            smith.createRelationshipTo( architect, RelTypes.CODED_BY );

            tx.success();
        }
    }

    /**
     * Get the Neo node. (a.k.a. Thomas Anderson node)
     * 
     * @return the Neo node
     */
    private Node getNeoNode()
    {
        return graphDb.getNodeById( matrixNodeId )
                .getSingleRelationship( RelTypes.NEO_NODE, Direction.OUTGOING )
                .getEndNode();
    }

    public String printNeoFriends()
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            Node neoNode = getNeoNode();
            // START SNIPPET: friends-usage
            int numberOfFriends = 0;
            String output = neoNode.getProperty( "name" ) + "'s friends:\n";
            Traverser friendsTraverser = getFriends( neoNode );
            for ( Path friendPath : friendsTraverser )
            {
                output += "At depth " + friendPath.length() + " => "
                          + friendPath.endNode()
                                  .getProperty( "name" ) + "\n";
                numberOfFriends++;
            }
            output += "Number of friends found: " + numberOfFriends + "\n";
            // END SNIPPET: friends-usage
            return output;
        }
    }

    // START SNIPPET: get-friends
    private Traverser getFriends(
            final Node person )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .breadthFirst()
                .relationships( RelTypes.KNOWS, Direction.OUTGOING )
                .evaluator( Evaluators.excludeStartPosition() );
        return td.traverse( person );
    }
    // END SNIPPET: get-friends

    public String printMatrixHackers()
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            // START SNIPPET: find--hackers-usage
            String output = "Hackers:\n";
            int numberOfHackers = 0;
            Traverser traverser = findHackers( getNeoNode() );
            for ( Path hackerPath : traverser )
            {
                output += "At depth " + hackerPath.length() + " => "
                          + hackerPath.endNode()
                                  .getProperty( "name" ) + "\n";
                numberOfHackers++;
            }
            output += "Number of hackers found: " + numberOfHackers + "\n";
            // END SNIPPET: find--hackers-usage
            return output;
        }
    }

    // START SNIPPET: find-hackers
    private Traverser findHackers( final Node startNode )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .breadthFirst()
                .relationships( RelTypes.CODED_BY, Direction.OUTGOING )
                .relationships( RelTypes.KNOWS, Direction.OUTGOING )
                .evaluator(
                        Evaluators.includeWhereLastRelationshipTypeIs( RelTypes.CODED_BY ) );
        return td.traverse( startNode );
    }
    // END SNIPPET: find-hackers

    private void registerShutdownHook()
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime()
                .addShutdownHook( new Thread()
                {
                    @Override
                    public void run()
                    {
                        graphDb.shutdown();
                    }
                } );
    }

    private static void deleteFileOrDirectory( final File file )
    {
        if ( !file.exists() )
        {
            return;
        }

        if ( file.isDirectory() )
        {
            for ( File child : file.listFiles() )
            {
                deleteFileOrDirectory( child );
            }
        }
        else
        {
            file.delete();
        }
    }
}