import com.ibm.mydev.MyDevSpringApplication;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.MyDevApiClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringJUnit4ClassRunner.class)
@RestClientTest(MyDevApiClient.class)
//@ContextConfiguration(classes = RestClientItConfig.class)
@SpringBootTest(classes = MyDevSpringApplication.class)
@ActiveProfiles("MYDEV_REAL")
public class MyDevApiClientLocalItTest {

    @Autowired
    @Qualifier("MyDevRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private MyDevApiClient sut;

    private MockRestServiceServer mockServer;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void testGetUserData() {

        mockServer
                .expect(requestTo("https://hr-bnpparibas-stg.csod.com/services/api/x/odata/api/views/$metadata#vw_rpt_user"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("cesadmin", MediaType.APPLICATION_JSON));

        String collaborator = sut.getUserData("cesadmin").getValue().get(0).getCollaborator();
        mockServer.verify();

        Assert.assertEquals("cesadmin", collaborator);

    }
}