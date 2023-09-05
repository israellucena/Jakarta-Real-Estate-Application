package com.example.project;
//Teste
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // This means that this class is a Controller
public class MainController {
	@Autowired // This means to get the bean called clientRepository
	private ClientRepository clientRepository;

	@Autowired // This means to get the bean called anuncianteRepository
	private AnuncianteRepository anuncianteRepository;

	@Autowired // This means to get the bean called homeRepository
	private HomeRepository homeRepository;

	@Autowired // This means to get the bean called contractRepository
	private ContractRepository contractRepository;

	private boolean login = false;
	private int idLogado = -1;

	@GetMapping(path = "/index")
	public String getIndex() {
		login = false;
		return "index";
	}

	@GetMapping(path = "/registrationClient")
	public String getClientRegistration(Model model) {
		Client c = new Client();
		model.addAttribute("newClient", c);
		return "registrationClient";
	}

	public String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	@PostMapping(path = "/saveClient")
	public String saveUser(@ModelAttribute("newClient") Client c) throws NoSuchAlgorithmException {
		boolean[] save = new boolean[1];
		save[0] = true;
		String salt;
		String hashOfSaltAndPass;

		synchronized (clientRepository) {
			Iterable<Client> clients = clientRepository.findAll();

			clients.forEach((client) -> {
				if (client.getEmail().equals(c.getEmail()))
					save[0] = false;
			});
		}

		if (save[0] == false)// Se já existe um utilizador registado com esse e-mail
			return "erroUtilizadorExiste"; // Retorna para uma mesma página indicando o ocorrido

		// Generates salt
		Random rd = new Random();
		StringBuffer sb = new StringBuffer();
		while (sb.length() < 16) {
			sb.append(Integer.toHexString(rd.nextInt()));
		}

		salt = sb.toString().substring(0, 16);

		// Se os campos inseridos cumprem todos os requisitos
		if (!c.getName().isBlank() && !c.getEmail().isBlank() && !c.getPsw().isBlank())
			save[0] = true;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedHash = digest.digest((salt + c.getPsw()).getBytes(StandardCharsets.UTF_8));

		hashOfSaltAndPass = bytesToHex(encodedHash);
		c.setSalt(salt);
		c.setSalt_Psw_Hash(hashOfSaltAndPass);
		c.setPsw("");

		if (save[0]) { // Todos os requisitos cumpridos
			clientRepository.save(c);
			login = true;
			idLogado = c.getCId();
			// Realizar autenticação da sessão
			return "redirect:/homeClient/" + c.getCId();
		}

		// Requisitos não cumpridos
		return "erro"; // Retornar para uma mesma página indicando o erro ocorrido
	}

	@GetMapping(path = "/registrationAnunciante")
	public String getAnuncianteRegistration(Model model) {
		Anunciante a = new Anunciante();
		model.addAttribute("newAnunciante", a);
		return "registrationAnunciante";
	}

	@PostMapping(path = "/saveAnunciante")
	public String saveAnunciante(@ModelAttribute("newAnunciante") Anunciante a) throws NoSuchAlgorithmException {
		boolean[] save = new boolean[1];
		save[0] = true;
		String salt;
		String hashOfSaltAndPass;

		synchronized (anuncianteRepository) {
			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getEmail().equals(a.getEmail()))
					save[0] = false;
			});
		}

		if (save[0] == false)// Se já existe um utilizador registado com esse e-mail
			return "erroUtilizadorExiste"; // Retorna para uma mesma página de indicando o ocorrido

		// Generates salt
		Random rd = new Random();
		StringBuffer sb = new StringBuffer();
		while (sb.length() < 16) {
			sb.append(Integer.toHexString(rd.nextInt()));
		}

		salt = sb.toString().substring(0, 16);

		// Se os campos inseridos cumprem todos os requisitos
		if (!a.getName().isBlank() && !a.getEmail().isBlank() && !a.getPsw().isBlank())
			save[0] = true;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedHash = digest.digest((salt + a.getPsw()).getBytes(StandardCharsets.UTF_8));

		hashOfSaltAndPass = bytesToHex(encodedHash);
		a.setSalt(salt);
		a.setSalt_Psw_Hash(hashOfSaltAndPass);
		a.setPsw("");

		if (save[0]) { // Todos os requisitos cumpridos
			anuncianteRepository.save(a);
			login = true;
			idLogado = a.getAId();
			// Realizar autenticação da sessão
			return "redirect:/homeAnunciante/" + a.getAId();
		}

		// Requisitos não cumpridos
		return "erro"; // Retornar para uma mesma página indicando o erro ocorrido
	}

	@GetMapping(path = "/loginClient")
	public String getClientLogin(Model model) {
		Client c = new Client();
		model.addAttribute("clientToLog", c);
		return "loginClient";
	}

	@PostMapping(path = "/logClient")
	public String logClient(@ModelAttribute("clientToLog") Client c) throws NoSuchAlgorithmException {
		boolean[] log = new boolean[1];
		log[0] = false;
		Integer[] cId = new Integer[1];

		Iterable<Client> clients = clientRepository.findAll();

		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		clients.forEach((client) -> {
			if (client.getEmail().equals(c.getEmail()) && client.getSalt_Psw_Hash().equals(
					bytesToHex(digest.digest((client.getSalt() + c.getPsw()).getBytes(StandardCharsets.UTF_8))))) {
				log[0] = true;
				cId[0] = client.getCId();
			}
		});

		if (log[0]) { // Utilizador existe na base de dados
			// Realizar autenticação da sessão
			login = true;
			idLogado = cId[0];
			return "redirect:/homeClient/" + cId[0];
		}

		return "erroLogin"; // Retorna para uma mesma página indicando o erro ocorrido
	}

	@GetMapping(path = "/loginAnunciante")
	public String getAnuncianteLogin(Model model) {
		Anunciante a = new Anunciante();
		model.addAttribute("anuncianteToLog", a);
		return "loginAnunciante";
	}

	@PostMapping(path = "/logAnunciante")
	public String logAnunciante(@ModelAttribute("anuncianteToLog") Anunciante a) throws NoSuchAlgorithmException {
		boolean[] log = new boolean[1];
		log[0] = false;
		Integer[] aId = new Integer[1];

		Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();

		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		anunciantes.forEach((anunciante) -> {
			if (anunciante.getEmail().equals(a.getEmail()) && anunciante.getSalt_Psw_Hash().equals(
					bytesToHex(digest.digest((anunciante.getSalt() + a.getPsw()).getBytes(StandardCharsets.UTF_8))))) {
				log[0] = true;
				aId[0] = anunciante.getAId();
			}
		});

		if (log[0]) { // Utilizador existe na base de dados
			// Realizar autenticação da sessão
			login = true;
			idLogado = aId[0];
			return "redirect:/homeAnunciante/" + aId[0];
		}

		return "erroLogin"; // Retorna para uma mesma página indicando o erro ocorrido
	}

	@GetMapping(path = "/homeClient/{logedClientId}")
	public String getClientHome(@PathVariable(value = "logedClientId") Integer logedClientId, Model model) {
		// Verificar se o utilizador está com sessão ativa
		if (login == true && idLogado == logedClientId) { // Se sim
			boolean[] isClient = new boolean[1];
			isClient[0] = false;

			Iterable<Client> clients = clientRepository.findAll();

			clients.forEach((client) -> {
				if (client.getCId() == logedClientId)
					isClient[0] = true;
			});

			if (isClient[0]) { // Se o utilizador é um cliente
				model.addAttribute("logedClientId", logedClientId);
				return "homeClient";
			} else { // Se o utilizador não é um cliente
				return "index";
			}
		}

		// Se não
		return "index";
	}

	@GetMapping(path = "/homeAnunciante/{logedAnuncianteId}")
	public String getAnuncianteHome(@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId, Model model) {
		// Verificar se o utilizador está com sessão ativa
		if (login == true && idLogado == logedAnuncianteId) { // Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId)
					isAnunciante[0] = true;
			});

			if (isAnunciante[0]) { // Se o utilizador é um anunciantes
				model.addAttribute("logedAnuncianteId", logedAnuncianteId);
				return "homeAnunciante";
			} else { // Se o utilizador não é um anunciante
				return "index";
			}
		}

		// Se não
		return "index";
	}

	@GetMapping(path = "/formAd/{logedAnuncianteId}")
	public String getFormAd(@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId, Model model) {
		// Verificar se o utilizador está com sessão ativa
		if (login = true && idLogado == logedAnuncianteId) {// Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;
			Home h = new Home();
			h.setContract(new Contract());

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId) {
					isAnunciante[0] = true;
					h.setResponsibleUser(anunciante);
				}
			});

			if (isAnunciante[0]) {// Se o utilizador é um anunciante
				model.addAttribute("newHome", h);
				model.addAttribute("logedAnuncianteId", logedAnuncianteId);
				return "formAd";
			} else { // Se o utilizador não é um anunciante
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@PostMapping(path = "/createAd/{logedAnuncianteId}")
	public String createAd(@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId,
			@ModelAttribute("newHome") Home h, Model model) {
		// Verificar se o utilzizador está com sessão ativa
		if (login == true && idLogado == logedAnuncianteId) {// Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId) {
					isAnunciante[0] = true;
					h.setResponsibleUser(anunciante);
				}
			});

			if (isAnunciante[0]) {// Se o utilizador é um anunciante
				h.setStatus("disponível");
				h.getContract().setHome(h);
				h.getContract().setStatus("disponível");
				h.getContract().setResponsibleUser(h.getResponsibleUser());
				homeRepository.save(h);
				return "redirect:/homeAnunciante/" + logedAnuncianteId;
			} else {// Se o utilizador não é um anunciante
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/searchClient/{logedUserId}")
	public String getSearchClient(@PathVariable(value = "logedUserId") Integer logedUserId, Model model) {
		// Verificar se o utilizador está com sessão ativa
		if (login == true && idLogado == logedUserId) { // Se sim
			Home h = new Home();
			model.addAttribute("logedUserId", logedUserId);
			model.addAttribute("homeSearch", h);
			return "searchClient";
		}

		// Se não
		return "index";
	}

	@GetMapping(path = "/searchAnunciante/{logedUserId}")
	public String getSearchAnunciante(@PathVariable(value = "logedUserId") Integer logedUserId, Model model) {
		// Verificar se o utilizador está com sessão ativa
		if (login == true && idLogado == logedUserId) { // Se sim
			Home h = new Home();
			model.addAttribute("logedUserId", logedUserId);
			model.addAttribute("homeSearch", h);
			return "searchAnunciante";
		}

		// Se não
		return "index";
	}

	@PostMapping(path = "/toSearch/{userType}/{logedUserId}")
	public String toSearch(@PathVariable(value = "userType") Integer userType,
			@PathVariable(value = "logedUserId") Integer logedUserId, @ModelAttribute("homeSearch") Home h,
			Model model) {
		// Verificar se o utilizador está com sessão ativa
		if (login == true && idLogado == logedUserId) { // Se sim
			if (userType == 1) {
				boolean[] isClient = new boolean[1];
				isClient[0] = false;

				Iterable<Client> clients = clientRepository.findAll();

				clients.forEach((client) -> {
					if (client.getCId() == logedUserId)
						isClient[0] = true;
				});

				if (isClient[0]) {// Se o utilizador é um cliente
					ArrayList<Home> listHomesSearch = new ArrayList<>();

					Iterable<Home> homes = homeRepository.findAll();

					h.setStatus("disponível");
					homes.forEach((home) -> {
						if (home.getType().equals(h.getType())
								&& home.getCity().toLowerCase().equals(h.getCity().toLowerCase())
								&& home.getCost() >= h.getMinCost() && home.getCost() <= h.getMaxCost()
								&& home.getStatus().toLowerCase().equals(h.getStatus().toLowerCase())) {
							listHomesSearch.add(home);
						}
					});

					model.addAttribute("listHomeSearch", listHomesSearch);
					model.addAttribute("logedUserId", logedUserId);

					return "searchResultClient";
				}
			}
			if (userType == 2) {
				boolean[] isAnunciante = new boolean[1];
				isAnunciante[0] = false;

				Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();

				anunciantes.forEach((anunciante) -> {
					if (anunciante.getAId() == logedUserId)
						isAnunciante[0] = true;
				});

				if (isAnunciante[0]) {// Se o utilizador é um anunciante
					ArrayList<Home> listHomesSearch = new ArrayList<>();

					Iterable<Home> homes = homeRepository.findAll();

					h.setStatus("disponível");
					homes.forEach((home) -> {
						if (home.getType().equals(h.getType())
								&& home.getCity().toLowerCase().equals(h.getCity().toLowerCase())
								&& home.getCost() >= h.getMinCost() && home.getCost() <= h.getMaxCost()
								&& home.getStatus().toLowerCase().equals(h.getStatus().toLowerCase())) {
							listHomesSearch.add(home);
						}
					});

					model.addAttribute("listHomeSearch", listHomesSearch);
					model.addAttribute("logedUserId", logedUserId);

					return "searchResultAnunciante";
				}
			}
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/contract/{homeId}/{logedClientId}")
	public String getContract(@PathVariable(value = "homeId") Integer homeId,
			@PathVariable(value = "logedClientId") Integer logedClientId, Model model) {
		// Verificar se o utilizador está com sessão ativa
		if (login == true && idLogado == logedClientId) { // Se sim
			boolean[] isClient = new boolean[1];
			isClient[0] = false;

			Iterable<Client> clients = clientRepository.findAll();

			clients.forEach((client) -> {
				if (client.getCId() == logedClientId)
					isClient[0] = true;
			});

			if (isClient[0]) { // Se o utilizador é um cliente
				Contract c = new Contract();
				c.setHome(new Home());
				c.setResponsibleUser(new Anunciante());
				c.setClientUser(new Client());

				Iterable<Home> homes = homeRepository.findAll();

				homes.forEach((home) -> {
					if (home.getHId() == homeId) {
						c.getHome().setType(home.getType());
						c.getHome().setCity(home.getCity());
						c.getHome().setDescription(home.getDescription());
						c.getHome().setCost(home.getCost());
						c.setDescription(home.getContract().getDescription());
						c.getResponsibleUser().setName(home.getResponsibleUser().getName());
						c.getResponsibleUser().setEmail(home.getResponsibleUser().getEmail());

					}
				});

				clients.forEach((client) -> {
					if (client.getCId() == logedClientId) {
						c.getClientUser().setName(client.getName());
						c.getClientUser().setEmail(client.getEmail());
					}
				});

				model.addAttribute("contract", c);
				model.addAttribute("homeId", homeId);
				model.addAttribute("logedClientId", logedClientId);

				return "contract";
			} else { // Se o utilizador não é um cliente
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@PostMapping(path = "/requestContract/{logedClientId}/{homeId}")
	public String saveContract(@PathVariable("logedClientId") Integer logedClientId,
			@PathVariable("homeId") Integer homeId, @ModelAttribute("contract") Contract c, Model model) {
		// Verificar se o utilizador está com sessão ativa
		if (login == true && idLogado == logedClientId) { // Se sim
			boolean[] isClient = new boolean[1];
			isClient[0] = false;

			Iterable<Client> clients = clientRepository.findAll();

			clients.forEach((client) -> {
				if (client.getCId() == logedClientId)
					isClient[0] = true;
			});

			if (isClient[0]) { // Se o utilizador é um cliente
				Iterable<Contract> contracts = contractRepository.findAll();

				contracts.forEach((contract) -> {
					if (contract.getHome().getHId() == homeId) {
						contract.setStatus("pendente");
						contract.getHome().setStatus("pendente");
						Iterable<Home> homes = homeRepository.findAll();

						homes.forEach((home) -> {
							if (home.getHId() == homeId) {
								contract.setHome(home);
							}
						});

						clients.forEach((client) -> {
							if (client.getCId() == logedClientId) {
								contract.setClientUser(client);
								contract.getHome().setClientUser(client);
							}
						});
						contractRepository.save(contract);
					}
				});
				model.addAttribute("logedClientId", logedClientId);
				return "operacaoBemSucedidaClient";
			} else { // Se o utilizador não é um cliente
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/seeHomesClient/{logedClientId}")
	public String getSeeHomesClient(@PathVariable(value = "logedClientId") Integer logedClientId, Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedClientId) { // Se sim
			boolean[] isClient = new boolean[1];
			isClient[0] = false;

			Iterable<Client> clients = clientRepository.findAll();
			Client c = new Client();

			clients.forEach((client) -> {
				if (client.getCId() == logedClientId) {
					isClient[0] = true;
					c.setEmail(client.getEmail());
				}
			});

			ArrayList<Home> listHomes = new ArrayList<>();

			Iterable<Home> homes = homeRepository.findAll();

			homes.forEach((home) -> {
				if (home.getClientUser() != null)
					if (home.getClientUser().getEmail().equals(c.getEmail())
							&& home.getContract().getStatus().toLowerCase().equals("assinado")) {
						listHomes.add(home);
					}
			});

			if (isClient[0]) { // Se o utilizador é um cliente
				model.addAttribute("clientHomes", listHomes);
				model.addAttribute("logedClientId", logedClientId);
				return "seeHomesClient";
			} else { // Se o utilizador não é um cliente
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/seeHomesAnunciante/{logedAnuncianteId}")
	public String getSeeHomesAnunciante(@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId,
			Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedAnuncianteId) { // Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();
			Anunciante a = new Anunciante();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId) {
					isAnunciante[0] = true;
					a.setEmail(anunciante.getEmail());
				}
			});

			ArrayList<Home> listHomes = new ArrayList<>();

			Iterable<Home> homes = homeRepository.findAll();

			homes.forEach((home) -> {
				if (home.getResponsibleUser() != null)
					if (home.getResponsibleUser().getEmail().equals(a.getEmail())) {
						listHomes.add(home);
					}
			});

			if (isAnunciante[0]) { // Se o utilizador é um anunciante
				model.addAttribute("anuncianteHomes", listHomes);
				model.addAttribute("logedAnuncianteId", logedAnuncianteId);
				return "seeHomesAnunciante";
			} else { // Se o utilizador não é um anunciante
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/seeContractsClient/{logedClientId}")
	public String getSeeContractsClient(@PathVariable(value = "logedClientId") Integer logedClientId, Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedClientId) { // Se sim
			boolean[] isClient = new boolean[1];
			isClient[0] = false;

			Iterable<Client> clients = clientRepository.findAll();
			Client c = new Client();

			clients.forEach((client) -> {
				if (client.getCId() == logedClientId) {
					isClient[0] = true;
					c.setEmail(client.getEmail());
				}
			});

			ArrayList<Contract> listContracts = new ArrayList<>();

			Iterable<Contract> contracts = contractRepository.findAll();

			contracts.forEach((contract) -> {
				if (contract.getClientUser() != null && contract.getStatus() != null)
					if (contract.getClientUser().getEmail().equals(c.getEmail())
							&& contract.getStatus().toLowerCase().equals("assinado")) {
						listContracts.add(contract);
					}
			});

			if (isClient[0]) { // Se o utilizador é um cliente
				model.addAttribute("clientContracts", listContracts);
				model.addAttribute("logedClientId", logedClientId);
				return "seeContractsClient";
			} else { // Se o utilizador não é um cliente
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/seeContractsAnunciante/{logedAnuncianteId}")
	public String getSeeContractsAnunciante(@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId,
			Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedAnuncianteId) { // Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();
			Anunciante a = new Anunciante();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId) {
					isAnunciante[0] = true;
					a.setEmail(anunciante.getEmail());
				}
			});

			ArrayList<Contract> listContracts = new ArrayList<>();

			Iterable<Contract> contracts = contractRepository.findAll();

			contracts.forEach((contract) -> {
				if (contract.getResponsibleUser() != null && contract.getStatus() != null)
					if (contract.getResponsibleUser().getEmail().equals(a.getEmail())
							&& contract.getStatus().toLowerCase().equals("assinado")) {
						listContracts.add(contract);
					}
			});

			if (isAnunciante[0]) { // Se o utilizador é um anunciante
				model.addAttribute("anuncianteContracts", listContracts);
				model.addAttribute("logedAnuncianteId", logedAnuncianteId);
				return "seeContractsAnunciante";
			} else { // Se o utilizador não é um cliente
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/removeAnuncianteArrendamentoContract/{logedAnuncianteId}/{contractId}")
	public String getRemoveAnuncianteContract(@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId,
			@PathVariable(value = "contractId") Integer contractId, Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedAnuncianteId) { // Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId) {
					isAnunciante[0] = true;
				}
			});

			if (!isAnunciante[0]) // Se o utilizador não é um anunciante
				return "index";

			Iterable<Contract> contracts = contractRepository.findAll();

			contracts.forEach((contract) -> {
				if (contract.getCId() == contractId)
					if (contract.getStatus().toLowerCase().equals("assinado")
							&& contract.getResponsibleUser().getAId() == logedAnuncianteId
							&& contract.getHome().getType().toLowerCase().equals("arrendamento")) {
						contract.setStatus("disponível");
						contract.getHome().setStatus("disponível");
						contract.setClientUser(null);
						contractRepository.save(contract);
					}
			});

			// Se o utilizador é um anunciante
			model.addAttribute("logedAnuncianteId", logedAnuncianteId);
			return "operacaoBemSucedidaAnunciante";
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/seePendingContractsClient/{logedClientId}")
	public String getSeePendingContractsClient(@PathVariable(value = "logedClientId") Integer logedClientId,
			Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedClientId) { // Se sim
			boolean[] isClient = new boolean[1];
			isClient[0] = false;

			Iterable<Client> clients = clientRepository.findAll();
			Client c = new Client();

			clients.forEach((client) -> {
				if (client.getCId() == logedClientId) {
					isClient[0] = true;
					c.setEmail(client.getEmail());
				}
			});

			ArrayList<Contract> listPendingContracts = new ArrayList<>();

			Iterable<Contract> contracts = contractRepository.findAll();

			contracts.forEach((contract) -> {
				if (contract.getClientUser() != null && contract.getStatus() != null)
					if (contract.getClientUser().getEmail().equals(c.getEmail())
							&& contract.getStatus().toLowerCase().equals("pendente")) {
						listPendingContracts.add(contract);
					}
			});

			if (isClient[0]) { // Se o utilizador é um cliente
				model.addAttribute("clientPendingContracts", listPendingContracts);
				model.addAttribute("logedClientId", logedClientId);
				return "seePendingContractsClient";
			} else { // Se o utilizador não é um cliente
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/removeClientPendingContract/{logedClientId}/{pendingContractId}")
	public String getRemoveClientPendingContract(@PathVariable(value = "logedClientId") Integer logedClientId,
			@PathVariable(value = "pendingContractId") Integer pendingContractId, Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedClientId) { // Se sim
			boolean[] isClient = new boolean[1];
			isClient[0] = false;

			Iterable<Client> clients = clientRepository.findAll();

			clients.forEach((client) -> {
				if (client.getCId() == logedClientId) {
					isClient[0] = true;
				}
			});

			if (!isClient[0]) // Se o utilizador não é um cliente
				return "index";

			Iterable<Contract> contracts = contractRepository.findAll();

			contracts.forEach((contract) -> {
				if (contract.getCId() == pendingContractId)
					if (contract.getStatus().toLowerCase().equals("pendente")
							&& contract.getClientUser().getCId() == logedClientId) {
						contract.setStatus("disponível");
						contract.getHome().setStatus("disponível");
						contract.setClientUser(null);
						contractRepository.save(contract);
					}
			});

			// Se o utilizador é um cliente
			model.addAttribute("logedClientId", logedClientId);
			return "operacaoBemSucedidaClient";
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/seePendingContractsAnunciante/{logedAnuncianteId}")
	public String getSeePendingContractsAnunciante(@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId,
			Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedAnuncianteId) { // Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();
			Anunciante a = new Anunciante();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId) {
					isAnunciante[0] = true;
					a.setEmail(anunciante.getEmail());
				}
			});

			ArrayList<Contract> listPendingContracts = new ArrayList<>();

			Iterable<Contract> contracts = contractRepository.findAll();

			contracts.forEach((contract) -> {
				if (contract.getResponsibleUser() != null && contract.getStatus() != null)
					if (contract.getResponsibleUser().getEmail().equals(a.getEmail())
							&& contract.getStatus().toLowerCase().equals("pendente")) {
						listPendingContracts.add(contract);
					}
			});

			if (isAnunciante[0]) { // Se o utilizador é um anunciante
				model.addAttribute("anunciantePendingContracts", listPendingContracts);
				model.addAttribute("logedAnuncianteId", logedAnuncianteId);
				return "seePendingContractsAnunciante";
			} else { // Se o utilizador não é um anunciante
				return "index";
			}
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/aceptAnunciantePendingContract/{logedAnuncianteId}/{contractId}")
	public String getAceptAnunciantePendingContract(
			@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId,
			@PathVariable(value = "contractId") Integer contractId, Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedAnuncianteId) { // Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId) {
					isAnunciante[0] = true;
				}
			});

			if (!isAnunciante[0]) // Se o utilizador não é um anunciante
				return "index";

			Iterable<Contract> contracts = contractRepository.findAll();

			contracts.forEach((contract) -> {
				if (contract.getCId() == contractId)
					if (contract.getStatus().toLowerCase().equals("pendente")
							&& contract.getResponsibleUser().getAId() == logedAnuncianteId) {
						contract.setStatus("assinado");
						if (contract.getHome().getType().toLowerCase().equals("venda")) {
							contract.getHome().setStatus("vendida");
							contract.getHome().setResponsibleUser(null);
							contract.getResponsibleUser().incHousesSold();
							contract.getResponsibleUser().incTotalSold(contract.getHome().getCost());
						}
						if (contract.getHome().getType().toLowerCase().equals("arrendamento")) {
							contract.getHome().setStatus("arrendada");
							contract.getResponsibleUser().incRentedHouses();
							contract.getResponsibleUser().incTotalArrendamento(contract.getHome().getCost());
						}
						contractRepository.save(contract);
					}
			});

			// Se o utilizador é um anunciante
			model.addAttribute("logedAnuncianteId", logedAnuncianteId);
			return "operacaoBemSucedidaAnunciante";
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/removeAnunciantePendingContract/{logedAnuncianteId}/{contractId}")
	public String getRemoveAnunciantePendingContract(
			@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId,
			@PathVariable(value = "contractId") Integer contractId, Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedAnuncianteId) { // Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId) {
					isAnunciante[0] = true;
				}
			});

			if (!isAnunciante[0]) // Se o utilizador não é um anunciante
				return "index";

			Iterable<Contract> contracts = contractRepository.findAll();

			contracts.forEach((contract) -> {
				if (contract.getCId() == contractId)
					if (contract.getStatus().toLowerCase().equals("pendente")
							&& contract.getResponsibleUser().getAId() == logedAnuncianteId) {
						contract.setStatus("disponível");
						contract.getHome().setStatus("disponível");
						contract.setClientUser(null);
						contractRepository.save(contract);
					}
			});

			// Se o utilizador é um anunciante
			model.addAttribute("logedAnuncianteId", logedAnuncianteId);
			return "operacaoBemSucedidaAnunciante";
		}
		// Se não
		return "index";
	}

	@GetMapping(path = "/seeStatisticsAnunciante/{logedAnuncianteId}")
	public String getSeeStatisticsAnunciante(@PathVariable(value = "logedAnuncianteId") Integer logedAnuncianteId,
			Model model) {
		// Verifica se o utilizador está com a sessão ativa
		if (login == true && idLogado == logedAnuncianteId) { // Se sim
			boolean[] isAnunciante = new boolean[1];
			isAnunciante[0] = false;

			Iterable<Anunciante> anunciantes = anuncianteRepository.findAll();
			Anunciante a = new Anunciante();

			anunciantes.forEach((anunciante) -> {
				if (anunciante.getAId() == logedAnuncianteId) {
					isAnunciante[0] = true;
					a.setEmail(anunciante.getEmail());
					model.addAttribute("rentedHouses", anunciante.getRentedHouses());
					model.addAttribute("housesSold", anunciante.getHousesSold());
					model.addAttribute("totalRented", anunciante.getTotalArrendamento());
					model.addAttribute("totalSold", anunciante.getTotalSold());
				}
			});

			if (!isAnunciante[0]) // Se o utilizador não é um anunciante
				return "index";

			// Se o utilizador é um anunciante
			ArrayList<String> listCities = new ArrayList<>();

			Iterable<Home> homes = homeRepository.findAll();

			homes.forEach((home) -> {
				if (home.getResponsibleUser() != null)
					if (home.getResponsibleUser().getEmail().equals(a.getEmail())) {
						if (!listCities.contains(home.getCity().toLowerCase()))
							listCities.add(home.getCity().toLowerCase());
					}
			});

			model.addAttribute("numCities", listCities.size());

			ArrayList<Integer> listClients = new ArrayList<>();

			Iterable<Contract> contracts = contractRepository.findAll();

			contracts.forEach((contract) -> {
				if (contract.getResponsibleUser().getAId() == logedAnuncianteId && contract.getClientUser() != null)
					if (!listClients.contains(contract.getClientUser().getCId())) {
						listClients.add(contract.getClientUser().getCId());
					}
			});

			model.addAttribute("numClients", listClients.size());
			model.addAttribute("logedAnuncianteId", logedAnuncianteId);
			return "seeStatisticsAnunciante";
		}
		// Se não
		return "index";
	}
}
