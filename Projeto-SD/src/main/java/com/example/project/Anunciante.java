package com.example.project;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity // This tells Hibernate to make a table out of this class
public class Anunciante {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer aId;

	private String name;

	@Column(name = "email")
	private String email;

	@OneToMany(mappedBy = "responsibleUser", cascade = CascadeType.ALL)
	private List<Home> listHomes;

	@OneToMany(mappedBy = "responsibleUser", cascade = CascadeType.ALL)
	private List<Contract> listContracts;

	private String salt;

	private String psw;

	private String salt_Psw_Hash;

	private Integer housesSold = 0;

	private Integer rentedHouses = 0;

	private Integer totalArrendamento = 0;

	private Integer totalSold = 0;

	public Integer getAId() {
		return aId;
	}

	public void setAId(Integer aId) {
		this.aId = aId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Home> getListHomes() {
		return listHomes;
	}

	public void setListHomes(List<Home> listHomes) {
		this.listHomes = listHomes;
	}

	public List<Contract> getListContracts() {
		return listContracts;
	}

	public void setListContract(List<Contract> listContracts) {
		this.listContracts = listContracts;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getPsw() {
		return psw;
	}

	public void setPsw(String psw) {
		this.psw = psw;
	}

	public String getSalt_Psw_Hash() {
		return salt_Psw_Hash;
	}

	public void setSalt_Psw_Hash(String salt_Psw_Hash) {
		this.salt_Psw_Hash = salt_Psw_Hash;
	}

	public Integer getHousesSold() {
		return housesSold;
	}

	public void incHousesSold() {
		this.housesSold++;
	}

	public Integer getRentedHouses() {
		return rentedHouses;
	}

	public void incRentedHouses() {
		this.rentedHouses++;
	}

	public Integer getTotalArrendamento() {
		return totalArrendamento;
	}

	public void incTotalArrendamento(Integer arrendamento) {
		this.totalArrendamento += arrendamento;
	}

	public Integer getTotalSold() {
		return totalSold;
	}

	public void incTotalSold(Integer sold) {
		this.totalSold += sold;
	}
}