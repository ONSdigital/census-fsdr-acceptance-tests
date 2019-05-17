package uk.gov.ons.census.fsdr.runners;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "json:build/cucumber-report.json"},
        features = {"src/test/resources/features"},
        glue = {"uk.gov.ons.census.fsdr.steps.endToEndIntegrationSteps"})
public class EndToEndIntergationRunner {
}
