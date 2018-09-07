package ch.sbb.matsim.mobsim.qsim.pt;

import org.matsim.core.mobsim.qsim.AbstractQSimModule;

/**
 * @author mrieser / Simunto GmbH
 */
public class SBBTransitEngineModule extends AbstractQSimModule {
	public final static String TRANSIT_ENGINE_NAME = "SBBTransitEngine";

	@Override
	protected void configureQSim() {
		bind(SBBTransitQSimEngine.class).asEagerSingleton();

		bindDepartureHandler(TRANSIT_ENGINE_NAME).to(SBBTransitQSimEngine.class);
		bindAgentSource(TRANSIT_ENGINE_NAME).to(SBBTransitQSimEngine.class);
		bindMobsimEngine(TRANSIT_ENGINE_NAME).to(SBBTransitQSimEngine.class);
	}
}
