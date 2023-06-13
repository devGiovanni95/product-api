package com.giovanni.springboot.controllers;

import com.giovanni.springboot.dtos.ProductRecordDto;
import com.giovanni.springboot.models.ProductModel;
import com.giovanni.springboot.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    @Autowired/*Construtor  para inicializar a injecao sem necessitar do construtor*/
    ProductRepository productRepository;

    @PostMapping("/products")/*Uri endereco de requisicao*/
    /*Criação do metodo do tipo record e @valid para validar as definicoes inseridas no record*/
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
        //instanciando objeto
        var productModel = new ProductModel();
        //Copiando de dto onde converte productRecordDto para => productModel
         BeanUtils.copyProperties(productRecordDto, productModel);
         //retorna um status da requisicao e os dados inseridos
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts(){
        List<ProductModel> productModelList = productRepository.findAll();

        if(!productModelList.isEmpty()){
            for (ProductModel product :productModelList){
                UUID uuid = product .getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getOneProducts(uuid)).withSelfRel());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(productModelList);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProducts(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> productModel = productRepository.findById(id);
        //outra maneira indicada pelo intellij
        //   return productModel.<ResponseEntity<Object>>map(model -> ResponseEntity.status(HttpStatus.OK).body(model)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found."));
        if(productModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        productModel.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));
        return ResponseEntity.status(HttpStatus.OK).body(productModel.get());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto){
        //Primeiro conferimo se existe
        Optional<ProductModel> productModel = productRepository.findById(id);
        if(productModel.isEmpty()){
            //se nao existir
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        //se existir atualizamos seus valores
        var productModelAux = productModel.get();
        BeanUtils.copyProperties(productRecordDto, productModelAux);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModelAux));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> productModel = productRepository.findById(id);
        if(productModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        productRepository.delete(productModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
    }


}
