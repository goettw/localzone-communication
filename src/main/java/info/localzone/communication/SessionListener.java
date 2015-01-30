package info.localzone.communication;

import info.localzone.communication.model.HttpSessionBean;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;


public class SessionListener implements HttpSessionListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);
	@Autowired
	private StringRedisTemplate redisTemplate;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        LOGGER.debug("==== Session is created ====") ;
        event.getSession().setMaxInactiveInterval(20);
    }
 
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    	
    	LOGGER.debug("==== Session is destroyed start===" );   	   
    	HttpSession session = event.getSession();
    	
    	HttpSessionBean httpSessionBean = (HttpSessionBean)session.getAttribute("scopedTarget.httpSessionBean");
    	   if (httpSessionBean != null){		
    //		      	RedisUtils.setVisitor (redisTemplate, httpSessionBean.getGeoHash(), null);
    		    	LOGGER.debug("httpSessionBean found");
    	   }
    	
    }
}