package com.sample;

public class Sprinkler {

	private Room room = null;
	private boolean on = false;
	
	
	public Sprinkler(Room room) {
		this.room = room;
	}
	
	public Room getRoom() {
		return room;
	}

	public boolean isOn() {
		return on;
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
