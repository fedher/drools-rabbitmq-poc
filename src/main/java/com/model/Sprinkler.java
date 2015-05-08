package com.model;

public class Sprinkler {

	private Room room;
	private boolean on = false;
	
	
	public Sprinkler(Room room) {
		this.room = room;
	}
	
	public Room getRoom() {
		return room;
	}

	public boolean getOn() {
		return on;
	}
	
	public void setOn(boolean on) {
		this.on = on;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
		
}
