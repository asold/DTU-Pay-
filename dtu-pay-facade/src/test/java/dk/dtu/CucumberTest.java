package dk.dtu;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "features",
        glue = {"dk.dtu"},
        plugin = {"pretty", "html:target/cucumber-report.html"},
        monochrome = true
)
public class CucumberTest {
}
