package com.BC.entertainmentgravitation.entity;

public class EditPersonal extends PersonalInformation {

	private String clientID = "";

	private String Stage_name = "";

	private String professional = "";

	private String Starting_price = "";

	private String language = "";

	private String nationality = "";

	private String region = "";

	private String age = "";

	private String Whether_the_application_for_the_star = "";

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public void setStage_name(String Stage_name) {
		this.Stage_name = Stage_name;
	}

	public String getStage_name() {
		return this.Stage_name;
	}

	public void setProfessional(String professional) {
		this.professional = professional;
	}

	public String getProfessional() {
		return this.professional;
	}

	public void setStarting_price(String Starting_price) {
		this.Starting_price = Starting_price;
	}

	public String getStarting_price() {
		return this.Starting_price;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getNationality() {
		return this.nationality;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRegion() {
		return this.region;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getAge() {
		return this.age;
	}

	public void setWhether_the_application_for_the_star(
			String Whether_the_application_for_the_star) {
		this.Whether_the_application_for_the_star = Whether_the_application_for_the_star;
	}

	public String getWhether_the_application_for_the_star() {
		return this.Whether_the_application_for_the_star;
	}

}

