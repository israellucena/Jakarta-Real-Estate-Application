package com.example.project;

import org.hibernate.annotations.Cascade;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity // This tells Hibernate to make a table out of this class
public class Home {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer hId;

	private String type; // Venda ou Arrendamento

	@ManyToOne (cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "responsibleUser_email")
	private Anunciante responsibleUser; // Utilizador responsável

	private String description; // Descrição da moradia

	@OneToOne(cascade = CascadeType.ALL)
	private Contract contract; // Objeto para futuro contrato

	private String city; // Cidade da moradia

	private Integer cost; // Preço da moradia

	private Integer minCost; // Para a pesquisa

	private Integer maxCost; // Para a pesquisa

	private String status; // Disponível, Arrendada ou Comprada

	@ManyToOne (cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "clientUser_email")
	private Client clientUser; // Se alugada ou comprada, o utilizador do cliente

	public Integer getHId() {
		return hId;
	}

	public void setId(Integer hId) {
		this.hId = hId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Anunciante getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(Anunciante responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
		this.cost = cost;
	}

	public Integer getMinCost() {
		return minCost;
	}

	public void setMinCost(Integer minCost) {
		this.minCost = minCost;
	}

	public Integer getMaxCost() {
		return maxCost;
	}

	public void setMaxCost(Integer maxCost) {
		this.maxCost = maxCost;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Client getClientUser() {
		return clientUser;
	}

	public void setClientUser(Client clientUser) {
		this.clientUser = clientUser;
	}
}