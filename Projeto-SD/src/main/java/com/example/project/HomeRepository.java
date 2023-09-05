package com.example.project;

import org.springframework.data.repository.CrudRepository;

import com.example.project.Home;

// This will be AUTO IMPLEMENTED by Spring into a Bean called homeRepository
// CRUD refers Create, Read, Update, Delete

public interface HomeRepository extends CrudRepository<Home, Integer> {

}