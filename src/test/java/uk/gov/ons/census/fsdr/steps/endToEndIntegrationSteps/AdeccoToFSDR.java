package uk.gov.ons.census.fwmt.tests.acceptance.steps.endToEndIntegrationSteps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import uk.gov.ons.census.fwmt.common.data.modelcase.ModelCase;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.census.fwmt.tests.acceptance.utils.QueueUtils;
import uk.gov.ons.census.fwmt.tests.acceptance.utils.TMMockUtils;
import uk.gov.ons.census.fwmt.tests.acceptance.utils.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;

@Slf4j
@PropertySource("classpath:application.properties")
public class PassHHInstructionsFromRMToTM {

        private String receivedRMMessage = null;
        private static final String RM_REQUEST_RECEIVED = "RM - Request Received";

        @Autowired
        private TMMockUtils tmMockUtils;

        @Autowired
        private QueueUtils queueUtils;

        private GatewayEventMonitor gatewayEventMonitor;

        @Value("${service.firehose.url}")
        private String firehoseUrl;
        @Value("${service.mocktm.url}")
        private String mockTmURL;
        @Value("${service.rabbit.url}")
        private String rabbitLocation;
        private ObjectMapper objectMapper = new ObjectMapper();
        private XMLReader xmlReader = new XMLReader();
        String fooResourceUrl = "http://localhost:5000/rm/customRequest";
        String[] expectedCreateCase = new String[7];
        String[] address = new String[4];

        @Before
        public void setup() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {

        }

        @After
        public void tearDown() {
        }

        @Given("reset the TMMock and MQ")
        public void And_reset_the_TMMock_and_MQ() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
            tmMockUtils.resetMock();
            queueUtils.clearQueues();
        }

        @And("a create case in the MQ with correct data")
        public void rmSendsACreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
                RestTemplate restTemplate = new RestTemplate();
                receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/validCreateCaseInstruction.xml"), Charsets.UTF_8);
                ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
        }

        @And("the case is picked and sent to TM")
        public void the_case_is_picked_and_sent_to_TM() throws IOException, ParserConfigurationException, SAXException {
                expectedCreateCase = xmlReader.xmlAttributeReader();
                ModelCase kase = tmMockUtils.getCaseById(expectedCreateCase[7]);
                Assert.assertEquals("caseId is not correct in TM as sent by RM", expectedCreateCase[7], kase.getId().toString());
            Assert.assertEquals("locality is not correct in TM as sent by RM", expectedCreateCase[8], kase.getAddress().getGeography().toString());
                Assert.assertEquals("UPRN is not correct in TM as sent by RM", expectedCreateCase[1], kase.getAddress().getUprn().toString());
                String addressRemoveOpenBrackets = kase.getAddress().getLines().toString().replace("[", "");
                String addressRemoveCloseBrackets = addressRemoveOpenBrackets.replace("]", "");
                address = addressRemoveCloseBrackets.split(",");
                Assert.assertEquals("Address line1 is not correct in TM as sent by RM", expectedCreateCase[5], address[0]);
                Assert.assertEquals("Postcode is not correct in TM as sent by RM", expectedCreateCase[6], kase.getAddress().getPostcode().toString());
                Assert.assertEquals("Type is not correct in TM as sent by RM", expectedCreateCase[2], kase.getType().toString());
                Assert.assertEquals("Establishment type is not correct in TM as sent by RM", expectedCreateCase[3], kase.getEstabType().toString());
        }

        @Given("a duplicate create case in the MQ with the following data")
        public void rmSendsADuplicateCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
                RestTemplate restTemplate = new RestTemplate();
                receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/validCreateCaseInstruction.xml"), Charsets.UTF_8);
                ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
        }

        @And("the duplicate case is picked and sent to TM")
        public void the_duplicate_case_is_picked_and_sent_to_TM() throws IOException, ParserConfigurationException, SAXException {
                expectedCreateCase = xmlReader.xmlAttributeReader();
                ModelCase kase = tmMockUtils.getCaseById(expectedCreateCase[7]);
                Assert.assertEquals("caseId is not correct in TM as sent by RM", expectedCreateCase[7], kase.getId().toString());
                Assert.assertEquals("locality is not correct in TM as sent by RM", expectedCreateCase[8], kase.getAddress().getGeography().toString());
                Assert.assertEquals("UPRN is not correct in TM as sent by RM", expectedCreateCase[1], kase.getAddress().getUprn().toString());
                String addressRemoveOpenBrackets = kase.getAddress().getLines().toString().replace("[", "");
                String addressRemoveCloseBrackets = addressRemoveOpenBrackets.replace("]", "");
                address = addressRemoveCloseBrackets.split(",");
                Assert.assertEquals("Address line1 is not correct in TM as sent by RM", expectedCreateCase[5], address[0]);
                Assert.assertEquals("Postcode is not correct in TM as sent by RM", expectedCreateCase[6], kase.getAddress().getPostcode().toString());
                Assert.assertEquals("Type is not correct in TM as sent by RM", expectedCreateCase[2], kase.getType().toString());
                Assert.assertEquals("Establishment type is not correct in TM as sent by RM", expectedCreateCase[3], kase.getEstabType().toString());
        }

        @Given("a create case in the MQ with the missing ARID attribute")
        public void rmSendsAMissingARIDattributeCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
                RestTemplate restTemplate = new RestTemplate();
                receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingArribute/missingARIDCreateCaseInstruction.xml"), Charsets.UTF_8);
                ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
        }

        @And("the missing ARID attribute case is sent to dead letter queue")
        public void the_missing_ARID_attribute_case_is_sent_to_dead_letter_queue() {
                Assert.assertEquals(1, queueUtils.getMessageCount("Action.FieldDLQ"));
        }

    @Given("a create case in the MQ with the missing AddressLine1 attribute")
    public void rmSendsAMissingAddressLine1attributeCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingArribute/missingAddressLine1CreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing AddressLine1 attribute case is sent to dead letter queue")
    public void the_missing_AddressLine1_attribute_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(2, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing EstabType attribute")
    public void rmSendsAMissingEstabTypeattributeCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingArribute/missingEstabTypeCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing EstabType attribute case is sent to dead letter queue")
    public void the_missing_EstabType_attribute_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(3, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing Postcode attribute")
    public void rmSendsAMissingPostcodeattributeCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingArribute/missingPostcodeCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing Postcode attribute case is sent to dead letter queue")
    public void the_missing_Postcode_attribute_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(4, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing Type attribute")
    public void rmSendsAMissingTypeattributeCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingArribute/missingTypeCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing Type attribute case is sent to dead letter queue")
    public void the_missing_Type_attribute_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(5, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing UPRN attribute")
    public void rmSendsAMissingUPRNattributeCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingArribute/missingUPRNCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing UPRN attribute case is sent to dead letter queue")
    public void the_missing_UPRN_attribute_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(6, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing Locality attribute")
    public void rmSendsAMissingLocalityattributeCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingArribute/missingLocalityCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing Locality attribute case is sent to dead letter queue")
    public void the_missing_Locality_attribute_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(7, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing ARID tag")
    public void rmSendsAMissingARIDtagCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingTag/missingARIDCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing ARID tag case is sent to dead letter queue")
    public void the_missing_ARID_tag_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(8, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing AddressLine1 tag")
    public void rmSendsAMissingAddressLine1tagCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingTag/missingAddressLine1CreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing AddressLine1 tag case is sent to dead letter queue")
    public void the_missing_AddressLine1_tag_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(9, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing EstabType tag")
    public void rmSendsAMissingEstabTypetagCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingTag/missingEstabTypeCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing EstabType tag case is sent to dead letter queue")
    public void the_missing_EstabType_tag_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(10, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing Postcode tag")
    public void rmSendsAMissingPostcodetagCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingTag/missingPostcodeCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing Postcode tag case is sent to dead letter queue")
    public void the_missing_Postcode_tag_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(11, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing Type tag")
    public void rmSendsAMissingTypetagCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingTag/missingTypeCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing Type tag case is sent to dead letter queue")
    public void the_missing_Type_tag_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(12, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing UPRN tag")
    public void rmSendsAMissingUPRNtagCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingTag/missingUPRNCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing UPRN tag case is sent to dead letter queue")
    public void the_missing_UPRN_tag_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(13, queueUtils.getMessageCount("Action.FieldDLQ"));
    }

    @Given("a create case in the MQ with the missing Locality tag")
    public void rmSendsAMissingLocalitytagCreateHHJobRequest() throws IOException, ParserConfigurationException, SAXException {
        RestTemplate restTemplate = new RestTemplate();
        receivedRMMessage = Resources.toString(Resources.getResource("files/endToEndIntegrationTests/passHHInstructionsFromRMToTM/missingTag/missingLocalityCreateCaseInstruction.xml"), Charsets.UTF_8);
        ResponseEntity<Void> response = restTemplate.postForEntity(fooResourceUrl,receivedRMMessage, Void.class);
    }

    @And("the missing Locality tag case is sent to dead letter queue")
    public void the_missing_Locality_tag_case_is_sent_to_dead_letter_queue() {
        Assert.assertEquals(14, queueUtils.getMessageCount("Action.FieldDLQ"));
    }
}
