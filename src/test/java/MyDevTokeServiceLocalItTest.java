import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token.MyDevTokenResponseBody;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token.MyDevTokenService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MyDevTokeServiceLocalItTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    MyDevTokenService sut = new MyDevTokenService(restTemplate);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(sut, "baseUrl", "https://hr-bnpparibas-stg.csod.com/");
        ReflectionTestUtils.setField(sut, "authentication", "services/api/oauth2/token");
    }

    @Test
    public void testRefreshToken() throws IOException {

        ResponseEntity response = getTokenMockedResponse();

        when(restTemplate.exchange(
                eq("https://hr-bnpparibas-stg.csod.com/services/api/oauth2/token"),
                eq(HttpMethod.POST),
                any(),
                eq(MyDevTokenResponseBody.class)))
                .thenReturn(response);

        sut.refreshToken();

        String token = sut.getAccessToken();

        Assert.assertNotNull(token);

        Assert.assertEquals("refreshed_access_token", token);

        verify(restTemplate, times(1)).exchange(
                eq("https://hr-bnpparibas-stg.csod.com/services/api/oauth2/token"),
                eq(HttpMethod.POST),
                any(),
                eq(MyDevTokenResponseBody.class)
        );
    }

    @Test
    public void testGetAccessToken() throws IOException {

        ResponseEntity response = getTokenMockedResponse();

        when(restTemplate.exchange(
                eq("https://hr-bnpparibas-stg.csod.com/services/api/oauth2/token"),
                eq(HttpMethod.POST),
                any(),
                eq(MyDevTokenResponseBody.class)))
                .thenReturn(response);

        ReflectionTestUtils.setField(sut, "token", "existing_access_token");

        String token = sut.getAccessToken();

        Assert.assertNotNull(token);

        Assert.assertEquals("existing_access_token", token);

        verify(restTemplate, never()).exchange(
                eq("https://hr-bnpparibas-stg.csod.com/services/api/oauth2/token"),
                eq(HttpMethod.POST),
                any(),
                eq(MyDevTokenResponseBody.class)
        );
    }

    private ResponseEntity getTokenMockedResponse() {
        MyDevTokenResponseBody response = new MyDevTokenResponseBody();
        response.setAccessToken("refreshed_access_token");
        response.setExpiresIn("3600");
        response.setScope("scope");
        response.setTokenType("token_type");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}