import com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration.MyDevApiConfiguration;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.MyDevApiClient;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token.MyDevTokenService;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevUserView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.UserItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class MyDevApiClientLocalItTest {

    @Mock
    MyDevApiConfiguration apiConfiguration;

    @Mock
    RestTemplate restTemplate;

    @Mock
    MyDevTokenService tokenService;

    @InjectMocks
    MyDevApiClient sut = new MyDevApiClient();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        apiConfiguration.baseUrl = "https://hr-bnpparibas-stg.csod.com/";
        apiConfiguration.users = "services/api/x/odata/api/views/vw_rpt_user";
    }

    @Test
    public void testGetUserData() {

        Mockito.when(tokenService.getAccessToken()).thenReturn("TEST_TOKEN");

        MyDevUserView mockedUserView = new MyDevUserView();

        UserItem user = new UserItem();
        user.setId(1);
        user.setLanguageId(2);
        user.setCollaborator("cesadmin");

        List<UserItem> users = new ArrayList<>();
        users.add(user);

        mockedUserView.setValue(users);

        AccessTokenMatcher accessTokenMatcher = new AccessTokenMatcher("Bearer TEST_TOKEN");

        Mockito.when(restTemplate.exchange(
                eq("https://hr-bnpparibas-stg.csod.com/services/api/x/odata/api/views/vw_rpt_user?$filter=user_ref%20eq%20'cesadmin'&$select=user_ref,user_id,user_language_id"),
                eq(HttpMethod.GET),
                Matchers.argThat(accessTokenMatcher),
                eq(MyDevUserView.class)))
                .thenReturn(new ResponseEntity<>(mockedUserView, HttpStatus.OK));

        MyDevUserView userView = sut.getUserData("cesadmin");

        Assert.assertNotNull(userView);
        Assert.assertNotNull(userView.getValue());
        Assert.assertEquals(1, userView.getValue().size());
        Assert.assertEquals(Integer.valueOf(1), userView.getValue().get(0).getId());
        Assert.assertEquals(Integer.valueOf(2), userView.getValue().get(0).getLanguageId());
        Assert.assertEquals("cesadmin", userView.getValue().get(0).getCollaborator());
    }


    private class AccessTokenMatcher extends ArgumentMatcher<HttpEntity> {

        private final String compareValue;

        public AccessTokenMatcher(String compareValue) {
            this.compareValue= compareValue;
        }

        @Override
        public boolean matches(Object argument) {

            HttpEntity entity = (HttpEntity) argument;

            if (compareValue != null) {
                if (entity != null) {
                    HttpHeaders headers = entity.getHeaders();
                    if (headers != null) {
                        List<String> token = headers.getOrDefault("Authorization", null);
                        return !token.isEmpty() && token.get(0).equals(compareValue);
                    } else {
                        return false;
                    }
                }
            } else {
                return entity == null ||
                        entity.getHeaders() == null ||
                        entity.getHeaders().get("Authorization").isEmpty() ||
                        entity.getHeaders().get("Authorization").get(0) == null;
            }
            return false;
        }
    }
}