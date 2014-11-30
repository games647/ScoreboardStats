package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebeaninternal.server.util.ClassPathReader;

import org.apache.commons.lang.ArrayUtils;

/**
 * This is workaround to a bug in Java 6. eBean 2.7.3 has a bug catching up
 * the wrong exception while scanning through the ebean.properties. Spigot
 * fixed this with updating eBean to 2.8.1 and newer versions of eBean has
 * fixed it completely.
 *
 * The actually problem is that non-latin characters cannot be read in Java 6.
 * Java 7 has a fix for it by passing a charset, but this still cannot be used in
 * Java 6 and eBean doesn't use it either.
 */
public class PathReader implements ClassPathReader {

    @Override
    public Object[] readPath(ClassLoader classLoader) {
        //return an empty array without creating one
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
}
