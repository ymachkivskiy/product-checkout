package integration;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jm.interview.pccheckout.Application;
import org.jm.interview.pccheckout.application.ProductOperations;
import org.jm.interview.pccheckout.domain.exceptions.ProductNotFoundException;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@WebMvcTest
public class MvcLayerIntegrationTests {
    private ObjectMapper resourcesMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductOperations productOperations;

    @Before
    public void setUp() {
        resourcesMapper = new ObjectMapper();
        resourcesMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }


    @Test
    public void update_product_price() throws Exception {

        ProductPriceResource productPrice = productPrice("p-1", 100);
        mvc.perform(put("/products")
                .content(resourcesMapper.writeValueAsString(productPrice))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productOperations).defineProductPrice(productPrice);
        verifyNoMoreInteractions(productOperations);
    }

    @Test
    public void error_404_on_unknown_product_in_checkout_card() throws Exception {
        ShoppingCardResource card = ShoppingCardResource.builder().products(asList(productQuantity("p-a", 10))).build();

        doThrow(new ProductNotFoundException("p-a"))
                .when(productOperations).checkout(card);

        mvc.perform(post("/checkout")
                .content(resourcesMapper.writeValueAsString(card))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productOperations).checkout(card);
        verifyNoMoreInteractions(productOperations);
    }


    @Test
    public void error_404_on_unknown_product_in_multiprice() throws Exception {

        ProductMultiPriceResource productMultiPriceResource = productMultiprice("product-123", 178, 30);
        doThrow(new ProductNotFoundException("product-123"))
                .when(productOperations).defineProductMultiPrice(productMultiPriceResource);

        mvc.perform(put("/products/multi-price")
                .content(resourcesMapper.writeValueAsString(productMultiPriceResource))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productOperations).defineProductMultiPrice(productMultiPriceResource);
        verifyNoMoreInteractions(productOperations);
    }

    private ProductMultiPriceResource productMultiprice(String productName, int multipriceCents, int quantity) {
        return ProductMultiPriceResource.builder().price(new PriceResource(multipriceCents)).product(new ProductResource(productName)).quantity(quantity).build();
    }

    private ProductPriceResource productPrice(String productName, int cents) {
        return ProductPriceResource.builder().product(new ProductResource(productName)).price(PriceResource.builder().cents(cents).build()).build();
    }

    private ProductQuantityResource productQuantity(String productName, int quantity) {
        return ProductQuantityResource.builder().product(new ProductResource(productName)).quantity(quantity).build();
    }
}
