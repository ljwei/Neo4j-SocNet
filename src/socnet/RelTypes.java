package socnet;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType
{
    REF_PERSONS,
    A_PERSON,
    STATUS,
    NEXT,
    FRIEND
}