package com.example.project;

import org.springframework.data.repository.CrudRepository;

import com.example.project.Client;

// This will be AUTO IMPLEMENTED by Spring into a Bean called clientRepository
// CRUD refers Create, Read, Update, Delete

public interface ClientRepository extends CrudRepository<Client, Integer> {

}