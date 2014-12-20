package socnet;

import java.util.Date;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.helpers.collection.IteratorUtil;

import static socnet.RelTypes.NEXT;
import static socnet.RelTypes.STATUS;

public class StatusUpdate
{
    private final Node underlyingNode;
    static final String TEXT = "TEXT";
    static final String DATE = "DATE";

    public StatusUpdate( Node underlyingNode )
    {

        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode()
    {
        return underlyingNode;
    }

    public Person getPerson()
    {
        return new Person( getPersonNode() );
    }

    private Node getPersonNode()
    {
        TraversalDescription traversalDescription = underlyingNode.getGraphDatabase()
                .traversalDescription()
                .depthFirst()
                .relationships( NEXT, Direction.INCOMING )
                .relationships( STATUS, Direction.INCOMING )
                .evaluator( Evaluators.includeWhereLastRelationshipTypeIs( STATUS ) );

        Traverser traverser = traversalDescription.traverse( getUnderlyingNode() );

        return IteratorUtil.singleOrNull( traverser.iterator() ).endNode();
    }

    public String getStatusText()
    {
        return (String) underlyingNode.getProperty( TEXT );
    }

    public Date getDate()
    {
        Long l = (Long) underlyingNode.getProperty( DATE );

        return new Date( l );
    }

}
