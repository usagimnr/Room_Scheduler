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
public class ReservationQueries {
    private static ArrayList<String> faculty;
    private static ArrayList<Date> dates;
    private static ArrayList<RoomEntry> roomsentry;
    private static PreparedStatement getAllReservations;
    private static PreparedStatement addReservation;
    private static PreparedStatement deleteReservation;
    private static ResultSet resultSet;
    private static ArrayList<Integer> seat;
    private static Connection connection;
    
    public static ArrayList<ReservationEntry> getReservationsByDate(){
        connection = DBConnection.getConnection();
        try{
            getAllReservations = connection.prepareStatement("SELECT * FROM Reservation order by date");
            ResultSet resultSet = getAllReservations.executeQuery();
            ArrayList<ReservationEntry> results = new ArrayList<ReservationEntry>();
            
                while(resultSet.next()){
                    results.add(new ReservationEntry(
                            resultSet.getString("faculty"), 
                            resultSet.getString("room"),
                            resultSet.getInt("seats"),
                            resultSet.getDate("date"),
                            resultSet.getTimestamp("timestamp"))
                    );
                }
                return results;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
              
        }
        return null;
    }
    
    public static ArrayList<ReservationEntry> getReservationsByDate(Date date){
        connection = DBConnection.getConnection();
        try{
            getAllReservations = connection.prepareStatement("SELECT * FROM Reservation Where Date = ?");
            getAllReservations.setDate(1,date);
            ResultSet resultSet = getAllReservations.executeQuery();
            ArrayList<ReservationEntry> results = new ArrayList<ReservationEntry>();
            
                while(resultSet.next()){
                    results.add(new ReservationEntry(
                            resultSet.getString("faculty"), 
                            resultSet.getString("room"),
                            resultSet.getInt("seats"),
                            resultSet.getDate("date"),
                            resultSet.getTimestamp("timestamp"))
                    );
                }
                return results;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
              
        }
        return null;
    }
    
    public static ArrayList<ReservationEntry> getReservationsByFaculty(String faculty){
        connection = DBConnection.getConnection();
        try{
            getAllReservations = connection.prepareStatement("SELECT * FROM Reservation WHERE faculty = ?");
            getAllReservations.setString(1, faculty);
            ResultSet resultSet = getAllReservations.executeQuery();
            ArrayList<ReservationEntry> results = new ArrayList<ReservationEntry>();
                while(resultSet.next()){
                    results.add(new ReservationEntry(
                            resultSet.getString("faculty"), 
                            resultSet.getString("room"),
                            resultSet.getInt("seats"),
                            resultSet.getDate("date"),
                            resultSet.getTimestamp("timestamp"))
                    );
                }
                return results;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
              
        }
        return null;
    }
    
    
    public static String addReservation(String faculty, int seats, Date date){
        ArrayList<RoomEntry> rooms = RoomQueries.getAllPossibleRooms(seats);
        ArrayList<ReservationEntry> reservations = getReservationsByDate();
        
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
                addReservation = connection.prepareStatement("insert into reservation (faculty, room, date, seats, timestamp) values (?, ?, ?, ?, ?)");
                addReservation.setString(1, faculty);
                addReservation.setString(2, smallRoom.getName());
                addReservation.setDate(3, date);
                addReservation.setInt(4, seats);
                addReservation.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
                addReservation.executeUpdate();
            }
            catch(SQLException sqlException)
            {
                sqlException.printStackTrace();
            }
            return String.format("%s reserved room: %s.", faculty, smallRoom.getName());
        }
        else{
            WaitlistQueries.addWaitlist(faculty, seats, date);
            return String.format("%s has been added to the waitlist.", faculty);
        }
    }
    
    public static String cancelReservation(String faculty, Date date){
        ArrayList<WaitlistEntry> waitlists = WaitlistQueries.getWaitlistByDate();
        for(WaitlistEntry waitlist: waitlists){
            if(waitlist.getFaculty().equals(faculty)) {
                WaitlistQueries.deleteWaitlistEntry(waitlist.getFaculty(), waitlist.getSeats(), waitlist.getDate());
            }
        }
        ArrayList<ReservationEntry> reservations = ReservationQueries.getReservationsByDate();
        for(ReservationEntry reservation: reservations){
            if(reservation.getFaculty().equals(faculty)) {
                ReservationQueries.deleteReservation(reservation.getFaculty(), reservation.getSeats(), reservation.getDate());
                for(WaitlistEntry waitlist: waitlists){
                    if(waitlist.getSeats() <= reservation.getSeats()) {
                        ReservationQueries.addReservation(waitlist.getFaculty(), waitlist.getSeats(), waitlist.getDate());
                        WaitlistQueries.deleteWaitlistEntry(waitlist.getFaculty(), waitlist.getSeats(), waitlist.getDate());
            }
        } 
            }
        }
        return null;
   }
    
    public static void deleteReservation(String faculty, int seats, Date date){
       connection = DBConnection.getConnection();
        try{
            deleteReservation = connection.prepareStatement("DELETE FROM Reservation WHERE faculty = ? AND date = ?");
            deleteReservation.setString(1, faculty);
            deleteReservation.setDate(2, date);
            deleteReservation.executeUpdate();
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();    
        }
   }
    
}
