import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mtss
 */
public class WaitlistQueries {
    
   private static Connection connection;
   private static PreparedStatement addWaitlist;
   private static PreparedStatement getWaitlist;
   private static PreparedStatement deleteWaitlist;
   private static PreparedStatement addWaitlistReservation;
   public static void addWaitlist(String faculty, int seats, Date date){
       connection = DBConnection.getConnection();
            try
            {
                addWaitlist = connection.prepareStatement("insert into waitlist (faculty, date, seats, timestamp) values (?, ?, ?, ?)");
                addWaitlist.setString(1, faculty);
                addWaitlist.setDate(2, date);
                addWaitlist.setInt(3, seats);
                addWaitlist.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
                addWaitlist.executeUpdate();
            }
            catch(SQLException sqlException)
            {
                sqlException.printStackTrace();
            }
   }
   public static ArrayList<WaitlistEntry> getWaitlistByDate(){
       connection = DBConnection.getConnection();
        try{
            getWaitlist = connection.prepareStatement("SELECT * FROM Waitlist order by date, timestamp");
            ResultSet resultSet = getWaitlist.executeQuery();
            ArrayList<WaitlistEntry> results = new ArrayList<WaitlistEntry>();
            
                while(resultSet.next()){
                    results.add(new WaitlistEntry(resultSet.getString("faculty"), resultSet.getInt("seats"), resultSet.getDate("date"), resultSet.getTimestamp("timestamp")));
                }
                return results;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();    
        }
        return null;
   }
   public static void deleteWaitlistEntry(String faculty, int seats, Date date){
       connection = DBConnection.getConnection();
        try{
            deleteWaitlist = connection.prepareStatement("DELETE FROM Waitlist WHERE faculty = ? AND date = ?");
            deleteWaitlist.setString(1, faculty);
            deleteWaitlist.setDate(2, date);
            deleteWaitlist.executeUpdate();
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();    
        }
   }

    public static ArrayList<WaitlistEntry> getWaitlistByDate(Date date){
        connection = DBConnection.getConnection();
        try{
            getWaitlist = connection.prepareStatement("SELECT * FROM Waitlist Where Date = ?");
            getWaitlist.setDate(1,date);
            ResultSet resultSet = getWaitlist.executeQuery();
            ArrayList<WaitlistEntry> results = new ArrayList<WaitlistEntry>();
            
                while(resultSet.next()){
                    results.add(new WaitlistEntry(
                            resultSet.getString("faculty"), 
                            resultSet.getInt("seats"),
                            resultSet.getDate("date"),
                            resultSet.getTimestamp("Timestamp"))
                    );
                }
                return results;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
              
        }
        return null;
    }
    
    public static ArrayList<WaitlistEntry> getWaitlistByFaculty(String faculty){
        connection = DBConnection.getConnection();
        try{
            getWaitlist = connection.prepareStatement("SELECT * FROM Waitlist WHERE faculty = ?");
            getWaitlist.setString(1, faculty);
            ResultSet resultSet = getWaitlist.executeQuery();
            ArrayList<WaitlistEntry> results = new ArrayList<WaitlistEntry>();
            
                while(resultSet.next()){
                    results.add(new WaitlistEntry(
                            resultSet.getString("faculty"), 
                            resultSet.getInt("seats"),
                            resultSet.getDate("date"),
                            resultSet.getTimestamp("Timestamp"))
                    );
                }
                return results;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
              
        }
        return null;
    }
    public static String addWaitlistReservation(String faculty, int seats, Date date){
        ArrayList<RoomEntry> rooms = RoomQueries.getAllPossibleRooms(seats);
        ArrayList<ReservationEntry> reservations = ReservationQueries.getReservationsByDate();
        
            for(ReservationEntry reservation : reservations){
                Start:
                for(RoomEntry room : rooms){
                    System.out.printf("\nIs %s a possible room?\n\n",room.getName());
                    
                    System.out.printf("\nLooking at reservation for: %s, with room: %s.%n", reservation.getFaculty(), room.getName());

                    System.out.printf("\nIs %s = %s AND %s = %s", reservation.getFaculty(), faculty, reservation.getDate(), date);
                    if(reservation.getFaculty().equals(faculty) && reservation.getDate().equals(date)){
                        System.out.print("\nYes\n");
                        rooms = new ArrayList<>();
                        break Start;
                    }
                    System.out.print("\nNo\n");

                    System.out.printf("\nIs %s = %s AND %s = %s", reservation.getRoom(), room.getName(), reservation.getDate(), date);
                    if(room.getName().equals(reservation.getRoom()) && reservation.getDate().toString().equals(date.toString())){
                        rooms.remove(room);
                        break Start;
                    }



                }
            }

        
        int min = 999999999;
        RoomEntry smallRoom = new RoomEntry("",0);
        
        if(rooms.size() != 0){
            for(RoomEntry room: rooms){
                if(room.getSeats() < min){
                    smallRoom = room;
                    min = smallRoom.getSeats();
                }
            }
            
            connection = DBConnection.getConnection();
            try
            {
                addWaitlistReservation = connection.prepareStatement("insert into reservation (faculty, room, date, seats, timestamp) values (?, ?, ?, ?, ?)");
                addWaitlistReservation.setString(1, faculty);
                addWaitlistReservation.setString(2, smallRoom.getName());
                addWaitlistReservation.setDate(3, date);
                addWaitlistReservation.setInt(4, seats);
                addWaitlistReservation.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
                addWaitlistReservation.executeUpdate();
            }
            catch(SQLException sqlException)
            {
                sqlException.printStackTrace();
            }
            deleteWaitlistEntry(faculty, seats, date);
            return String.format("Faculty " + faculty + " has been reserved room" + smallRoom + "on " + date + ".");
        }
       return null;
    }
}
