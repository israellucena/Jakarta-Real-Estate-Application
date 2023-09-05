package com.example.project;

import org.springframework.data.repository.CrudRepository;

import com.example.project.Anunciante;

//This will be AUTO IMPLEMENTED by Spring into a Bean called anuncianteRepository
//CRUD refers Create, Read, Update, Delete

public interface AnuncianteRepository extends CrudRepository<Anunciante, Integer> {

}