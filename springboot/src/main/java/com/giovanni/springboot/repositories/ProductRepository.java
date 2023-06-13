package com.giovanni.springboot.repositories;

import com.giovanni.springboot.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository/*Transacoes com o banco de dados*/
public interface ProductRepository extends JpaRepository<ProductModel, UUID> {


}
