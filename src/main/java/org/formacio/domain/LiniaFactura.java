package org.formacio.domain;

public class LiniaFactura {

	
	private Long id;
	
	private String producte;

    private Integer total;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProducte() {
		return producte;
	}

	public void setProducte(String producte) {
		this.producte = producte;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
	
	
}
