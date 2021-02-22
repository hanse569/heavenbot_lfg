package be.isservers.hmb.utils;

import be.isservers.hmb.lfg.library.OrganizedDate;
import net.dv8tion.jda.api.events.UpdateEvent;

import java.sql.*;

public class SQLiteSource {

    public static Connection getConn(){
        Connection conn = null;
        try{
            String url = "jdbc:sqlite:database.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException ex) { ex.printStackTrace(); }
        return conn;
    }

    public static ResultSet getTable(String query) {
        ResultSet rs = null;
        try {
            Statement stmt;
            stmt = getConn().createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) { ex.printStackTrace(); }
        return rs;
    }

    public static void addEvent(OrganizedDate od) {
        if(od != null){
            try {
                String myStatement = "INSERT INTO LFG_OrganizedDate ('admin','instance','difficulty','date','description') VALUES (?,?,?,?,?);";
                PreparedStatement statement = getConn().prepareStatement(myStatement);
                statement.setString(1,od.getAdminId());
                statement.setInt(2,od.getInstance().getIdInstance());
                statement.setInt(3,od.getDifficulty());
                statement.setString(4,od.getDateToRequest());
                statement.setString(5,od.getDescription().replace("'","''"));

                statement.executeUpdate();

            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public static void insertOrRemoveRole(String request, int idEvent, String idMember){
        try{
            PreparedStatement statement = getConn().prepareStatement(request);
            statement.setInt(1,idEvent);
            statement.setString(2,idMember);
            statement.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void removeEvent(String request, int id) {
        try{
            PreparedStatement statement = getConn().prepareStatement(request);
            statement.setInt(1,id);
            statement.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void updateValueOfEvent(String key,int value, int idEvent){
        try{
            PreparedStatement statement = getConn().prepareStatement("UPDATE LFG_OrganizedDate SET "+ key + "=? WHERE id=?;");
            statement.setInt(1,value);
            statement.setInt(2,idEvent);
            statement.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void updateValueOfEvent(String key,String value, int idEvent){
        try{
            PreparedStatement statement = getConn().prepareStatement("UPDATE LFG_OrganizedDate SET " + key + "=? WHERE id=?;");
            statement.setString(1,value);
            statement.setInt(2,idEvent);
            statement.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}
