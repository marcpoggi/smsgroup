package com.feeltest.smsgroup;

import java.util.ArrayList;

public class Groupe {
	private int id;
	private String nomgroup;
	private boolean checked = false;
	private ArrayList<Integer> belong;
	

	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Groupe(String nomgroup) {
		this.nomgroup=nomgroup;
	}
		
	public String getnomgroup() {
		return nomgroup;
	}
	public void setnomgroup(String nomgroup) {
		this.nomgroup = nomgroup;
	}
	public ArrayList<Integer> getBelong() {
		return belong;
	}
	public void setBelong(ArrayList<Integer> belong) {
		this.belong = belong;
	}
}
