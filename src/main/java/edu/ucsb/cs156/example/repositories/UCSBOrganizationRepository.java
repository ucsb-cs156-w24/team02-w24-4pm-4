package edu.ucsb.cs156.example.repositories;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs156.example.entities.UCSBOrganization;


public interface UCSBOrganizationRepository extends CrudRepository<UCSBOrganization, String>{

}
