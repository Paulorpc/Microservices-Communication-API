package br.blog.smarti.ms.communication.buyprocess.services.bank;

import java.io.IOException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.blog.smarti.ms.communication.buyprocess.dtos.BankRetornoDto;
import br.blog.smarti.ms.communication.buyprocess.dtos.CompraChaveDto;
import br.blog.smarti.ms.communication.buyprocess.dtos.PagamentoDto;

@Service
public class BankService {

	@Value("${bank.link}")
	private String link;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public PagamentoRetorno pagar(CompraChaveDto compraChaveDto) throws IOException {
		
		PagamentoDto json = new PagamentoDto();
		json.setNroCartao(compraChaveDto.getCompraDto().getNroCartao());
		json.setCodigoSegurancaCartao(compraChaveDto.getCompraDto().getCodigoSegurancaCartao());
		json.setValorCompra(compraChaveDto.getCompraDto().getValorPassagem());
		
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<PagamentoDto> entity = new HttpEntity<PagamentoDto>(json, headers);

		try {
			ResponseEntity<BankRetornoDto> bankRetorno = restTemplate.exchange(link, HttpMethod.POST, entity, BankRetornoDto.class);
			return new PagamentoRetorno(bankRetorno.getBody().getMensagem(), true);
		}catch(HttpClientErrorException ex){
			if( ex.getStatusCode() == HttpStatus.BAD_REQUEST ) {
				ObjectMapper mapper = new ObjectMapper();
				BankRetornoDto obj = mapper.readValue(ex.getResponseBodyAsString(), BankRetornoDto.class);
				return new PagamentoRetorno(obj.getMensagem(), false);
			}
			throw ex;
		}catch (RuntimeException ex) {
			throw ex;
		}

	}

}