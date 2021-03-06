package org.formacio.repositori;


import org.formacio.domain.Factura;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface FacturesRepositori extends CrudRepository<Factura, Long> {

	@Query("select sum(linia.total) from Factura f join f.linies linia where f.client.nom = ?1")
	public Number totalClient(String client);

	List<Factura> findByClientNom(String nom);
	
}
