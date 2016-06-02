package org.python.ReL;

import java.util.*;
import java.io.*;

import com.thinkaurelius.titan.core.schema.SchemaStatus;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.graphdb.database.management.GraphIndexStatusReport;
import com.thinkaurelius.titan.graphdb.database.management.ManagementSystem;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.python.ReL.WDB.database.wdb.metadata.*;
import com.thinkaurelius.titan.core.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;
import org.python.ReL.WDB.database.wdb.metadata.ClassDef;
import org.python.ReL.WDB.database.wdb.metadata.IndexDef;
import org.python.ReL.WDB.database.wdb.metadata.Query;
import org.python.ReL.WDB.database.wdb.metadata.WDBObject;

/**
 * @author Alvin Deng
 * @author Raymond Chee
 * @date 05/26/2016
 *
 * TitanDB Adapter for CarnotKE
 */

public class TitanNoSQLDatabase extends DatabaseInterface {

    private static TitanGraph graph = null;
    private static File INSTALLATION_ROOT;
    private static String PROPERTIES_PATH;

    /**
     * Initialize TitanDB with defined configurations and populate the database with root node.
     */
    private void initTitanDB() {
        validateInstallationRoot();
        graph = TitanFactory.open(PROPERTIES_PATH);
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            logger.setLevel(Level.OFF);
        }
        
        TitanManagement mgmt = graph.openManagement();
        boolean initGraph = mgmt.containsPropertyKey("classDefName");

        if(!initGraph) {
            initGraph = true;
            PropertyKey classDefName = mgmt.makePropertyKey("classDefName").dataType(String.class).make();
            VertexLabel classDefLabel = mgmt.makeVertexLabel("classDef").make();
            TitanGraphIndex classDefIndex = mgmt.buildIndex("byClassDefName", Vertex.class).addKey(classDefName).
                    indexOnly(classDefLabel).buildCompositeIndex();
            mgmt.commit();
            try {
                GraphIndexStatusReport classDefReport = ManagementSystem.awaitGraphIndexStatus(graph, "byClassDefName").
                        status(SchemaStatus.REGISTERED).call();
            } catch (Exception e) {
                System.out.println("e = " + e.toString());
            }

            mgmt = graph.openManagement();
            PropertyKey WDBObjectName = mgmt.makePropertyKey("WDBObjectName").dataType(String.class).make();
            VertexLabel WDBObjectLabel = mgmt.makeVertexLabel("WDBObject").make();
            TitanGraphIndex WDBObjectIndex = mgmt.buildIndex("byWDBObjectName", Vertex.class).addKey(WDBObjectName).
                    indexOnly(WDBObjectLabel).buildCompositeIndex();
            mgmt.commit();
            try {
                GraphIndexStatusReport WDBObjectReport = ManagementSystem.awaitGraphIndexStatus(graph, "byWDBObjectName").
                        status(SchemaStatus.REGISTERED).call();
            } catch (Exception e) {
                System.out.println("e = " + e.toString());
            }
        }
    }

    /**
     * Initialize the INSTALLATION_ROOT path
     */
    public void validateInstallationRoot() {
        final String serverRoot = System.getenv("INSTALLATION_ROOT");
        if (serverRoot == null) {
        }

        INSTALLATION_ROOT = new File(serverRoot);
        if (!INSTALLATION_ROOT.isDirectory()) {

        }
        PROPERTIES_PATH = new File(serverRoot, "CarnotKE.properties").getAbsolutePath();
    }

    /**
     * Default constructor to support TitanNoSQLAdapter.
     */
    public TitanNoSQLDatabase() {
        this.adapter = new TitanNoSQLAdapter(this);
        initTitanDB();
    }

    /* Using Bo's implementation logic for WDBObjects */

    /**
     * Search through the graph and return a proper ClassDef Vertex
     * @param classDefName Name of the ClassDef
     * @return A ClassDef Vertex
     */
    private Vertex getClassDefVertex(String classDefName) {
        GraphTraversalSource g = graph.traversal();
        GraphTraversal<Vertex, Vertex> graphTraversal = g.V().has("classDefName", classDefName);

        while (graphTraversal.hasNext()) {
            Vertex currentVertex = graphTraversal.next();
            if(currentVertex.property("classDefName").isPresent()) {
                if (currentVertex.property("classDefName").value().equals(classDefName)) {
                    return currentVertex;
                }
            }
        }
        return null;
    }

    /**
     * Inserting a ClassDef into the graph
     * @param classDef ClassDef object
     */
    private void putClassDef(ClassDef classDef) {
        Vertex classDefVertex = getClassDefVertex(classDef.name);
        if(classDefVertex == null) {
            classDefVertex = graph.addVertex(T.label, "classDef", "classDefName", classDef.name);
        }
        final byte[] data = SerializationUtils.serialize(classDef);
        classDefVertex.property("classDefData", data);
        graph.tx().commit();
        System.out.println("Inserting classDef " + classDef.name + " complete");
    }

    /**
     * Returning a ClassDef object from the graph given the name
     * @param classDefName The name of the ClassDef
     * @return A ClassDef object with an appropriate name
     */
    private ClassDef getClassDef(String classDefName) {
        GraphTraversalSource g = graph.traversal();
        GraphTraversal<Vertex, Vertex> graphTraversal = g.V().has("classDefName", classDefName);

        while (graphTraversal.hasNext()) {
            Vertex currentVertex = graphTraversal.next();
            if(currentVertex.property("classDefName").isPresent()) {
                if (currentVertex.property("classDefName").value().equals(classDefName)) {
                    return (ClassDef) SerializationUtils.deserialize((byte[]) currentVertex.property("classDefData").value());
                }
            }
        }
        return null;
    }

    /**
     * Return a WDBObject Vertex given its WDBObject UID
     * @param Uid UID from WDBObject
     * @return A WDBObject Vertex from the graph
     */
    private Vertex getWDBObjectVertex(Integer Uid) {
        GraphTraversalSource g = graph.traversal();
        GraphTraversal<Vertex, Vertex> graphTraversal = g.V().has("WDBObjectName", "" + Uid);
        while (graphTraversal.hasNext()) {
            Vertex currentVertex = graphTraversal.next();
            if(currentVertex.property("WDBObjectName").isPresent()) {
                if (currentVertex.property("WDBObjectName").value().equals("" + Uid)) {
                    return currentVertex;
                }
            }
        }
        return null;
    }

    /**
     * Inserting a WDBObject into the graph
     * @param wdbObject The WDBObject
     */
    private void putWDBObject(WDBObject wdbObject) {

        Vertex WDBObjectVertex = getWDBObjectVertex(wdbObject.getUid());
        if(WDBObjectVertex == null) {
            WDBObjectVertex = graph.addVertex(T.label, "WDBObject", "WDBObjectName", "" + wdbObject.getUid());
        }
        final byte[] data = SerializationUtils.serialize(wdbObject);
        WDBObjectVertex.property("WDBObjectData", data);
        graph.tx().commit();
        System.out.println("Inserting WDBObject " + wdbObject.getUid() + " complete");
    }

    /**
     * Returning a WDBObject in the graph
     * @param className Name of the ClassDef
     * @param Uid UID of the WDBObject
     * @return The WDBObject from the graph
     */
    private WDBObject getWDBObject(String className, Integer Uid) {
        GraphTraversalSource g = graph.traversal();
        GraphTraversal<Vertex, Vertex> graphTraversal = g.V().has("WDBObjectName", "" + Uid);
        while (graphTraversal.hasNext()) {
            Vertex currentVertex = graphTraversal.next();
            if(currentVertex.property("WDBObjectName").isPresent()) {
                if (currentVertex.property("WDBObjectName").value().equals("" + Uid)) {
                    return (WDBObject) SerializationUtils.deserialize((byte[]) currentVertex.property("WDBObjectData").value());
                }
            }
        }
        return null;
    }

    private class TitanNoSQLAdapter implements Adapter {

        private TitanNoSQLDatabase db;

        /**
         * Constructor of TitanNoSQLDatabase.
         *
         * @param db TitanNoSQLDatabase Object
         */
        private TitanNoSQLAdapter(TitanNoSQLDatabase db) {
            this.db = db;
        }

        /**
         * Inserting a new ClassDef into the graph.
         *
         * @param cd ClassDef that needs to be inserted
         */
        @Override
        public void putClass(ClassDef cd) {
            db.putClassDef(cd);
        }

        /**
         * Searches for a ClassDef that matches the query in the graph.
         *
         * @param query Query that searches for the ClassDef
         * @return ClassDef that the query searches for
         * @throws ClassNotFoundException When the ClassDef does not exist in the graph
         */
        @Override
        public ClassDef getClass(Query query) throws ClassNotFoundException {
            return getClass(query.queryName);
        }

        /**
         * Searches for a ClassDef that matches to the string name in the graph.
         *
         * @param s Name of the ClassDef
         * @return ClassDef with the name of s
         * @throws ClassNotFoundException
         */
        @Override
        public ClassDef getClass(String s) throws ClassNotFoundException {
            ClassDef classDef = db.getClassDef(s);
            if (classDef == null) {
                throw new ClassNotFoundException("Key is not present in table");
            }
            return classDef;
        }

        /**
         * Inserting the WDBObject into the graph
         * @param wdbObject The WDBObject
         */
        @Override
        public void putObject(WDBObject wdbObject) {
            db.putWDBObject(wdbObject);
        }

        /**
         * Return the WDBObject from the graph with specific requirements
         * @param className Name of the ClassDef
         * @param Uid UID of the WDBObject
         * @return WDBObject from the graph
         */
        @Override
        public WDBObject getObject(String className, Integer Uid) {
            return db.getWDBObject(className, Uid);
        }

        @Override
        public ArrayList<WDBObject> getObjects(IndexDef indexDef, String s) {
            throw new UnsupportedOperationException();
        }


        @Override
        public void commit() {
            graph.tx().commit();
        }

        @Override
        public void abort() {

        }

    }
}
