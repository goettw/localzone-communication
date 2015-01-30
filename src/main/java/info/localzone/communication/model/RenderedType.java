package info.localzone.communication.model;

import java.util.Locale;

import org.springframework.context.MessageSource;

public class RenderedType implements Comparable<RenderedType>{
	@Override
	public int compareTo(RenderedType o) {
		
		return getDisplayText().compareTo(o.getDisplayText());
	}

	public RenderedType() {
		super();
	}

	String type;
	int count;
	String displayText;


	

	public RenderedType(String type, MessageSource messageSource, Locale locale) {
		setType(type, messageSource, locale);

	}

	public String getDisplayText() {
		return displayText;
	}

	private void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public String getType() {
		return type;
	}

	public void setType(String type, MessageSource messageSource, Locale locale) {
		try {
			setDisplayText(messageSource.getMessage("placeType." + type, new Object[] {}, locale));
		} catch (org.springframework.context.NoSuchMessageException e) {
			
			setDisplayText(type);
		}

		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
