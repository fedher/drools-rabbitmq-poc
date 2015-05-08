package com.sample;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.model.Room;
import com.model.Sprinkler;


public class DroolsMain {
	
    public static final void main(String[] args) {
    	KieRuntimeLogger runtimeLogger = null;
    	
        try {
            // Loads up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-rules");
        	
        	runtimeLogger = KieServices.Factory.get().getLoggers().newConsoleLogger(kSession);       	
        	
        	kSession.addEventListener(new DefaultAgendaEventListener() {
        		@Override
        		public void afterMatchFired(AfterMatchFiredEvent event) {
        			super.afterMatchFired(event);
        			System.out.println(event);
        			// Do something after a rule is matched ...
        		}
        	});
        	
            String[] names = new String[] { "kitchen", "bedroom", "office", "living" };
            Map<String, Room> name2room = new HashMap<String, Room>();

            for (String name: names) {
                Room room = new Room(name);
                name2room.put(name, room);
                kSession.insert(room);
                                
                Sprinkler sprinkler = new Sprinkler(room);
                kSession.insert(sprinkler);
            }
            
            kSession.fireAllRules();        	
        	
        	Consumer consumer = new Consumer(kSession, "rule.engine.feed", name2room);
        	consumer.start();        	
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
        	if (runtimeLogger != null) {
            	runtimeLogger.close();	
        	}        	
        }
    }

//    public void retractAll() {
//        for (FactHandle handle : ksession.getFactHandles()) {
//            retract(handle);
//        }
//    }
    
//    public void retractAll(ObjectFilter filter) {
//        for (FactHandle handle : ksession.getFactHandles(filter)) {
//            retract(handle);
//        }
//    }
    
}
