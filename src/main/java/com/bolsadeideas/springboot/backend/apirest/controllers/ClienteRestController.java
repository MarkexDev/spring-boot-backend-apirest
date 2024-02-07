package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.services.IClienteService;

import jakarta.validation.Valid;

/*
 * API REST
 * REST es una arquitectura de estilo para una interfaz de programación de aplicaciones
 * En esta clase se encuentran los metodos necesarios para el envio y la recepcion de datos
 */

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController //Crea una instancia en el contexto de Spring de la clase que contenga la anotación, de la misma forma que se crearía con la anotación @Component.
@RequestMapping("/api") //permite asignar una solicitud HTTP a un método o clase usando algunos criterios básicos cómo la ruta REST o el método HTTP.
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")//Mapea la URL del ednpoint. Anotación para asignar solicitudes HTTP GET a métodos de controlador específicos
	public List<Cliente> index(){
		return clienteService.findAll(); //Devuelve la lista de clientes en formato JSON
	}
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		
		/*En esta seccion de codigo se lleva a cabo el 
		 *manejo de errores del servidor (en la BDD)
		 */
		try {
			cliente = clienteService.findById(id);
		} catch(DataAccessException e) {
			response.put("mensaje", "Ha ocurrido un error al realizar la consulta en la Base De Datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		//Finaliza el control de errores de BDD
		
		
		if(cliente == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			
			List<String> errors = new ArrayList<>();
			
			for(FieldError err: result.getFieldErrors()) {
				errors.add("El campo '" + err.getField() + "' " + err.getDefaultMessage());
			}
			
			/* Implementacion de validacion en Java 8 API STREAM
			 
			 List<String> errors = result.getFieldErrors()
			 		.stream()
			 		.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
			 		.colection(Collectors.toList());
			 
			 */
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			clienteNew = clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Ha ocurrido un error al realizar el INSERT en la Base De Datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido creado con éxito!");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		
		Cliente clienteActual = this.clienteService.findById(id);
		
		Cliente clienteUpdated = null;
		
		Map<String, Object> response = new HashMap<>();
		
if(result.hasErrors()) {
			
			List<String> errors = new ArrayList<>();
			
			for(FieldError err: result.getFieldErrors()) {
				errors.add("El campo '" + err.getField() + "' " + err.getDefaultMessage());
			}
			
			/* Implementacion de validacion en Java 8 API STREAM
			 
			 List<String> errors = result.getFieldErrors()
			 		.stream()
			 		.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
			 		.colection(Collectors.toList());
			 
			 */
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if(clienteActual == null) {
			response.put("mensaje", "Error: no se pudo editar. El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
		clienteActual.setNombre(cliente.getNombre());
		clienteActual.setApellido(cliente.getApellido());
		clienteActual.setEmail(cliente.getEmail());
		clienteActual.setCreateAt(cliente.getCreateAt());
		
		clienteUpdated = this.clienteService.save(clienteActual);
		
		} catch (DataAccessException e) {
			response.put("mensaje", "Ha ocurrido un error al ACTUALIZAR el cliente en la Base De Datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido ACTUALIZADO con éxito!");
		response.put("cliente", clienteUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			Cliente currentCliente = this.clienteService.findById(id);
			this.clienteService.delete(currentCliente);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Ha ocurrido un error al ELIMINAR el cliente en la Base De Datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido ELIMINADO con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
}
