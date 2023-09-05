package com.example.project;

import org.hibernate.annotations.Cascade;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity // This tells Hibernate to make a table out of this class
public class Contract {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer cId;

	@OneToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	private Home home;

	private String description;

	@ManyToOne (cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "responsibleUser_email")
	private Anunciante responsibleUser; // Utilizador responsável pela moradia

	@ManyToOne (cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "clientUser_email")
	private Client clientUser; // Utilizador cliente

	private String status; // Pendente, assinado ou disponível

	public Integer getCId() {
		return cId;
	}

	public void setCId(Integer cId) {
		this.cId = cId;
	}

	public Home getHome() {
		return home;
	}

	public void setHome(Home home) {
		this.home = home;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Anunciante getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(Anunciante responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public Client getClientUser() {
		return clientUser;
	}

	public void setClientUser(Client clientUser) {
		this.clientUser = clientUser;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
