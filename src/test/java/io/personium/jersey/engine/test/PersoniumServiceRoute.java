package io.personium.jersey.engine.test;

import io.personium.client.ServiceCollection;

/**
 * Class containing route of personium service 
 */
public class PersoniumServiceRoute implements ServiceCollection.IPersoniumServiceRoute {
    /** engine name */
    private String name;

    /** engine source */
    private String src;

    /**
     * Constructor for PersoniumServiceRoute
     * @param name engine name
     * @param src engine source name
     */
    public PersoniumServiceRoute(final String name, final String src) {
        this.name = name;
        this.src = src;
    }

    /** getter for engine name */
    public String getName() {
        return name;
    }

    /** getter for engine source */
    public String getSrc() {
        return src;
    }
}