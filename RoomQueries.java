import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mtss
 */
public class RoomQueries {
    private static Connection connection;
    private static ArrayList<RoomEntry> rooms;
    private static PreparedStatement getRooms;
    private static PreparedStatement addRoom;
    private static PreparedStatement deleteRoom;
    private static PreparedStatement getRoomList;
    private static ResultSet resultSet;
    
    public static ArrayList<RoomEntry> getAllPossibleRooms(int seats){
        connection = DBConnection.getConnection();
        try{
            getRooms = connection.prepareStatement("SELECT * FROM Rooms order by seats");
            ResultSet resultSet = getRooms.executeQuery();
            ArrayList<RoomEntry> results = new ArrayList<>();
            rooms = new ArrayList<RoomEntry>();
            
                while(resultSet.next()){
                    results.add(new RoomEntry(resultSet.getString("name"), resultSet.getInt("seats")));
                }
                for(int i = 0; i < results.size(); i++){
                    if(seats <= results.get(i).getSeats()){
                        rooms.add(new RoomEntry(results.get(i).getName(), results.get(i).getSeats()));
                    }
                }
                return rooms;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
        } 
        return null;
    }
    
    public static ArrayList<String> getRoomList()
    {
        connection = DBConnection.getConnection();
        ArrayList<String> rooms = new ArrayList<String>();
        try
        {
            getRoomList = connection.prepareStatement("SELECT * from rooms order by name");
            resultSet = getRoomList.executeQuery();
            
            while(resultSet.next())
            {
                rooms.add(resultSet.getString(1));
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return rooms;
        
    }
    
    public static void addRoom(String room, int seats){
        RoomEntry rooms = new RoomEntry(room, seats);
        connection = DBConnection.getConnection();
        try{
            addRoom = connection.prepareStatement("insert into rooms (name, seats) values (?, ?)");
            addRoom.setString(1, rooms.getName());
            addRoom.setInt(2, seats);
            addRoom.executeUpdate();
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        ArrayList<WaitlistEntry> waitlists = WaitlistQueries.getWaitlistByDate();
        for(WaitlistEntry waitlist: waitlists){
            if(waitlist.getSeats() <= seats) {
                ReservationQueries.addReservation(waitlist.getFaculty(), waitlist.getSeats(), waitlist.getDate());
                WaitlistQueries.deleteWaitlistEntry(waitlist.getFaculty(), waitlist.getSeats(), waitlist.getDate());
            }
        }  
    }
                
    public static void dropRoom(String room){
        connection = DBConnection.getConnection();
        try{
            deleteRoom = connection.prepareStatement("DELETE FROM Rooms WHERE  name = ?");
            deleteRoom.setString(1, room);
            deleteRoom.executeUpdate();
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();    
        }
        ArrayList<ReservationEntry> reservations = ReservationQueries.getReservationsByDate();
        for(ReservationEntry reservation: reservations){
            if(reservation.getRoom().equals(room)) {
                ReservationQueries.addReservation(reservation.getFaculty(), reservation.getSeats(), reservation.getDate());
                ReservationQueries.deleteReservation(reservation.getFaculty(), reservation.getSeats(), reservation.getDate());
                WaitlistQueries.addWaitlistReservation(reservation.getFaculty(), reservation.getSeats(), reservation.getDate());
            }   
        }
    }
}