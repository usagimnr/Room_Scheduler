/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mtss
 */
public class RoomEntry {
    public String roomName;
    public int numSeats;
    
    public RoomEntry(String name, int seats){
        roomName = name;
        numSeats = seats;
    }
    
    public String getName(){
        return roomName;
    }
    
    public int getSeats(){
        return numSeats;
    }
}
