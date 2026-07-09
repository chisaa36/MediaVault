package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.db.DatabaseInitializer;

public class UserDAO {
	
	private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean login(String username, String password) throws SQLException {

        String sql = """
            SELECT 1
            FROM users
            WHERE username = ? AND password = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    public int getUserID(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return -1;
    }
    
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void addUser(String username, String password) throws SQLException {
        int userId = -1;
    	String sql = "INSERT INTO users(username, password) VALUES(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
		        if (keys.next()) {
		        	userId = keys.getInt(1);
		        }
		    }
            
        } catch (SQLException e) {
			if (e.getMessage().contains("UNIQUE constraint failed")) {
		        System.out.println("Username '" + username + "' is already taken.");
		    } else {
		        System.out.println(e.getMessage());
		    }
		}
        
        // add "all" entries category if user is added
     	if (userId != -1) {
     		DatabaseInitializer.registerUser(conn, userId);
     	}
    }
    
    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public void updatePassword(int id, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}
