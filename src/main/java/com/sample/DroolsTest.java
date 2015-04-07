package com.sample;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

    public static final void main(String[] args) {
        try {        	
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-rules");
        	        	
            // go !            
            String[] names = new String[] {"kitchen", "bedroom", "office", "living"};

            Map<String, Room> name2room = new HashMap<String, Room>();

            for (String name: names) {
                Room room = new Room(name);
                name2room.put(name, room);
                kSession.insert(room);
                
                Sprinkler sprinkler = new Sprinkler(room);
                kSession.insert(sprinkler);
            }
            
            kSession.fireAllRules();
            
            TempWatcher watcher = new TempWatcher(name2room);
            watcher.start();            
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }        
    
}
