package EmpiresMod.Datasource.Bridge;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.sqlite.JDBC;

import EmpiresMod.Constants;
import EmpiresMod.Empires;
import EmpiresMod.Configuration.Config;
import EmpiresMod.Configuration.ConfigProperty;
import EmpiresMod.Configuration.ConfigTemplate;

public class BridgeSQLite extends BridgeSQL {

	public ConfigProperty<String> dbPath = Config.instance.dbPath;

	public BridgeSQLite(ConfigTemplate config) {
		dbPath.set(Constants.DATABASE_FOLDER + config.getModID() + "/data.db");
		config.addBinding(dbPath, true);
		initProperties();
		initConnection();
	}

	@Override
	protected void initConnection() {
		File file = new File(dbPath.get());
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		this.dsn = "jdbc:sqlite:" + dbPath.get();

		try {
			DriverManager.registerDriver(new JDBC());
		} catch (SQLException ex) {
			Empires.instance.LOG.error("Failed to register driver for SQLite database.", ex);
		}

		try {
			if (conn != null && !conn.isClosed()) {
				try {
					conn.close();
				} catch (SQLException ex) {
				} // Ignore since we are just closing an old connection
				conn = null;
			}

			conn = DriverManager.getConnection(dsn, properties);
		} catch (SQLException ex) {
			Empires.instance.LOG.error("Failed to get SQL connection! {}", dsn);
			Empires.instance.LOG.error(ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
	protected void initProperties() {
		properties.put("foreign_keys", "ON");
	}
}