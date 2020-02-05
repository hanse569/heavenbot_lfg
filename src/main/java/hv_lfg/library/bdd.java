package hv_lfg.library;

import java.sql.*;

public class bdd {

    public static Connection getConn(){
        Connection conn = null;
        try{
            String url = "jdbc:sqlite:C:/Users/hanse/hv_lfg.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException ex) { ex.printStackTrace(); }
        return conn;
    }

    public static ResultSet getTable(Connection conn, String query) {
        ResultSet rs = null;
        try {
            Statement stmt;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) { ex.printStackTrace(); }
        return rs;
    }

    public static void Insert(String query){
        try {
            Connection conn = bdd.getConn();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void addEvent(OrganizedDate od) {
        if(od != null){
            try {
                Connection conn = bdd.getConn();

                String myStatement = "INSERT INTO OrganizedDate ('idMessageDiscord','admin','instance','difficulty','date','descritption') VALUES (?,?,?,?,?,?);";
                PreparedStatement statement = conn.prepareStatement(myStatement);
                statement.setString(1,od.getIdMessageDiscord());
                statement.setString(2,od.getAdmin().getIdDiscord());
                statement.setInt(3,od.getInstance());
                statement.setInt(4,od.getDifficulty());
                statement.setString(5,od.getDateToString());
                statement.setString(6,od.getDescription().replace("'","''"));

                statement.executeUpdate();

            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
}
