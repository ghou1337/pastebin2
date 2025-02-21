package pl.pastebin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.pastebin.client.HashClient;
import pl.pastebin.model.Data;
import pl.pastebin.model.DataCloudResponse;
import pl.pastebin.cloud.service.GoogleCloudStorageService;
import pl.pastebin.model.MetadataDAO;
import pl.pastebin.services.MetadataService;
import reactor.core.publisher.Mono;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.sql.Timestamp;
import java.util.UUID;

@WebMvcTest(controllers = SharingController.class)
@ExtendWith(MockitoExtension.class)
public class SharingControllerTest {
    @MockBean
    private MetadataService metadataService;

    @MockBean
    private GoogleCloudStorageService googleCloudStorageService;

    @MockBean
    private HashClient hashClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CLOUD_URL = "https://storage.googleapis.com/bucket/file_name";
    private static final String API_POST_ENDPOINT = "/api/post";
    private static final String API_GET_TEXT_BY_HASH_ENDPOINT = "/api/{hash}";
    private static final String TIME_STR = "2024-01-01 10:11:11";
    private static final String DATA_NAME = "TEST FILE NAME";
    private static final String DATA_TEXT = "TEST text longer 3 char";
    private static final Timestamp DATA_EXPRESSION_TIME = Timestamp.valueOf("2024-01-26 10:11:11");
    private static final Timestamp DATA_CREATED_AT = Timestamp.valueOf("2024-01-01 10:11:11");
    private static final int DATA_EXPRESSION_DAYS = 25;
    private static final String DATA_HASH = UUID.randomUUID().toString();


    @BeforeEach
    void setUp() {

    }

    @Test
    void postMessageTest() throws Exception {
        // given
        Data data = new Data(
                DATA_NAME,
                DATA_TEXT,
                DATA_EXPRESSION_DAYS,
                DATA_HASH);
        MetadataDAO metadataDAO = new MetadataDAO(
                DATA_CREATED_AT,
                DATA_EXPRESSION_TIME
        );
        String json = objectMapper.writeValueAsString(data);

        //when
        Mockito.when(hashClient.getHash()).thenReturn(Mono.just(DATA_HASH));
        Mockito.when(metadataService.setMetadataDate(data)).thenReturn(metadataDAO);
        Mockito.when(metadataService.saveMetadataFromFile(data, metadataDAO)).thenReturn(DATA_HASH);
        Mockito.when(googleCloudStorageService.uploadFile(data, metadataDAO)).thenReturn(CLOUD_URL);

        //then
        mockMvc.perform(post(API_POST_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(data.getName()))
                .andExpect(jsonPath("$.text").value(data.getText()))
                .andExpect(jsonPath("$.hash").value(data.getHash()))
                .andExpect(jsonPath("$.expressionDays").value(data.getExpressionDays()));
    }

    @Test
    void postMessageWithInvalidDataTest() throws Exception {
        // given
        Data emptyTextData = new Data(
                DATA_NAME,
                "",
                DATA_EXPRESSION_DAYS,
                DATA_HASH);

        // when
        String json = objectMapper.writeValueAsString(emptyTextData);

        // then
        mockMvc.perform(post(API_POST_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postMessageWithInvalidDateTest() throws Exception {
        // given
        Data invalidDateData = new Data(
                DATA_NAME,
                DATA_TEXT,
                -1,
                DATA_HASH);
        // when
        String json = objectMapper.writeValueAsString(invalidDateData);

        // then
        mockMvc.perform(post(API_POST_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTextByHashTest() throws Exception {
        // given
        DataCloudResponse dataCloudResponse = new DataCloudResponse(
                DATA_NAME,
                DATA_TEXT,
                DATA_HASH
        );

        // when
        Mockito.when(googleCloudStorageService.getFile(DATA_HASH)).thenReturn(dataCloudResponse);

        // then
        mockMvc.perform(get(API_GET_TEXT_BY_HASH_ENDPOINT, DATA_HASH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(DATA_NAME))
                .andExpect(jsonPath("$.text").value(DATA_TEXT))
                .andExpect(jsonPath("$.hash").value(DATA_HASH))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
