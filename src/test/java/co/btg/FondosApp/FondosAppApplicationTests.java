package co.btg.FondosApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = FondosAppApplication.class)
@ActiveProfiles("local")
@TestPropertySource(properties = {
		"amazon.dynamodb.endpoint=http://localhost:8000/",
		"amazon.aws.accesskey=test1",
		"amazon.aws.secretkey=test231" })
class FondosAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
