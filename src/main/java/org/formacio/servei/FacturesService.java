package org.formacio.servei;

import org.formacio.domain.Factura;
import org.formacio.domain.LiniaFactura;
import org.formacio.repositori.FacturesRepositori;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class FacturesService {

	@Autowired
	private FacturesRepositori facturesRepositori;

	@Autowired
	private FidalitzacioService service;
	
	/*
	 * Aquest metode ha de carregar la factura amb id idFactura i afegir una nova linia amb les dades
	 * passades (producte i totalProducte)
	 * 
	 * S'ha de retornar la factura modificada
	 * 
	 * Per implementar aquest metode necessitareu una referencia (dependencia) a FacturesRepositori
	 */


	public Factura afegirProducte (long idFactura, String producte, int totalProducte) {
		Optional<Factura> factura = facturesRepositori.findById(idFactura);

		if (factura.isPresent()){
			LiniaFactura linia = new LiniaFactura();
			linia.setProducte(producte);
			linia.setTotal(totalProducte);
			factura.get().getLinies().add(linia);
			facturesRepositori.save(factura.get());

			premi(factura.get());
		}
		return factura.get();
	}

	public void premi(Factura factura){
		final int LINIAGUANYADORA = 4;
		if(factura.getLinies().size()>= LINIAGUANYADORA){
			service.notificaRegal(factura.getClient().getEmail());
		}
	}
}
