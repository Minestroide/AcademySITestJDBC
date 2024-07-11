package it.marco.digrigoli;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Main {

	private static MySQLProvider provider;

	private static void initProvider(String database) {
		String host = "localhost";
		int port = 3307;
		String username = "root";
		String password = "admin";

		provider = new MySQLProvider(host, port, database, username, password);
	}

	private static Connection getConnection() throws SQLException {
		if (provider == null) {
			throw new IllegalStateException("MySQLProvider has not been initialized.");
		}

		Connection connection = provider.getConnection();

		if (connection == null) {
			throw new IllegalStateException("Connection is null.");
		}

		System.out.println("Connection validity: " + connection.isValid(100));

		return connection;
	}

	public static void printAllData(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		StringBuilder columnsString = new StringBuilder();
		for (int i = 0; i < md.getColumnCount(); i++) {
			columnsString.append(md.getColumnName(i + 1)).append("\t");
		}
		System.out.println(columnsString.toString());
		while (rs.next()) {
			StringBuilder rowString = new StringBuilder();
			for (int i = 0; i < md.getColumnCount(); i++) {
				rowString.append(rs.getObject(i + 1)).append("\t");
			}
			System.out.println(rowString.toString());
		}
	}

	private static void deleteDb(String dbName) {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Deleting db %s...", dbName));
			PreparedStatement preparedStatement = conn.prepareStatement("DROP DATABASE IF EXISTS " + dbName + ";");

			preparedStatement.execute();
			System.out.println(String.format("Db %s deleted.", dbName));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createDb(String dbName) {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Creating db %s...", dbName));
			PreparedStatement preparedStatement = conn
					.prepareStatement("CREATE DATABASE IF NOT EXISTS " + dbName + ";");

			preparedStatement.execute();
			System.out.println(String.format("Db %s created.", dbName));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createUtenteTable() {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Creating table utente..."));
			PreparedStatement preparedStatement = conn
					.prepareStatement("CREATE TABLE IF NOT EXISTS utente (" + "id INT PRIMARY KEY NOT NULL,"
							+ "cognome VARCHAR(255) NOT NULL," + "nome VARCHAR(255) NOT NULL" + ");");

			preparedStatement.execute();
			System.out.println(String.format("Table utente created."));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createLibroTable() {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Creating table libro..."));
			PreparedStatement preparedStatement = conn
					.prepareStatement("CREATE TABLE IF NOT EXISTS libro (" + "id VARCHAR(255) PRIMARY KEY NOT NULL,"
							+ "titolo VARCHAR(255) NOT NULL," + "autore VARCHAR(255) NOT NULL" + ");");

			preparedStatement.execute();
			System.out.println(String.format("Table libro created."));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createPrestitoTable() {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Creating table libro..."));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS prestito (" + "id INT PRIMARY KEY NOT NULL," + "inizio DATE NOT NULL,"
							+ "fine DATE," + "id_libro VARCHAR(255) NOT NULL," + "id_utente INT NOT NULL,"
							+ "CONSTRAINT chiave_esterna_libro FOREIGN KEY (id_libro) REFERENCES libro(id),"
							+ "CONSTRAINT chiave_esterna_utente FOREIGN KEY (id_utente) REFERENCES utente(id)" + ");");

			preparedStatement.execute();
			System.out.println(String.format("Table libro created."));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void insertUser(int id, String name, String surname) { // l'id non è AUTO_INCREMENT per cui lo
																			// passeremo direttamente come parametro
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Inserting user %s %s...", name, surname));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"INSERT INTO utente (id, nome, cognome) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, surname);
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				System.out.println(String.format("User %s %s inserted: %s", name, surname, rs.getInt(1)));
			} else {
				System.out.println(String.format("User %s %s inserted. No keys generated.", name, surname));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void insertBook(int id, String title, String author) {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Inserting book %s...", id));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"INSERT INTO libro (id, titolo, autore) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, title);
			preparedStatement.setString(3, author);
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				System.out.println(String.format("Book %s inserted: %s", id));
			} else {
				System.out.println(String.format("Book %s inserted. No keys generated.", id));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void insertLoan(int id, String startDate, String endDate, int userId, int bookId) {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Inserting loan %s...", id));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"INSERT INTO prestito (id, inizio, fine, id_utente, id_libro) VALUES (?, ?, ?, ?, ?);",
					Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, startDate);
			preparedStatement.setString(3, endDate);
			preparedStatement.setInt(4, userId);
			preparedStatement.setInt(5, bookId);
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				System.out.println(String.format("Loan %s inserted: %s", id));
			} else {
				System.out.println(String.format("Loan %s inserted. No keys generated.", id));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showHandedBooks(int userId) {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Querying handed books of user with id %s...", userId));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT utente.nome, utente.cognome, libro.titolo, libro.autore FROM prestito INNER JOIN libro ON libro.id = prestito.id_libro INNER JOIN utente ON utente.id = prestito.id_utente WHERE id_utente = ? ORDER BY inizio ASC;");
			preparedStatement.setInt(1, userId);
			printAllData(preparedStatement.executeQuery());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showHandedBooks(int userId, String startDate, String endDate) {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Querying handed books of user with id %s...", userId));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT utente.nome, utente.cognome, libro.titolo, libro.autore, prestito.inizio, prestito.fine FROM prestito INNER JOIN libro ON libro.id = prestito.id_libro INNER JOIN utente ON utente.id = prestito.id_utente WHERE id_utente = ? AND inizio >= ? AND fine <= ? ORDER BY inizio ASC;");
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, startDate);
			preparedStatement.setString(3, endDate);
			printAllData(preparedStatement.executeQuery());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showBestReaders() {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Querying best readers..."));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT utente.nome, utente.cognome, COUNT(id_libro) AS libri_prestati FROM utente "
							+ "INNER JOIN prestito ON prestito.id_utente = utente.id GROUP BY prestito.id_libro ORDER BY libri_prestati DESC;");
			printAllData(preparedStatement.executeQuery());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showUnreturnedBooks() {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Querying unreturned books..."));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT libro.titolo, libro.autore FROM prestito INNER JOIN libro ON libro.id = prestito.id_libro WHERE fine = NULL;");
			printAllData(preparedStatement.executeQuery());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showMostHandedBooks() {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Querying most handed books..."));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT libro.titolo, libro.autore, SUM(prestito.id_libro) AS prestato_volte FROM prestito INNER JOIN libro ON prestito.id_libro = libro.id GROUP BY prestito.id_libro ORDER BY prestato_volte DESC;");
			printAllData(preparedStatement.executeQuery());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showLongestLoans() {
		try (Connection conn = getConnection()) {
			System.out.println(String.format("Querying longest loans..."));
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT prestito.id, libro.titolo, libro.autore, DATEDIFF(IFNULL(prestito.fine, CURRENT_DATE), prestito.inizio) AS durata FROM prestito INNER JOIN libro ON libro.id = prestito.id_libro HAVING durata >= 15;");
			printAllData(preparedStatement.executeQuery());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		initProvider("sys");
		deleteDb("test_academy");
		createDb("test_academy");

		// reinit mysql provider with test_academy db instead of using SQL USE;
		initProvider("test_academy");
		createUtenteTable();
		createLibroTable();
		createPrestitoTable();
		insertUser(1, "Mario", "Rossi");
		insertUser(2, "Andrea", "Verdi");
		insertUser(3, "Massimo", "Bianchi");
		insertUser(4, "Sara", "Vallieri");
		insertUser(5, "Marco", "Graviglia");
		insertUser(6, "Marzia", "Esposito");
		insertBook(1, "Divina Commedia", "Dante Alighieri");
		insertLoan(1, "2024/1/1", "2024/1/7", 4, 1);
		insertLoan(2, "2024/1/2", "2024/1/7", 4, 1);
		insertLoan(3, "2024/1/2", "2024/1/7", 4, 1);
		insertLoan(4, "2024/1/1", null, 4, 1);
		showHandedBooks(4);
		showBestReaders();
		showUnreturnedBooks();
		showHandedBooks(4, "2024/1/2", "2024/1/7");
		showMostHandedBooks();
		showLongestLoans();
	}

}
