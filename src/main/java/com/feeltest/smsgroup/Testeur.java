package com.feeltest.smsgroup;

public class Testeur {

	
	private int id;
	private String nomprenom;
	private boolean checked = false;
	private String tel;
	private int id_group;
	

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
	
	public Testeur(String tel,String nomprenom, int id_group) {
		this.setTel(tel);
		this.nomprenom=nomprenom;
		this.id_group=id_group;
	}
	public Testeur(String tel) {
		this.setTel(tel);
	}
	
	
	public String getNomPrenom() {
		return nomprenom;
	}
	public void setNomPrenom(String nomprenom) {
		this.nomprenom = nomprenom;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public int getId_group() {
		return id_group;
	}
	public void setId_group(int id_group) {
		this.id_group = id_group;
	}
	
}



