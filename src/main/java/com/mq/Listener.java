package com.mq;

import org.kie.api.runtime.KieSession;

public interface Listener {

	void process(KieSession kSession, String message);
	
}
