package net.whydah.uss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.cantara.config.ApplicationProperties;
import no.cantara.stingray.application.StingrayApplicationFactory;

public class ApplicationFactory implements StingrayApplicationFactory<MainApplication> {
    private static final Logger log = LoggerFactory.getLogger(MainApplication.class);

    @Override
    public Class<?> providerClass() {
        return MainApplication.class;
    }

    @Override
    public String alias() {
        return "uss";
    }

    @Override
    public MainApplication create(ApplicationProperties config) {
        return new MainApplication(config);
    }
}