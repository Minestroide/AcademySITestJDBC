package it.marco.digrigoli;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.cj.jdbc.MysqlDataSource;

public class MySQLProvider {
	
	private MysqlDataSource dataSource;
	
	public MySQLProvider(String host, int port, String database, String username, String password) {
		this.dataSource = new MysqlDataSource();
		this.dataSource.setServerName(host);
		this.dataSource.setPort(port);
		this.dataSource.setDatabaseName(database);
		this.dataSource.setUser(username);
		this.dataSource.setPassword(password);
	}
	
	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
	
}
