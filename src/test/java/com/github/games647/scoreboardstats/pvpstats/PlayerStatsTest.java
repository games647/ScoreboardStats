package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerStatsTest {

    private static String replaceUrlString(String input) throws IOException {
        final File file = File.createTempFile("temp", null);
        file.deleteOnExit();
        String result = input.replaceAll("\\{DIR\\}", file.getParentFile().getPath().replaceAll("\\\\", "/") + '/');
        result = result.replaceAll("\\{NAME\\}", file.getName());

        return result;
    }

    private EbeanServer server;

    @Before
    public void setUp() throws IOException {
        final ServerConfig config = new ServerConfig();
        config.addClass(PlayerStats.class);
        config.setName("TestCase");
        config.setValidateOnSave(true);

        final DataSourceConfig dataConfig = new DataSourceConfig();
        dataConfig.setUrl(replaceUrlString("jdbc:sqlite:{DIR}{NAME}.db"));
        dataConfig.setHeartbeatSql("SELECT 1");
        dataConfig.setIsolationLevel(TransactionIsolation.getLevel("SERIALIZABLE"));
        dataConfig.setDriver("org.sqlite.JDBC");
        dataConfig.setUsername("dad");
        dataConfig.setPassword("xyz");

        config.setDataSourceConfig(dataConfig);
        server = EbeanServerFactory.create(config);

        final DdlGenerator gen = ((SpiEbeanServer) server).getDdlGenerator();
        gen.runScript(false, gen.generateCreateDdl().replace("table", "table IF NOT EXISTS"));
    }

    /**
     * Test of getPlayername method, of class PlayerStats.
     */
    @Test
    public void testValidation() {
        final PlayerStats stats = new PlayerStats();
        final String validationType = "playername";

        Assert.assertTrue(stats.getPlayername(), server.validate(stats, validationType, null).length > 0);

        stats.setPlayername("-.abc14");
        Assert.assertTrue(stats.getPlayername(), server.validate(stats, validationType, null).length > 0);

        stats.setPlayername("123456789_12345678");
        Assert.assertTrue(stats.getPlayername(), server.validate(stats, validationType, null).length > 0);

        stats.setPlayername("xyz_123");
        Assert.assertTrue(stats.getPlayername(), server.validate(stats, validationType, null).length == 0);

        stats.setPlayername("xyz1234");
        Assert.assertTrue(stats.getPlayername(), server.validate(stats, validationType, null).length == 0);

        stats.setPlayername("123456789_123456");
        Assert.assertTrue(stats.getPlayername(), server.validate(stats, validationType, null).length == 0);
    }
}
