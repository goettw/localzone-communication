package info.localzone.communication.model;

import java.util.Locale;

import org.springframework.context.MessageSource;

public class RenderedChannel extends RenderedText{
	

	public RenderedChannel(String channel, MessageSource messageSource, Locale locale) {
		super(channel, messageSource, locale);
	}

	public String getChannel() {
		return super.getText();
	}
}
