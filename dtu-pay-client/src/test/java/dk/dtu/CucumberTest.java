package dk.dtu;


import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;

@RunWith(Cucumber.class)

@CucumberOptions(
		features = "features",
		glue = {"dk.dtu"},
		plugin = {"pretty", "html:target/cucumber-report.html"},
		monochrome = true
)
public class CucumberTest {
	@BeforeClass
	public static void setupLogger() {
		System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");
	}
}
