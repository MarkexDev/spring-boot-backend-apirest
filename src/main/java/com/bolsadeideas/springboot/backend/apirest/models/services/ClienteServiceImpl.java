package com.bolsadeideas.springboot.backend.apirest.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.backend.apirest.models.dao.IClienteDao;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;

/*
 * En esta clase se lleva a cabo la implementación de
 * los metodos de la clase IClienteService.java
 */

@Service
public class ClienteServiceImpl implements IClienteService{
	
	@Autowired
	private IClienteDao clienteDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Cliente> findAll() {
		return (List<Cliente>) clienteDao.findAll();
	}
	
	@Transactional(readOnly = true)
	@Override
	public Cliente findById(Long id) {	
		return clienteDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public Cliente save(Cliente cliente) {
		return clienteDao.save(cliente);
	}
	
	@Transactional
	@Override
	public void delete(Cliente cliente) {
		clienteDao.delete(cliente);
	}

}
