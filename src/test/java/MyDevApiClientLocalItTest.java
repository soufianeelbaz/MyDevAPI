import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.MyDevApiClient;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token.MyDevTokenService;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class MyDevApiClientLocalItTest {

    @Mock
    RestTemplate restTemplate;

    @Mock
    MyDevTokenService tokenService;

    @InjectMocks
    MyDevApiClient sut = new MyDevApiClient();

    private final String TEST_TOKEN = "TEST_TOKEN";
    private final String CESADMIN_UID = "cesadmin";
    private final int LANGUAGE_ID = 2;
    private final int USER_ID = 1;
    private final List<String> OBJECT_IDS = Arrays.asList("3e677399-2acb-444d-975f-17f37ea16fb5", "bcbdd8e1-688c-4289-ac60-485e92849d62");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(sut, "baseUrl", "https://hr-bnpparibas-stg.csod.com/");
    }

    @Test
    public void testGetUserData() throws IOException {

        ReflectionTestUtils.setField(sut, "users", "services/api/x/odata/api/views/vw_rpt_user");

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

        MyDevUserView userView = sut.getUserData(CESADMIN_UID);

        Assert.assertNotNull(userView);
        Assert.assertNotNull(userView.getValue());
        Assert.assertEquals(1, userView.getValue().size());
        Assert.assertEquals(Integer.valueOf(1), userView.getValue().get(0).getId());
        Assert.assertEquals(Integer.valueOf(2), userView.getValue().get(0).getLanguageId());
        Assert.assertEquals(CESADMIN_UID, userView.getValue().get(0).getCollaborator());
    }

    @Test
    public void testGetTranscriptData() throws IOException {

        ReflectionTestUtils.setField(sut, "transcripts", "services/api/x/odata/api/views/vw_rpt_transcript");

        MyDevTranscriptView mockedTranscriptView = new MyDevTranscriptView();

        List<TranscriptItem> transcriptItems = new ArrayList<>();
        transcriptItems.add(new TranscriptItem(OBJECT_IDS.get(0), 11, true, false, false, true, "2017-11-27T06:54:01.693Z", null, "2017-11-27T07:12:26.657Z", null, USER_ID));
        transcriptItems.add(new TranscriptItem(OBJECT_IDS.get(1), 12, true, false, false, true, "2018-11-27T06:54:01.693Z", null, "2018-11-27T07:12:26.657Z", null, USER_ID));
        mockedTranscriptView.setValue(transcriptItems);

        Mockito.when(tokenService.getAccessToken()).thenReturn(TEST_TOKEN);
        Mockito.when(restTemplate.exchange(
                eq("https://hr-bnpparibas-stg.csod.com/services/api/x/odata/api/views/vw_rpt_transcript?$filter=transc_user_id%20eq%201%20and%20is_removed%20eq%20false%20and%20is_standalone%20eq%20true%20and%20is_latest_version_on_transcript%20eq%20true%20and%20(((user_lo_status_group_id%20eq%2012%20or%20user_lo_status_group_id%20eq%2013)%20and%20archived%20eq%20false)%20or%20(user_lo_status_group_id%20eq%2011%20and%20user_lo_comp_dt%20ge%20cast('2015-01-01',%20Edm.DateTimeOffset)))&$select=transc_object_id,user_lo_status_group_id,is_latest_version_on_transcript,archived,is_removed,is_standalone,user_lo_reg_dt,user_lo_min_due_date,user_lo_comp_dt,training_purpose,transc_user_id"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(MyDevTranscriptView.class)))
                .thenReturn(new ResponseEntity<>(mockedTranscriptView, HttpStatus.OK));

        MyDevTranscriptView transcriptData = sut.getTranscriptData(USER_ID, 2020);

        Assert.assertNotNull(transcriptData);
        Assert.assertNotNull(transcriptData.getValue());
        Assert.assertEquals(2, transcriptData.getValue().size());
        Assert.assertTrue(transcriptData.getValue().get(0).getIsLatest());
        Assert.assertTrue(transcriptData.getValue().get(1).getIsLatest());
        Assert.assertFalse(transcriptData.getValue().get(0).getIsArchived());
        Assert.assertFalse(transcriptData.getValue().get(1).getIsArchived());
        Assert.assertFalse(transcriptData.getValue().get(0).getRemoved());
        Assert.assertFalse(transcriptData.getValue().get(1).getRemoved());
        Assert.assertTrue(transcriptData.getValue().get(0).getStandalone());
        Assert.assertTrue(transcriptData.getValue().get(1).getStandalone());
        Assert.assertEquals(USER_ID, transcriptData.getValue().get(0).getUserId().intValue());
        Assert.assertEquals(USER_ID, transcriptData.getValue().get(1).getUserId().intValue());
    }

    @Test
    public void testGetTrainingData() throws IOException {

        ReflectionTestUtils.setField(sut, "trainings", "services/api/x/odata/api/views/vw_rpt_training");

        MyDevTrainingView mockedTrainingView = new MyDevTrainingView();

        List<TrainingItem> trainingItems = new ArrayList<>();
        trainingItems.add(new TrainingItem("Travailler ma mobilité", null, null, "EVNT", 21, OBJECT_IDS.get(0)));
        trainingItems.add(new TrainingItem("Codes de conduite pour les collaborateurs", null, null, "CRSE", 11, OBJECT_IDS.get(1)));
        mockedTrainingView.setValue(trainingItems);

        Mockito.when(tokenService.getAccessToken()).thenReturn(TEST_TOKEN);
        Mockito.when(restTemplate.exchange(
                eq("https://hr-bnpparibas-stg.csod.com/services/api/x/odata/api/views/vw_rpt_training?$filter=(object_id%20eq%203e677399-2acb-444d-975f-17f37ea16fb5%20or%20object_id%20eq%20bcbdd8e1-688c-4289-ac60-485e92849d62)&$select=object_id,lo_title,lo_start_dt,lo_end_dt,lo_object_type,lo_hours"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(MyDevTrainingView.class)))
                .thenReturn(new ResponseEntity<>(mockedTrainingView, HttpStatus.OK));

        MyDevTrainingView trainingData = sut.getTrainingData(OBJECT_IDS);

        Assert.assertNotNull(trainingData);
        Assert.assertNotNull(trainingData.getValue());
        Assert.assertEquals(2, trainingData.getValue().size());
        Assert.assertEquals(OBJECT_IDS.get(0), trainingData.getValue().get(0).getObjectId());
        Assert.assertEquals(OBJECT_IDS.get(1), trainingData.getValue().get(1).getObjectId());
    }

    @Test
    public void testGetTrainingLocalData() throws IOException {

        ReflectionTestUtils.setField(sut, "trainingsLocal", "services/api/x/odata/api/views/vw_rpt_training_title_local");

        MyDevTrainingLocalView mockedTrainingLocalView = new MyDevTrainingLocalView();

        List<TrainingLocalItem> trainingLocalItems = new ArrayList<>();
        trainingLocalItems.add(new TrainingLocalItem(OBJECT_IDS.get(0), "Travailler ma mobilité", LANGUAGE_ID));
        trainingLocalItems.add(new TrainingLocalItem(OBJECT_IDS.get(1), "Codes de conduite pour les collaborateurs", LANGUAGE_ID));
        mockedTrainingLocalView.setValue(trainingLocalItems);

        Mockito.when(tokenService.getAccessToken()).thenReturn(TEST_TOKEN);
        Mockito.when(restTemplate.exchange(
                eq("https://hr-bnpparibas-stg.csod.com/services/api/x/odata/api/views/vw_rpt_training_title_local?$filter=training_title_local_culture_id%20eq%202%20and%20(training_title_local_object_id%20eq%203e677399-2acb-444d-975f-17f37ea16fb5%20or%20training_title_local_object_id%20eq%20bcbdd8e1-688c-4289-ac60-485e92849d62)"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(MyDevTrainingLocalView.class)))
                .thenReturn(new ResponseEntity<>(mockedTrainingLocalView, HttpStatus.OK));

        MyDevTrainingLocalView trainingLocalData = sut.getTrainingLocalData(LANGUAGE_ID, OBJECT_IDS);

        Assert.assertNotNull(trainingLocalData);
        Assert.assertNotNull(trainingLocalData.getValue());
        Assert.assertEquals(2, trainingLocalData.getValue().size());
        Assert.assertEquals(OBJECT_IDS.get(0), trainingLocalData.getValue().get(0).getObjectId());
        Assert.assertEquals(OBJECT_IDS.get(1), trainingLocalData.getValue().get(1).getObjectId());
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