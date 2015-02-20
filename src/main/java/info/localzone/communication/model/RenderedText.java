package info.localzone.communication.model;

import java.util.Locale;

import org.springframework.context.MessageSource;

abstract class RenderedText implements Comparable<RenderedText>{
	@Override
	public int compareTo(RenderedText o) {
		
		return getDisplayText().compareTo(o.getDisplayText());
	}

	public RenderedText() {
		super();
	}

	String text;

	String displayText;


	

	public RenderedText(String text, MessageSource messageSource, Locale locale) {
		setText(text, messageSource, locale);

	}

	public String getDisplayText() {
		return displayText;
	}

	private void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	 String getText() {
		return text;
	}

	void setText(String text, MessageSource messageSource, Locale locale) {
		try {
			setDisplayText(messageSource.getMessage(text, new Object[] {}, locale));
		} catch (org.springframework.context.NoSuchMessageException e) {
			
			setDisplayText(text);
		}

		this.text = text;
	}


}
