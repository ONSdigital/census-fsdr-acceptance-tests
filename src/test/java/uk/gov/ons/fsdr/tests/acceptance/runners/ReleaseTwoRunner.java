package uk.gov.ons.fsdr.tests.acceptance.runners;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

  @RunWith(Cucumber.class)
  @CucumberOptions(plugin = {"pretty", "json:build/cucumber-report.json"},
          features = {"src/test/resources/acceptancetests/Release2.feature"},
          glue = {"uk.gov.ons.fsdr.tests.acceptance.release2.steps"})
  public class ReleaseTwoRunner {

  }
