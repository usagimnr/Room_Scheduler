import java.sql.Date;
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
public class ReservationEntry {
    public Date date;
    public String facultyName;
    public int numSeats;
    public String roomName;
    private Timestamp timestamp;
    
    public ReservationEntry(String f, String r, int s, Date d, Timestamp t){
        facultyName = f;
        numSeats = s;
        roomName = r;
        date = d;
        timestamp = t;
    }
    
    public String getFaculty(){
        return facultyName;
    }
    
    public String getRoom(){
        return roomName;
    }
    
    public int getSeats(){
        return numSeats;
    }
    
    public Date getDate(){
        return date;
    }
    
    public Timestamp getTimestamp(){
        return timestamp;
    }
}
