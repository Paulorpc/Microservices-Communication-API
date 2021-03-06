package br.blog.smarti.ms.communication.buyfeedback.controllers;

import br.blog.smarti.ms.communication.buyfeedback.entities.CompraRedis;
import br.blog.smarti.ms.communication.buyfeedback.exceptions.NaoFinalizadoException;
import br.blog.smarti.ms.communication.buyfeedback.repositories.CompraRedisRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompraController {

  @Autowired private CompraRedisRepository compraRedisRepository;

  @RequestMapping(path = "/hello", method = RequestMethod.GET)
  public String status() {
    return "msc-buyfeedback is On!";
  }

  @RequestMapping(path = "/{chave}", method = RequestMethod.GET)
  public CompraRedis status(@PathVariable("chave") String chave) {

    Optional<CompraRedis> compra = compraRedisRepository.findById(chave);

    if (!compra.isPresent()) {
      throw new NaoFinalizadoException();
    }

    return compra.get();
  }
}
