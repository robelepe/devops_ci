package com.paymentchain.billing;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentchain.billing.common.InvoiceRequestMapper;
import com.paymentchain.billing.common.InvoiceResposeMapper;
import com.paymentchain.billing.controller.InvoiceRestController;
import com.paymentchain.billing.dto.InvoiceRequest;
import com.paymentchain.billing.dto.InvoiceResponse;
import com.paymentchain.billing.entities.Invoice;
import com.paymentchain.billing.respository.InvoiceRepository;



@WebMvcTest(InvoiceRestController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class BasicApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private InvoiceRepository ir;
	
	@MockBean
	InvoiceRequestMapper irm;
	
	@MockBean
	InvoiceResposeMapper irspm;
	
	private static final String PASSWORD = "password";
	private static final String USER = "admin";

	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testCreate() throws Exception {
		Base64.Encoder encoder = Base64.getEncoder();
		String encoding = encoder.encodeToString((USER + ":" + PASSWORD).getBytes());
		Invoice mockdto = new Invoice();
		Mockito.when(ir.save(mockdto)).thenReturn(mockdto);
		Mockito.when(irm.InvoiceRequestToInvoice(new InvoiceRequest())).thenReturn(mockdto);
		Mockito.when(irspm.InvoiceToInvoiceRespose(mockdto)).thenReturn(new InvoiceResponse());

		RequestBuilder request = MockMvcRequestBuilders
                .post("/billing")
                .header("Authorization", "Basic " + encoding)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(mockdto)); 
		
		this.mockMvc.perform(request)
			.andDo(print())
			.andExpect(status().isOk());
		
	}
	
	
	@Test
	public void contextLoads() {
		String message = "Default message change 20220126";
		Assert.assertNotNull(message);
	}

}
