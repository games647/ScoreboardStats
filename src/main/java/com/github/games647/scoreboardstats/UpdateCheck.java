package com.github.games647.scoreboardstats;

import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import static org.bukkit.Bukkit.getLogger;

public final class UpdateCheck  {

    private static boolean update;

    public static boolean checkUpdate(final String oldversion, final String link) {
        try {
            final String version = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new java.net.URL(link).openStream()).getElementsByTagName("item").item(0).getChildNodes()
                        .item(1).getTextContent().replace(Other.PLUGIN_NAME + "-", "");

            if (Double.parseDouble(oldversion) < Double.parseDouble(version)) {
                getLogger().info(Message.LOG_NAME + Message.NEW_VERSION);
                update = true;
            }
        } catch (Exception ex) {
            getLogger().info(Message.LOG_NAME + Message.VERSION_CHECK_FAILED);
        }

        return false;
    }

    public static boolean isUpdate() {
        return update;
    }
}
