import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mtss
 */
public class Dates {
    static Date date;
    private static Connection connection;
    private static PreparedStatement getDates;
    private static PreparedStatement addDates;
    private static ResultSet resultSet;
    
    public static ArrayList<Date> getAllDates()
    {
        connection = DBConnection.getConnection();
        ArrayList<Date> dates = new ArrayList<Date>();
        try{
            getDates = connection.prepareStatement("select date from dates order by date");
            resultSet = getDates.executeQuery();
            
            while(resultSet.next()){
                dates.add(resultSet.getDate(1));
            }
        }
            catch(SQLException sqlException){
                    sqlException.printStackTrace();
            } 
            return dates;
        
    }
    
    public static void addDate(Date dates){
        date = dates;
        
        connection = DBConnection.getConnection();
        try{
            addDates = connection.prepareStatement("insert into dates (date) values (?)");
            addDates.setDate(1, (java.sql.Date) date);
            addDates.executeUpdate();
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

}
