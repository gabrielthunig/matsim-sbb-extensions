/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2018.
 */

package ch.sbb.matsim.mobsim.qsim;

import ch.sbb.matsim.config.SBBTransitConfigGroup;
import ch.sbb.matsim.mobsim.qsim.pt.SBBTransitEngineModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.mobsim.qsim.AbstractQSimModule;
import org.matsim.core.mobsim.qsim.ActivityEngineModule;
import org.matsim.core.mobsim.qsim.PopulationModule;
import org.matsim.core.mobsim.qsim.QSimProvider;
import org.matsim.core.mobsim.qsim.TeleportationModule;
import org.matsim.core.mobsim.qsim.changeeventsengine.NetworkChangeEventsModule;
import org.matsim.core.mobsim.qsim.components.QSimComponentsModule;
import org.matsim.core.mobsim.qsim.messagequeueengine.MessageQueueModule;
import org.matsim.core.mobsim.qsim.pt.ComplexTransitStopHandlerFactory;
import org.matsim.core.mobsim.qsim.pt.TransitStopHandlerFactory;
import org.matsim.core.mobsim.qsim.qnetsimengine.DefaultQNetworkFactory;
import org.matsim.core.mobsim.qsim.qnetsimengine.QLanesNetworkFactory;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetsimEngineModule;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetworkFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author mrieser / SBB
 */
public class SBBQSimModule extends AbstractModule {

    @Inject Config config ;

    @Override
    public void install() {
        install(new QSimComponentsModule());

        getQSimModules().forEach(this::installQSimModule);

        bind(Key.get(new TypeLiteral<List<AbstractQSimModule>>() {
        }, Names.named("overrides"))).toInstance(Collections.emptyList());

        bind(new TypeLiteral<Collection<AbstractQSimModule>>() {
        }).to(new TypeLiteral<Set<AbstractQSimModule>>() {
        });

        bind(Mobsim.class).toProvider(QSimProvider.class);
        if ( config.qsim().isUseLanes() ) {
            bind(QNetworkFactory.class).to( QLanesNetworkFactory.class ) ;
        } else {
            bind(QNetworkFactory.class).to( DefaultQNetworkFactory.class ) ;
        }

        bind(TransitStopHandlerFactory.class).to(ComplexTransitStopHandlerFactory.class).asEagerSingleton();

        // make sure the config is registered before the simulation starts
        // https://github.com/SchweizerischeBundesbahnen/matsim-sbb-extensions/issues/3
        ConfigUtils.addOrGetModule(getConfig(), SBBTransitConfigGroup.class);
    }

    // @SuppressWarnings("static-method")
    static public Collection<AbstractQSimModule> getQSimModules() {
        return Arrays.asList(
            new MessageQueueModule(),
            new ActivityEngineModule(),
            new QNetsimEngineModule(),
            new TeleportationModule(),
            new PopulationModule(),
            new NetworkChangeEventsModule(),
            new SBBTransitEngineModule()
        );
    }

}
