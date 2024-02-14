package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.validation.Valid;

@Tag(name = "UCSBDiningCommonsMenuItems")
@RequestMapping("/api/ucsbdiningcommonsmenuitems")
@RestController
@Slf4j

public class UCSBDiningCommonsMenuItemsController extends ApiController{
    
    @Autowired
    UCSBDiningCommonsMenuItemsRepository ucsbDiningCommonsMenuItemsRepository;

    @Operation(summary = "List all UCSB Dining Common Menu Items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItems> allMenuItems(){
        Iterable<UCSBDiningCommonsMenuItems> menuItems = ucsbDiningCommonsMenuItemsRepository.findAll();
        return menuItems;
    }

    @Operation(summary = "Create a New Menu Item")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItems postMenuItems(
        @Parameter(name="diningCommonsCode") @RequestParam String diningCommonsCode,
        @Parameter(name="name") @RequestParam String name,
        @Parameter(name="station") @RequestParam String station) 
        throws JsonProcessingException {

        UCSBDiningCommonsMenuItems menuItems = new UCSBDiningCommonsMenuItems();
        menuItems.setDiningCommonsCode(diningCommonsCode);
        menuItems.setName(name);
        menuItems.setStation(station);

        UCSBDiningCommonsMenuItems savedMenuItems = ucsbDiningCommonsMenuItemsRepository.save(menuItems);

        return savedMenuItems;
    }

    @Operation(summary= "Get a Single Menu Item")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBDiningCommonsMenuItems getById(
        @Parameter(name="id") @RequestParam Long id){
        UCSBDiningCommonsMenuItems menuItems = ucsbDiningCommonsMenuItemsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

        return menuItems;
    }

    /*
    @Operation(summary= "Delete a UCSBDiningCommonsMenuItem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteMenuItem(
        @Parameter(name="id") @RequestParam Long id){
        UCSBDiningCommonsMenuItems menuItems = ucsbDiningCommonsMenuItemsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

        ucsbDiningCommonsMenuItemsRepository.delete(menuItems);
        return genericMessage("UCSBDiningCommonsMenuItems with id %s deleted".formatted(id));
    }
    
    @Operation(summary= "Update a single Menu Items")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBDiningCommonsMenuItems updateMenuItems(
        @Parameter(name="id") @RequestParam Long id,
        @RequestBody @Valid UCSBDiningCommonsMenuItems incoming
    ){
        UCSBDiningCommonsMenuItems menuItems = ucsbDiningCommonsMenuItemsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));
        
        menuItems.setDiningCommonsCode(incoming.getDiningCommonsCode());
        menuItems.setName(incoming.getName());
        menuItems.setStation(incoming.getStation());

        ucsbDiningCommonsMenuItemsRepository.save(menuItems);
        
        return menuItems;
    }*/
}
