package uk.gov.ons.fsdr.tests.acceptance.runners;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "json:build/cucumber-report.json"},
    features = {"src/test/resources/acceptancetests/creates.feature"},
    glue = {"uk.gov.ons.fsdr.tests.acceptance.steps"})
public class CreateRunner {
}
