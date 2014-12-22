package socnet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ConnectToNeo4j
{
    private static final String PATH_DB = "target/socnet-db";
    private GraphDatabaseService graphDb;

    public ConnectToNeo4j() {
    	setUp();
    }
    
    public GraphDatabaseService getGraphDb() {
    	return graphDb;
    }
    
    private void setUp()
    {
    	//deleteFileOrDirectory( new File( PATH_DB ) );
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( PATH_DB );
        registerShutdownHook();
        //createNodespace();
    }


    
    public void shutdown()
    {
        graphDb.shutdown();
    }

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
