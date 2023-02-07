package iwcatalog.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import iwcatalog.dto.ProductDTO;
import iwcatalog.resources.ProductResource;
import iwcatalog.services.ProductService;
import iwcatalog.services.exceptions.DatabaseException;
import iwcatalog.services.exceptions.ResourceNotFoundException;
import iwcatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourcesTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        when(productService.findAllPaged(any())).thenReturn(page);

        when(productService.findById(existingId)).thenReturn(productDTO);
        when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(productService.update(eq(existingId), any())).thenReturn(productDTO);
        when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(productService).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        doThrow(DatabaseException.class).when(productService).delete(dependentId);

        when(productService.insert(any())).thenReturn(productDTO);
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions resultActions =
                mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        ResultActions resultActions =
                mockMvc.perform(delete("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void insertShouldReturnProductDTOCreated() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.description").exists());
        resultActions.andExpect(jsonPath("$.price").exists());
        resultActions.andExpect(jsonPath("$.imgUrl").exists());
        resultActions.andExpect(jsonPath("$.date").exists());
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions =
                mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions =
                mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.description").exists());
        resultActions.andExpect(jsonPath("$.price").exists());
        resultActions.andExpect(jsonPath("$.imgUrl").exists());
        resultActions.andExpect(jsonPath("$.date").exists());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions resultActions =
                mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions resultActions =
                mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.description").exists());
        resultActions.andExpect(jsonPath("$.price").exists());
        resultActions.andExpect(jsonPath("$.imgUrl").exists());
        resultActions.andExpect(jsonPath("$.date").exists());
    }

    @Test
    void findAllShouldReturnPage() throws Exception {
        ResultActions resultActions =
                mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
    }
}
