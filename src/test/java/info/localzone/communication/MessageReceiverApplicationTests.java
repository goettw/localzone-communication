package info.localzone.communication;

import info.localzone.communication.MessageReceiverApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MessageReceiverApplication.class)
@WebAppConfiguration
public class MessageReceiverApplicationTests {

	@Test
	public void contextLoads() {
	}

}
