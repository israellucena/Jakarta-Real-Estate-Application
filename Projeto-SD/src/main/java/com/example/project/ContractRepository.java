package com.example.project;

import org.springframework.data.repository.CrudRepository;

import com.example.project.Contract;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ContractRepository extends CrudRepository<Contract, Integer> {

}