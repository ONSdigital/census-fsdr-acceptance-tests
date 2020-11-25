package uk.gov.ons.fsdr.tests.acceptance.steps;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import cucumber.api.java.en.Given;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertTrue;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;

@Slf4j
@PropertySource("classpath:application.properties")
public class CsvIngestSteps {

  @Autowired SftpUtils sftpUtils;

  @Autowired FsdrUtils fsdrUtils;

  @Value("${files.hq.hqingest}")
  private URI hqingestFiles;

  @Value("${sftp.directory.hq}")
  private String hqDirectory;

  @Given("A {string} ingest CSV {string} exists in SFTP")
  public void put_hq_create_csv_in_sftp(String source, String fileName) throws JSchException, SftpException, IOException {
    if(source.equals("HQ")) {
      sftpUtils.putFiletoSftp(hqDirectory, hqingestFiles.getPath() + fileName);
    } else {
      //TODO NISRA stuff
    }
  }

  @Given("we ingest the HQ CSV")
  public void we_ingest_the_HQ_CSV() throws IOException {
    fsdrUtils.ingestHqCsv();
    assertTrue(gatewayEventMonitor.hasEventTriggered("<N/A>", "HQ_INGEST_COMPLETE", 20000l));

  }
}
