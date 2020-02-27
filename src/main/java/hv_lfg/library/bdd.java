package hv_lfg.library;

import java.sql.*;

public class bdd {

    private static Connection conn = bdd.getConn();

    public static Connection getConn(){
        Connection conn = null;
        try{
            String url = "jdbc:sqlite:C:/Users/hanse/hv_lfg.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException ex) { ex.printStackTrace(); }
        return conn;
    }

    public static ResultSet getTable(String query) {
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
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void addEvent(OrganizedDate od) {
        if(od != null){
            try {
                String myStatement = "INSERT INTO OrganizedDate ('idMessageDiscord','admin','instance','difficulty','date','description') VALUES (?,?,?,?,?,?);";
                PreparedStatement statement = conn.prepareStatement(myStatement);
                statement.setString(1,od.getIdMessageDiscord());
                statement.setString(2,od.getAdmin().getIdDiscord());
                statement.setInt(3,od.getInstance().getIdInstance());
                statement.setInt(4,od.getDifficulty());
                statement.setString(5,od.getDateToRequest());
                statement.setString(6,od.getDescription().replace("'","''"));

                statement.executeUpdate();

            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public static void updateIdMessageEvent(int id,String newIdMessageDiscord){
        try{
            String myStatement = "UPDATE OrganizedDate set idMessageDiscord = ? WHERE id = ?;";
            PreparedStatement statement = conn.prepareStatement(myStatement);
            statement.setString(1,newIdMessageDiscord);
            statement.setInt(2,id);
            statement.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void insertOrRemoveRole(String request,int idEvent,String idMember){
        try{
            PreparedStatement statement = conn.prepareStatement(request);
            statement.setInt(1,idEvent);
            statement.setString(2,idMember);
            statement.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}
